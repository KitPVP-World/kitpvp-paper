#!/bin/bash
set -euo pipefail
IFS=$'\n\t'

export SPIGET_RESOURCES=$1
: "${SPIGET_DOWNLOAD_TOLERANCE:=5}" # in minutes

acceptArgs=(--accept application/zip --accept application/java-archive --accept application/octet-stream)

log() {
  printf "\033[1;36mINFO \033[0m%s\n" "$1"
}

logError() {
  printf "\033[1;31mERROR \033[0;31mWhile trying to download from Spiget: %s\033[0m\n" "$1"
}

isTrue() {
  case "${1,,}" in
  true | yes | on | 1)
    return 0
    ;;
  *)
    return 1
    ;;
  esac
}

get_silent() {
  local flags=(-s)
  if isTrue "${DEBUG_GET:-false}"; then
    flags+=("--debug")
  fi
  mc-image-helper "${flags[@]}" get "$@"
}

get() {
  mc-image-helper get "$@"
}

containsJars() {
  file=${1?}

  pat='\.jar$'

  while read -r line; do
    if [[ $line =~ $pat ]]; then
      return 0
    fi
  done < <(unzip -l "$file" | tail -n +4)

  return 1
}

containsPlugin() {
  file=${1?}

  pat='plugin.yml$'

  while read -r line; do
    if [[ $line =~ $pat ]]; then
      return 0
    fi
  done < <(unzip -l "$file" | tail -n +4)

  return 1
}

downloadResourceFromSpiget() {
  resource=${1?}

  tempDir="/tmp/download-spiget/tmp-${resource}"
  mkdir -p "$tempDir"
  resourceUrl="https://api.spiget.org/v2/resources/${resource}"
  if ! outfile=$(get_silent --output-filename -o "$tempDir" "${acceptArgs[@]}" "${resourceUrl}/download"); then
    echo "Failed to download resource '${resource}' from ${resourceUrl}/download"
    if externalUrl=$(get --json-path '$.file.externalUrl' "${resourceUrl}"); then
      logError "       Visit $externalUrl to pre-download the resource"
      logError "       instead of using SPIGET_RESOURCES"
    fi
    exit 1
  fi

  if ! fileType=$(get --json-path '.file.type' "${resourceUrl}"); then
    logError "Failed to retrieve file type of resource $resource"
    exit 1
  fi
  if [[ $fileType = .sk ]]; then
    mkdir -p /server/plugins/Skript/scripts
    mv "$outfile" /server/plugins/Skript/scripts
  else
    if containsPlugin "${outfile}"; then
      echo "Moving resource ${resource} into plugins"
      mv "$outfile" /server/plugins
    elif containsJars "${outfile}"; then
      echo "Extracting contents of resource ${resource} into plugins"
      extract "$outfile" /server/plugins
    else
      logError "File for resource ${resource} has an unexpected file type: ${fileType}"
      exit 2
    fi
  fi
  rm -rf "$tempDir"
}

if [[ ${SPIGET_RESOURCES} ]]; then
  log "Getting plugins via Spiget"
  IFS=',' read -r -a resources <<<"${SPIGET_RESOURCES}"
  mkdir -p /server/plugins
  for resource in "${resources[@]}"; do
    log "Downloading resource ${resource} ..."

    downloadResourceFromSpiget "${resource}"
  done
fi

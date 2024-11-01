#!/bin/bash

set -euo pipefail

: "${MODRINTH_DOWNLOAD_DEPENDENCIES:=required}"
: "${MODRINTH_ALLOWED_VERSION_TYPE:=release}"

if ! [[ -v MINECRAFT_VERSION ]]; then
  log "ERROR: plugins via 'download-modrinth.sh' require MINECRAFT_VERSION to be set to corresponding Minecraft game version"
  exit 1
fi

mc-image-helper modrinth \
  --output-directory=/data/plugins \
  --projects="${MODRINTH_PROJECTS}" \
  --game-version="${MINECRAFT_VERSION}" \
  --loader="paper" \
  --download-dependencies="${MODRINTH_DOWNLOAD_DEPENDENCIES}" \
  --allowed-version-type="${MODRINTH_ALLOWED_VERSION_TYPE}"
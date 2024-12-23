#!/bin/bash

set -euo pipefail

if id ubuntu > /dev/null 2>&1; then
  deluser ubuntu
fi

addgroup --gid 1000 minecraft

adduser --system --shell /bin/false --uid 1000 --ingroup minecraft --home /server minecraft

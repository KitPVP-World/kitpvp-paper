#!/bin/bash

export TARGET

set -euo pipefail

# Update and install packages
apt-get update
# shellcheck disable=SC2086
DEBIAN_FRONTEND=noninteractive \
apt-get install -y \
  imagemagick \
  file \
  gosu \
  sudo \
  net-tools \
  iputils-ping \
  curl \
  git \
  jq \
  dos2unix \
  mysql-client \
  tzdata \
  rsync \
  nano \
  unzip \
  zstd \
  lbzip2 \
  nfs-common \
  libpcap0.8 \
  ${EXTRA_DEB_PACKAGES}

# Install Git LFS
curl -s https://packagecloud.io/install/repositories/github/git-lfs/script.deb.sh | sudo bash
apt-get update
apt-get install -y git-lfs

# Clean up APT when done
apt-get clean

# Set git credentials globally
cat <<EOF >> /etc/gitconfig
[user]
	name = Minecraft Server on Docker
	email = server@example.com
EOF
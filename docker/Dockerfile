FROM azul/zulu-openjdk:21-jre

# hook into docker BuildKit --platform support
# see https://docs.docker.com/engine/reference/builder/#automatic-platform-args-in-the-global-scope
ARG TARGETOS
ARG TARGETARCH
ARG TARGETVARIANT

# the version was not set to the right one automatically
ENV VERSION="1.21.7"
ENV MINECRAFT_VERSION="${VERSION}"

COPY data/scripts/install-packages.sh data/scripts/setup-users.sh /setup/

RUN /setup/install-packages.sh
RUN /setup/setup-users.sh

ARG APPS_REV=1
ARG GITHUB_BASEURL=https://github.com

ARG EASY_ADD_VERSION=0.8.11
ADD ${GITHUB_BASEURL}/itzg/easy-add/releases/download/${EASY_ADD_VERSION}/easy-add_${TARGETOS}_${TARGETARCH}${TARGETVARIANT} /usr/bin/easy-add
RUN chmod +x /usr/bin/easy-add

ARG MC_MONITOR_VERSION=0.15.4
RUN easy-add --var os=${TARGETOS} --var arch=${TARGETARCH}${TARGETVARIANT} \
  --var version=${MC_MONITOR_VERSION} --var app=mc-monitor --file {{.app}} \
  --from ${GITHUB_BASEURL}/itzg/{{.app}}/releases/download/{{.version}}/{{.app}}_{{.version}}_{{.os}}_{{.arch}}.tar.gz

ARG MC_HELPER_VERSION=1.46.0
ARG MC_HELPER_BASE_URL=${GITHUB_BASEURL}/itzg/mc-image-helper/releases/download/${MC_HELPER_VERSION}
# used for cache busting local copy of mc-image-helper
ARG MC_HELPER_REV=1
RUN curl -fsSL ${MC_HELPER_BASE_URL}/mc-image-helper-${MC_HELPER_VERSION}.tgz \
  | tar -C /usr/share -zxf - \
  && ln -s /usr/share/mc-image-helper-${MC_HELPER_VERSION}/bin/mc-image-helper /usr/bin

USER 1000:1000

WORKDIR /plugins
WORKDIR /config
WORKDIR /data
WORKDIR /server/plugins
WORKDIR /server/config
WORKDIR /server/libraries

ADD data/config /config
ADD data/plugins /plugins
ADD data/data /data

WORKDIR /server

STOPSIGNAL SIGTERM

ADD ../kitpvpslime-bundler-*-mojmap.jar ./kitpvp-paper.jar

ADD data/scripts/download-modrinth.sh data/scripts/download-spiget.sh data/scripts/optimize-startup.sh ./
ADD data/scripts/mc-health.sh /health.sh

RUN ./optimize-startup.sh # cache

EXPOSE 25565

ENV MEMORY=2048

CMD cp -r /plugins/* /server/plugins && cp -r /config/* /server/config && cp -r /data/* /server && \
    java -Xms${MEMORY}M -Xmx${MEMORY}M \
    -Dfile.encoding=UTF-8 \
    -XX:+AlwaysPreTouch -XX:+DisableExplicitGC -XX:+ParallelRefProcEnabled -XX:+PerfDisableSharedMem -XX:+UnlockExperimentalVMOptions -XX:+UseG1GC -XX:G1HeapRegionSize=8M -XX:G1HeapWastePercent=5 -XX:G1MaxNewSizePercent=40 -XX:G1MixedGCCountTarget=4 -XX:G1MixedGCLiveThresholdPercent=90 -XX:G1NewSizePercent=30 -XX:G1RSetUpdatingPauseTimePercent=5 -XX:G1ReservePercent=20 -XX:InitiatingHeapOccupancyPercent=15 -XX:MaxGCPauseMillis=200 -XX:MaxTenuringThreshold=1 -XX:SurvivorRatio=32 -Dusing.aikars.flags=https://mcflags.emc.gs -Daikars.new.flags=true \
    -jar kitpvp-paper.jar nogui

HEALTHCHECK --start-period=2m --retries=2 --interval=30s CMD /health.sh

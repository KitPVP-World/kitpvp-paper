FROM gradle:jdk21-jammy AS build

#RUN apk add --no-cache git
#RUN apk add --no-cache openssh

USER gradle

WORKDIR /home/gradle

ARG CACHEBUST=1
RUN git clone https://github.com/KitPvP-World/kitpvp-paper -b main /home/gradle/kitpvp-paper

WORKDIR /home/gradle/kitpvp-paper

RUN rm settings.gradle.kts
RUN mv settings.gradle-docker.kts settings.gradle.kts

WORKDIR /home/gradle/kitpvp-paper/

RUN git config --global user.email "no-reply@kitpvp.world"
RUN git config --global user.name "KitPvP Building"
RUN gradle applyPatches --stacktrace --no-daemon
RUN gradle build --stacktrace --no-daemon
#RUN gradle createMojmapPaperclipJar --stacktrace --no-daemon

FROM azul/zulu-openjdk:21-jre

# hook into docker BuildKit --platform support
# see https://docs.docker.com/engine/reference/builder/#automatic-platform-args-in-the-global-scope
ARG TARGETOS
ARG TARGETARCH
ARG TARGETVARIANT

# the version was not set to the right one automatically
ENV VERSION="1.21.1"
ENV MINECRAFT_VERSION="${VERSION}"

COPY --from=build /home/gradle/kitpvp-paper/docker-data/scripts/install-packages.sh /setup/install-packages.sh
COPY --from=build /home/gradle/kitpvp-paper/docker-data/scripts/setup-users.sh /setup/setup-users.sh

RUN /setup/install-packages.sh
RUN /setup/setup-users.sh

ARG APPS_REV=1
ARG GITHUB_BASEURL=https://github.com

ARG EASY_ADD_VERSION=0.8.8
ADD ${GITHUB_BASEURL}/itzg/easy-add/releases/download/${EASY_ADD_VERSION}/easy-add_${TARGETOS}_${TARGETARCH}${TARGETVARIANT} /usr/bin/easy-add
RUN chmod +x /usr/bin/easy-add

ARG MC_MONITOR_VERSION=0.14.1
RUN easy-add --var os=${TARGETOS} --var arch=${TARGETARCH}${TARGETVARIANT} \
  --var version=${MC_MONITOR_VERSION} --var app=mc-monitor --file {{.app}} \
  --from ${GITHUB_BASEURL}/itzg/{{.app}}/releases/download/{{.version}}/{{.app}}_{{.version}}_{{.os}}_{{.arch}}.tar.gz

ARG MC_HELPER_VERSION=1.39.11
ARG MC_HELPER_BASE_URL=${GITHUB_BASEURL}/itzg/mc-image-helper/releases/download/${MC_HELPER_VERSION}
# used for cache busting local copy of mc-image-helper
ARG MC_HELPER_REV=1
RUN curl -fsSL ${MC_HELPER_BASE_URL}/mc-image-helper-${MC_HELPER_VERSION}.tgz \
  | tar -C /usr/share -zxf - \
  && ln -s /usr/share/mc-image-helper-${MC_HELPER_VERSION}/bin/mc-image-helper /usr/bin

USER 1000:1000

WORKDIR /data

STOPSIGNAL SIGTERM

RUN mkdir -p /data/plugins
RUN mkdir -p /data/libraries
RUN mkdir -p /data/config

COPY --from=build /home/gradle/kitpvp-paper/docker-data/config ./config
COPY --from=build /home/gradle/kitpvp-paper/docker-data/plugins ./plugins
COPY --from=build /home/gradle/kitpvp-paper/docker-data/data .
COPY --from=build /home/gradle/kitpvp-paper/kitpvpslime-server/build/libs/kitpvpslime-server-*-mojang-mapped.jar ./kitpvp-paper.jar
COPY --from=build --chmod=755 /home/gradle/kitpvp-paper/docker-data/scripts/download-modrinth.sh .
COPY --from=build --chmod=755 /home/gradle/kitpvp-paper/docker-data/scripts/mc-health.sh /health.sh

EXPOSE 25565

ENV MEMORY=2048

CMD java -Xms${MEMORY}M -Xmx${MEMORY}M \
    -Dfile.encoding=UTF-8 \
    # Akair's Flags
    -XX:+AlwaysPreTouch -XX:+DisableExplicitGC -XX:+ParallelRefProcEnabled -XX:+PerfDisableSharedMem -XX:+UnlockExperimentalVMOptions -XX:+UseG1GC -XX:G1HeapRegionSize=8M -XX:G1HeapWastePercent=5 -XX:G1MaxNewSizePercent=40 -XX:G1MixedGCCountTarget=4 -XX:G1MixedGCLiveThresholdPercent=90 -XX:G1NewSizePercent=30 -XX:G1RSetUpdatingPauseTimePercent=5 -XX:G1ReservePercent=20 -XX:InitiatingHeapOccupancyPercent=15 -XX:MaxGCPauseMillis=200 -XX:MaxTenuringThreshold=1 -XX:SurvivorRatio=32 -Dusing.aikars.flags=https://mcflags.emc.gs -Daikars.new.flags=true \
    -jar kitpvp-paper.jar nogui

HEALTHCHECK --start-period=2m --retries=2 --interval=30s CMD mc-health
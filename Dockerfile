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
RUN gradle createMojmapPaperclipJar --stacktrace --no-daemon

FROM itzg/minecraft-server

COPY --from=build /home/gradle/kitpvp-paper/docker-data/config /config
COPY --from=build /home/gradle/kitpvp-paper/docker-data/plugins /plugins
COPY --from=build /home/gradle/kitpvp-paper/docker-data/data /data

WORKDIR /data

COPY --from=build /home/gradle/kitpvp-paper/build/libs/kitpvp-slime-paperclip-*-mojmap.jar ./kitpvp-paper.jar

RUN java -Dpaperclip.patchonly=true -jar ./kitpvp-paper.jar # cache

ENV EULA=true
ENV TYPE="PAPER"
ENV USE_AIKAR_FLAGS=true
ENV PAPER_CUSTOM_JAR="/data/kitpvp-paper.jar"
ENV REPLACE_ENV_VARIABLES="TRUE"
ENV ENV_VARIABLE_PREFIX="CFG_"
ENV CFG_VELOCITY_ENABLED=true

# the version was not set to the right one automatically
ENV VERSION="1.21.1"

# There was an issue with permissions for the libraries; This seems to fix it
RUN chmod -R 777 /data/libraries
RUN chown 1000:1000 -R /data/libraries
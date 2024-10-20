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

WORKDIR /data

COPY --from=build /home/gradle/kitpvp-paper/build/libs/kitpvp-slime-paperclip-*-mojmap.jar ./kitpvp-paper.jar

RUN java -Dpaperclip.patchonly=true -jar ./kitpvp-paper.jar # cache

ENV EULA=true
ENV TYPE="CUSTOM"
ENV USE_AIKAR_FLAGS=truepl
ENV CUSTOM_JAR_EXEC="-jar kitpvp-paper.jar"
ENV REPLACE_ENV_VARIABLES="TRUE"
ENV ENV_VARIABLE_PREFIX="CFG_"
ENV CFG_VELOCITY_ENABLED=true
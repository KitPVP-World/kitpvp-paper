pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
}

rootProject.name = "kitpvp-slime"

include("kitpvpslime-api", "kitpvpslime-server")
include("api", "core")
include("test-plugin")
include("kitpvpslime-api-all")
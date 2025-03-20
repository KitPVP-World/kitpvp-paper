pluginManagement {
    includeBuild("asp-build-logic")

    repositories {
        gradlePluginPortal()
        mavenLocal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

rootProject.name = "kitpvp-slime"

include(":api")
project(":api").projectDir = file("asp-api")
include(":core")
project(":core").projectDir = file("asp-core")
include(":kitpvpslime-api")
//include(":kitpvpslime-server")

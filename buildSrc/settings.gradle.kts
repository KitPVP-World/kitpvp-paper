rootProject.name = "buildSrc"

pluginManagement {
    repositories {
        maven("https://repo.papermc.io/repository/maven-public/")
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

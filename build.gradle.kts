import io.papermc.paperweight.util.Git

plugins {
    java
    `maven-publish`
    id("org.kordamp.gradle.profiles") version "0.47.0" // required by aswm - keep version sync
    id("com.gradleup.shadow") version "8.3.0" apply false

    // In general, keep this version in sync with upstream. Sometimes a newer version than upstream might work, but an older version is extremely likely to break.
    id("io.papermc.paperweight.patcher") version "1.7.1"
}

val paperMavenPublicUrl = "https://repo.papermc.io/repository/maven-public/"

repositories {
    mavenCentral()
    maven(paperMavenPublicUrl) {
        content { onlyForConfigurations(configurations.paperclip.name) }
    }
}

dependencies {
    remapper("net.fabricmc:tiny-remapper:0.10.3:fat") // Must be kept in sync with upstream
    decompiler("org.vineflower:vineflower:1.10.1") // Must be kept in sync with upstream
    paperclip("io.papermc:paperclip:3.0.3") // You probably want this to be kept in sync with upstream
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }
}

subprojects {
    tasks.withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release = 21
    }
    tasks.withType<Javadoc> {
        options.encoding = Charsets.UTF_8.name()
    }
    tasks.withType<ProcessResources> {
        filteringCharset = Charsets.UTF_8.name()
    }

    repositories {
        mavenCentral()
        maven(paperMavenPublicUrl)
        maven("https://repo.infernalsuite.com/repository/maven-snapshots/")
        maven("https://repo.rapture.pw/repository/maven-releases/")
    }
}

val paperDir = layout.projectDirectory.dir("work/AdvancedSlimePaper")
val initSubmodules by tasks.registering {
    outputs.upToDateWhen { false }
    doLast {
        Git(layout.projectDirectory)("submodule", "update", "--init").executeOut()
    }
}

paperweight {
    serverProject = project(":kitpvpslime-server")

    remapRepo = paperMavenPublicUrl
    decompileRepo = paperMavenPublicUrl

    upstreams {
        register("slimeworldmanager") {
            upstreamDataTask {
                dependsOn(initSubmodules)
                projectDir = paperDir
            }

            patchTasks {
                register("api") {
                    upstreamDir = paperDir.dir("slimeworldmanager-api")
                    patchDir = layout.projectDirectory.dir("patches/api")
                    outputDir = layout.projectDirectory.dir("kitpvpslime-api")
                }

                register("server") {
                    upstreamDir = paperDir.dir("slimeworldmanager-server")
                    patchDir = layout.projectDirectory.dir("patches/server")
                    outputDir = layout.projectDirectory.dir("kitpvpslime-server")
                    importMcDev = true
                }

                register("core") {
                    isBareDirectory = true
                    upstreamDir = paperDir.dir("core")
                    patchDir = layout.projectDirectory.dir("patches/core")
                    outputDir = layout.projectDirectory.dir("core")
                }

                register("aswmApi") {
                    isBareDirectory = true
                    upstreamDir = paperDir.dir("api")
                    patchDir = layout.projectDirectory.dir("patches/aswmApi")
                    outputDir = layout.projectDirectory.dir("api")
                }

                register("generatedApi") {
                    isBareDirectory.set(true)
                    upstreamDir = paperDir.dir("paper-api-generator/generated")
                    patchDir = layout.projectDirectory.dir("patches/generatedApi")
                    outputDir = layout.projectDirectory.dir("paper-api-generator/generated")
                }
            }
        }
    }
}

//
// Everything below here is optional if you don't care about publishing API or dev bundles to your repository
//

tasks.generateDevelopmentBundle {
    apiCoordinates = "world.kitpvp.kitpvpslime:kitpvpslime-api"
    libraryRepositories = listOf(
        "https://repo.maven.apache.org/maven2/",
        paperMavenPublicUrl,
        "https://maven.kitpvp.world/snapshots/", // This should be a repo hosting your API (in this example, 'world.kitpvp.kitpvpslime:kitpvpslime-api')
    )
}

allprojects {
    // Publishing API:
    // ./gradlew :kitpvpslime-api:publish[ToMavenLocal]
    publishing {
        repositories {
            maven {
                name = "kitpvp"
                url = uri("https://maven.kitpvp.world/snapshots/")
                // See Gradle docs for how to provide credentials to PasswordCredentials
                // https://docs.gradle.org/current/samples/sample_publishing_credentials.html
                credentials(PasswordCredentials::class)
            }
        }
    }
}

publishing {
    // Publishing dev bundle:
    // ./gradlew publishDevBundlePublicationTo(MavenLocal|MyRepoSnapshotsRepository) -PpublishDevBundle
    if (project.hasProperty("publishDevBundle")) {
        publications.create<MavenPublication>("devBundle") {
            artifact(tasks.generateDevelopmentBundle) {
                artifactId = "dev-bundle"
            }
        }
    }
}

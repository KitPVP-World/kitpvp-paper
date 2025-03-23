import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("io.papermc.paperweight.patcher")
}

paperweight {
    upstreams.register("asp") {
        repo = github("InfernalSuite", "AdvancedSlimePaper")
        ref = providers.gradleProperty("aspRef")

        patchDir("aspApi") {
            upstreamPath = "api"
            patchesDir = file("asp-patches/api")
            outputDir = file("asp-api")
        }
        patchDir("aspCore") {
            upstreamPath = "core"
            patchesDir = file("asp-patches/core")
            outputDir = file("asp-core")
        }
        patchFile {
            path = "impl/aspaper-server/build.gradle.kts"
            outputFile = file("kitpvpslime-server/build.gradle.kts")
            patchFile = file("kitpvpslime-server/build.gradle.kts.patch")
        }
        patchFile {
            path = "impl/aspaper-api/build.gradle.kts"
            outputFile = file("kitpvpslime-api/build.gradle.kts")
            patchFile = file("kitpvpslime-api/build.gradle.kts.patch")
        }
        patchRepo("paperApi") {
            upstreamPath = "impl/paper-api/"
            excludes = setOf("build.gradle.kts")
            patchesDir = file("kitpvpslime-api/paper-patches")
            outputDir = file("kitpvpslime-api")
        }
    }
}

val paperMavenPublicUrl = "https://repo.papermc.io/repository/maven-public/"

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")

    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }

    repositories {
        mavenCentral()
        maven(paperMavenPublicUrl)
    }

    tasks.withType<AbstractArchiveTask>().configureEach {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }
    tasks.withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release = 21
        options.isFork = true
    }
    tasks.withType<Javadoc> {
        options.encoding = Charsets.UTF_8.name()
    }
    tasks.withType<ProcessResources> {
        filteringCharset = Charsets.UTF_8.name()
    }
    tasks.withType<Test> {
        testLogging {
            showStackTraces = true
            exceptionFormat = TestExceptionFormat.FULL
            events(TestLogEvent.STANDARD_OUT)
        }
    }

    extensions.configure<PublishingExtension> {
        repositories {
            /*
            maven("https://repo.papermc.io/repository/maven-snapshots/") {
                name = "paperSnapshots"
                credentials(PasswordCredentials::class)
            }
             */
        }
    }
}

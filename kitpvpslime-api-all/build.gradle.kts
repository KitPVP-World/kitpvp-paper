plugins {
    `java-library`
    `maven-publish`
    id("com.gradleup.shadow")
}

description = "A bundle for the paper & asp api"

java {
    withSourcesJar()
    withJavadocJar()
}

val shadePaper: Configuration by configurations.creating {
    isTransitive = false
}

val projectsToShade = setOf(
    project(":api"),
    project(":core"),
    project(":kitpvpslime-api")
)

projectsToShade.forEach {
    configurations.api.get().extendsFrom(it.configurations.api.get())
    configurations.compileOnlyApi.get().extendsFrom(it.configurations.compileOnlyApi.get())
    configurations.implementation.get().extendsFrom(it.configurations.implementation.get())
    configurations.runtimeClasspath.get().extendsFrom(it.configurations.runtimeClasspath.get())
}

dependencies {
    for (otherProject in projectsToShade) {
        shadePaper(otherProject)
    }
}

// copied from :kitpvpslime-api:build.gradle.kts - added by Paper for the Brigadier api
val outgoingVariants = arrayOf("runtimeElements", "apiElements", "sourcesElements", "javadocElements")
configurations {
    val outgoing = outgoingVariants.map { named(it) }
    for (config in outgoing) {
        config {
            outgoing {
                capability("${project.group}:${project.name}:${project.version}")
                capability("${project.group}:${project(":kitpvpslime-api").name}:${project.version}")
                capability("io.papermc.paper:paper-mojangapi:${project.version}")
                capability("com.destroystokyo.paper:paper-mojangapi:${project.version}")
            }
        }
    }
}

tasks {
    named<Jar>("sourcesJar") {
        projectsToShade.forEach {
            from(it.sourceSets.main.get().allSource)
        }
    }
    withType<Javadoc> {
        val options = options as StandardJavadocDocletOptions
        options.overview = "src/main/javadoc/overview.html"
        options.use()
        options.isDocFilesSubDirs = true

        projectsToShade.forEach {
            dependsOn(it.tasks.javadoc)

            fixPaperJavadocs(options, it) // paper has a custom javadoc configuration which we'll also need to clone

            source(it.sourceSets.main.get().allJava)
            classpath += it.sourceSets.main.get().compileClasspath
        }
    }

    build {
        dependsOn(shadowJar)
    }

    val generateApiVersioningFile by registering {
        inputs.property("version", project.version)
        val pomProps = layout.buildDirectory.file("pom.properties")
        outputs.file(pomProps)
        val projectVersion = project.version
        doLast {
            pomProps.get().asFile.writeText("version=$projectVersion")
        }
    }

    shadowJar {
        archiveClassifier = ""
        configurations = listOf(shadePaper)

        from(generateApiVersioningFile.map { it.outputs.files.singleFile }) {
            into("META-INF/maven/${project.group}/${project.name}")
        }
        manifest {
            attributes(
                "Automatic-Module-Name" to "org.bukkit"
            )
        }
    }
}

publishing {
    publications.create<MavenPublication>("maven") {
        outgoingVariants.forEach {
            suppressPomMetadataWarningsFor(it)
        }

        from(components["java"])
    }
}

fun Javadoc.fixPaperJavadocs(options: StandardJavadocDocletOptions, subproject: Project) {
    val optionsToClone = subproject.tasks.javadoc.get().options as StandardJavadocDocletOptions
    options.links(*optionsToClone.links!!.toTypedArray())
    options.tags(optionsToClone.tags)

    val apiAndDocs = subproject.configurations.findByName("apiAndDocs")
    if(apiAndDocs != null) {
        inputs.files(apiAndDocs).ignoreEmptyDirectories().withPropertyName(apiAndDocs.name + "-configuration")
        doFirst {
            options.addStringOption(
                "sourcepath",
                apiAndDocs.resolvedConfiguration.files.joinToString(separator = File.pathSeparator, transform = File::getPath)
            )
        }
    }

    // workaround for https://github.com/gradle/gradle/issues/4046
    val javadocSourceSetDirectory = subproject.projectDir.resolve("src/main/javadoc")
    if(javadocSourceSetDirectory.exists()) {
        inputs.dir(javadocSourceSetDirectory).withPropertyName("javadoc-${subproject.name}-sourceset")
    }
    doLast {
        copy {
            from(subproject.projectDir.resolve("src/main/javadoc")) {
                include("**/doc-files/**")
            }
            into("build/docs/javadoc")
        }
    }
}



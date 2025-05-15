val gitCommitProvider = providers.of(com.infernalsuite.asp.conventions.GitCommitValueSource::class) {}

version = "${
    rootProject.providers.gradleProperty("apiVersion").get()
}-${gitCommitProvider.orNull ?: "SNAPSHOT"}"

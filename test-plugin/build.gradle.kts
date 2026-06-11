version = "1.0.0-SNAPSHOT"

repositories {
//    maven("https://libraries.minecraft.net")
}

dependencies {
    compileOnly(project(":kitpvpslime-api"))
    compileOnly(project(":kitpvpslime-server"))
    compileOnly("ca.spottedleaf:concurrentutil:0.0.10")
    compileOnly(libs.annotations)
    compileOnly("com.mojang:datafixerupper:9.0.19")

}

tasks.processResources {
    val apiVersion = rootProject.providers.gradleProperty("mcVersion").get()
    val props = mapOf(
        "version" to project.version,
        "apiversion" to "\"$apiVersion\"",
    )
    inputs.properties(props)
    filesMatching("paper-plugin.yml") {
        expand(props)
    }
}

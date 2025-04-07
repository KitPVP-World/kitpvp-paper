# See PaperMC
https://github.com/PaperMC/Paper/blob/main/CONTRIBUTING.md

# Project Specifics
- **Applying all patches**: `./gradlew applyAllPatches`
- **Rebuilding file patches** (i.e., for build.gradle.kts in server or api): `./gradlew rebuildAspaperSingleFilePatches`
- **Making file changes to the server/api**: 
  - asp-api: `./gradlew fixupAspApiFilePatches rebuildAspApiFilePatches`
  - asp-core: `./gradlew fixupAspCoreFilePatches rebuildAspCoreFilePatches`
  - kitpvpslime-server (minecraft): `./gradlew fixupMinecraftSourcePatches rebuildMinecraftSourcePatches`
  - kitpvpslime-api (own api): no patching needed

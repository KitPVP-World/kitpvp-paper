# KitPvP Slime - A AdvancedSlimePaper fork with kitpvp patches
Changes related to async performance and security within arena worlds

## Paperweight

The files of most interest are
- build.gradle.kts
- settings.gradle.kts
- gradle.properties

When updating upstream, be sure to keep the dependencies noted in `build.gradle.kts` in sync with upstream.
It's also a good idea to use the same version of the Gradle wrapper as upstream.

## Tasks

```
Paperweight tasks
-----------------
yxapplyApiPatches
applyPatches
applyServerPatches
cleanCache - Delete the project setup cache and task outputs.
createMojmapBundlerJar - Build a runnable bundler jar
createMojmapPaperclipJar - Build a runnable paperclip jar
createReobfBundlerJar - Build a runnable bundler jar
createReobfPaperclipJar - Build a runnable paperclip jar
generateDevelopmentBundle
rebuildApiPatches
rebuildPatches
rebuildServerPatches
reobfJar - Re-obfuscate the built jar to obf mappings
runDev - Spin up a non-relocated Mojang-mapped test server
runReobf - Spin up a test server from the reobfJar output jar
runShadow - Spin up a test server from the shadowJar archiveFile
```
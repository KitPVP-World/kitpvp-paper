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
applyApiPatches
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

## Docker

###  Docker Run (from locally built image)
```bash
docker run -it --rm -p 25565 kitpvp-paper:latest
```

### Docker Build

#### Build from `main` branch
```bash
docker build -t kitpvp-paper:latest . --build-arg CACHEBUST=$(git log -n 1 --pretty=format:"%H" origin/main)
```

#### Build from `develop` branch
```bash
docker build -t kitpvp-paper:latest . --build-arg CACHEBUST=$(git log -n 1 --pretty=format:"%H" origin/develop)
```
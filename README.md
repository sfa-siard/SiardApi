> **⚠️ ARCHIVED REPOSITORY**  
> This repository is archived and no longer maintained. All development has moved to the new monorepo:  
> **https://github.com/sfa-siard/siard-suite**  
> 
> Please create any issues or pull requests in the new monorepo, which now contains all submodules including this one.

# SiardApi - SIARD 2.2 API
This package contains an API for reading and writing files in the SIARD Format 2.2.

## Getting started (for developers)
For building the binaries, Java JDK 17 must be installed.

### Build the project
```bash
./gradlew clean build
```

### Versioning, tags, and releases
Versions and tags are managed with the [Axion Release Plugin](https://github.com/allegro/axion-release-plugin) for Gradle.

Short overview:
```shell
./gradlew currentVersion  # Shows the current version

./gradlew release         # Creates a new release, adds a tag, and pushes it to remote
```

## Documentation
- [User Manual](https://github.com/sfa-siard/siard-suite/blob/main/docs/user-manual/en/user-manual.adoc)
- [Software Architecture Document](https://github.com/sfa-siard/siard-suite/blob/main/docs/sad/sad.adoc)

## Declaration
Contributions to the codebase have been made with the support of Windsurf. Windsurf is AI-powered code completion tool, that is trained exclusively on natural language and source code data with [permissive licenses](https://windsurf.com/blog/copilot-trains-on-gpl-codeium-does-not). 







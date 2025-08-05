# SiardApi - An API for reading and writing files in the SIARD Format 2.2

## Getting started

Build the project and run all tests

```bash
./gradlew clean build
```


Create a release

```bash
./gradlew release
```

This adds a new tag and pushes it to the repository.

## Usage


add the following to settings.gradle.kts:

```kotlin
    gitRepository(URI.create("https://github.com/sfa-siard/SiardApi")) {
        producesModule("ch.admin.bar:siard-api")
    }
```

and add the dependency in build.gradle.kts to your other dependencies:

```kotlin
    implementation("ch.admin.bar:siard-api:v2.2.126") // check for the latest version
```






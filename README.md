# SiardApi - An API for reading and writing files in the SIARD Format 2.1

This package contains an API for reading and writing files in the
SIARD Format 2.1

## Getting started (for devs)

For building the binaries, Java JDK (1.8 or higher), Ant, and Git must
have been installed.

Rename build.properties.template to build.properties and change the configuration to your needs - a sensible default is provided that should work on unix systems.


Generate Siard 2.1 classes from [metadata.xsd](./doc/specifications/metadata.xsd):
```bash
ant generate
```

Run all tests:
```bash
ant test
```

Create a release

```bash
ant deploy
```

this target also:
* runs all tests.
* copies `siardapi.jar` to the `dist` folder
* updates [MANIFEST.MF](src/META-INF/MANIFEST.MF)
* makes a git commit (if git is configure in `build.properties`)


More information about the build process can be found in
[doc/manual/developer/build.html](./doc/manual/developer/build.html).


## IDEs

No specific IDE is needed. 

The project can be opened in Eclipse. But use of Eclipse is optional.


## Documentation
[./doc/manual/user/index.html](./doc/manual/user/index.html) contains the manual for using the binaries.
[./doc/manual/developer/index.html](./doc/manual/developer/index.html) is the manual for developers wishing to build the binaries or work on the code.

# SiardApi - An API for reading and writing files in the SIARD Format 2.1

This package contains an API for reading and writing files in the
SIARD Format 2.1

## Getting started (for devs)

For building the binaries, Java JDK (1.8 or higher) and Ant must
have been installed.

Check `build.properties` and make changes according to your system. No special configuration should be necessary, if the java binaries are available in your `$PATH`.  

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

## IDEs

No specific IDE is needed. 


## Documentation

[./doc/manual/user/index.html](./doc/manual/user/index.html) contains the manual for using the binaries.
[./doc/manual/developer/index.html](./doc/manual/developer/index.html) is the manual for developers wishing to build the binaries or work on the code.

More information about the build process can be found in
[doc/manual/developer/build.html](./doc/manual/developer/build.html).
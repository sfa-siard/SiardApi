import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

plugins {
    java
    `java-library`
    id("pl.allegro.tech.build.axion-release") version "1.14.3"
    id("io.freefair.lombok") version "6.5.0"
}

group = "ch.admin.bar.siard"
version = scmVersion.version

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    withSourcesJar()
}

repositories {
    mavenCentral()
    flatDir {
        dirs("lib")
    }
}

// Define directories similar to Ant properties
val dirSrc = "src/main/java"
val dirRes = "$dirSrc/ch/admin/bar/siard2/api/res"
val dirGenerated = "$dirSrc/ch/admin/bar/siard2/api/generated"
val dirTest = "src/test/java"
val dirLib = "lib"
val dirDoc = "doc"
val dirEtc = "etc"
val dirTestFiles = "testfiles"
val dirTmp = "tmp"
val dirDist = "dist"
val dirJavadoc = "$dirDoc/javadoc"
val dirSpecifications = "$dirDoc/specifications"

// Define dependencies similar to Ant classpath definitions
dependencies {
    // Local JAR files
    implementation(files("$dirLib/enterutils.jar"))
    implementation(files("$dirLib/sqlparser.jar"))
    implementation(files("$dirLib/zip64.jar"))

    // JAXB dependencies
    implementation(files("$dirLib/activation-1.1.1.jar"))
    implementation(files("$dirLib/jaxb-api.jar"))
    implementation(files("$dirLib/jaxb-core.jar"))
    implementation(files("$dirLib/jaxb-impl.jar"))

    // Woodstox dependencies
    implementation(files("$dirLib/stax2-api-3.1.1.jar"))
    implementation(files("$dirLib/woodstox-core-lgpl-4.1.2.jar"))

    // MSV dependencies
    implementation(files("$dirLib/msv-core-2010.2.jar"))
    implementation(files("$dirLib/xsdlib-2010.1.jar"))
    implementation(files("$dirLib/woodstox-msv-rng-datatype-20020414.jar"))

    // Test dependencies
    testImplementation("junit:junit:4.13.1")
    testImplementation("org.hamcrest:hamcrest-core:1.3")

    // ANTLR dependency
    implementation(files("$dirLib/antlr-runtime-4.5.2.jar"))
}

// Create necessary directories
tasks.register("createDirs") {
    dependsOn("clean")
    group = "build"
    description = "Initialize project directories"

    doLast {
        mkdir("$dirGenerated/old10")
        mkdir("$dirGenerated/old21")
        mkdir("$dirGenerated/table")
        mkdir(dirTmp)
        mkdir("$dirTmp/lobs")
        mkdir(dirDist)
        mkdir(dirJavadoc)
    }
}

// Task to generate JAXB classes from XSD files
tasks.register<Exec>("generateJaxb") {
    group = "build"
    description = "Generate JAXB classes from XSD files"

    // This assumes xjc is in the PATH, you may need to adjust this
    val xjc = "xjc"

    doFirst {
        // Delete previously generated files
        delete(fileTree(dirGenerated) {
            include("**/*.java")
        })
    }

    // Generate metadata classes
    commandLine(
        xjc,
        "-encoding", "UTF-8",
        "-npa",
        "-d", dirSrc,
        "-p", "ch.admin.bar.siard2.api.generated",
        "$dirRes/metadata.xsd"
    )

    // Generate old10 metadata classes
    doLast {
        exec {
            commandLine(
                xjc,
                "-encoding", "UTF-8",
                "-npa",
                "-d", dirSrc,
                "-p", "ch.admin.bar.siard2.api.generated.old10",
                "$dirRes/old10/metadata.xsd"
            )
        }

        // Generate old21 metadata classes
        exec {
            commandLine(
                xjc,
                "-encoding", "UTF-8",
                "-npa",
                "-d", dirSrc,
                "-p", "ch.admin.bar.siard2.api.generated.old21",
                "$dirRes/old21/metadata.xsd"
            )
        }

        // Generate table classes
        exec {
            commandLine(
                xjc,
                "-encoding", "UTF-8",
                "-npa",
                "-d", dirSrc,
                "-p", "ch.admin.bar.siard2.api.generated.table",
                "$dirRes/table.xsd"
            )
        }
    }
}

// Copy resources to build directory
//tasks.processResources {
//    from(dirRes) {
//        include("**/*.*")
//    }
//}

// Configure Java compilation
tasks.compileJava {
    dependsOn("generateJaxb")
    options.encoding = "UTF-8"
}

// Configure Javadoc generation
tasks.javadoc {
    title = "SIARD API"
    source = sourceSets.main.get().allJava
    classpath = sourceSets.main.get().compileClasspath
    options {
        encoding = "UTF-8"
        (this as StandardJavadocDocletOptions).apply {
            addStringOption("Xdoclint:none", "-quiet")
            tags = listOf(
                "label:a:Label:",
                "responsibility:a:Responsibility:",
                "precondition:a:Precondition:",
                "postcondition:a:Postcondition:"
            )
        }
    }
}

// Create release ZIP file
//tasks.register<Zip>("release") {
//    group = "distribution"
//    description = "Create a ZIP file with binaries for distribution"
//    dependsOn("jar", "javadoc")
//
//    archiveFileName.set("${project.name}-${project.version}.zip")
//    destinationDirectory.set(file(dirDist))
//
//    // Include the main JAR file
//    from("${buildDir}/libs") {
//        include("*.jar")
//        into("${project.name}/$dirLib")
//    }
//
//    // Include library JARs except test libraries
//    from(dirLib) {
//        exclude("hamcrest-core-1.3.jar")
//        exclude("junit-4.12.jar")
//        into("${project.name}/$dirLib")
//    }
//
//    // Include documentation
//    from(dirDoc) {
//        exclude("developer/**/*.*")
//        into("${project.name}/$dirDoc")
//    }
//
//    // Include etc files except debug properties
//    from(dirEtc) {
//        exclude("debug.properties")
//        into("${project.name}/$dirEtc")
//    }
//
//    // Include test files
//    from(dirTestFiles) {
//        include("sql2003.siard")
//        include("sample.siard")
//        into("${project.name}/$dirTestFiles")
//    }
//
//    // Include text files
//    from(".") {
//        include("*.txt")
//        into("${project.name}")
//    }
//}

// Update the manifest with version and build date
tasks.jar {
    manifest {
        attributes(
            "Implementation-Title" to "SIARD API",
            "Implementation-Version" to project.version,
            "Built-Date" to LocalDate.now().format(DateTimeFormatter.ofPattern("dd. MMM yyyy", Locale.ENGLISH))
        )
    }
}

// Default tasks
defaultTasks("build")

// Clean task
tasks.clean {
    delete(dirTmp)
    delete(dirDist)
    delete(dirJavadoc)
    delete(dirGenerated)
}

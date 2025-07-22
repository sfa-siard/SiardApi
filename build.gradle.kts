import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

plugins {
    java
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
val dirRes = "src/main/resources/res"
val dirGenerated = "$dirSrc/ch/admin/bar/siard2/api/generated"
val dirLib = "lib"
val dirTmp = "tmp"

val xjcConfiguration = configurations.create("xjc")

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

    // 4.x uses jakarta.* packages.  For a javax‑based project stick to 2.3.*.
    xjcConfiguration("org.glassfish.jaxb:jaxb-xjc:2.3.2")
    xjcConfiguration("org.glassfish.jaxb:jaxb-runtime:2.3.2")  // needed by the compiler itself
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
    }
}

// Task to generate JAXB classes from XSD files
tasks.register<JavaExec>("generateJaxb") {
    group       = "build"
    description = "Generate JAXB classes from XSD files"

    classpath   = xjcConfiguration
    mainClass.set("com.sun.tools.xjc.XJCFacade")

    // first schema
    args("-encoding", "UTF-8", "-npa", "-d", dirSrc,
        "-p", "ch.admin.bar.siard2.api.generated",
        "$dirRes/metadata.xsd")

    // run three more times for the other packages
    doLast {
        fun runXjc(pkg: String, xsd: String) = exec {
            commandLine = listOf(
                "java", "-cp", xjcConfiguration.asPath,
                "com.sun.tools.xjc.XJCFacade",
                "-encoding", "UTF-8", "-npa", "-d", dirSrc,
                "-p", pkg, xsd
            )
        }
        runXjc("ch.admin.bar.siard2.api.generated.old10", "$dirRes/old10/metadata.xsd")
        runXjc("ch.admin.bar.siard2.api.generated.old21", "$dirRes/old21/metadata.xsd")
        runXjc("ch.admin.bar.siard2.api.generated.table",  "$dirRes/table.xsd")
    }
}


// Configure Java compilation
tasks.compileJava {
    dependsOn("generateJaxb")
    options.encoding = "UTF-8"
}

// Configure resources processing
tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    dependsOn("generateJaxb")
    // Ensure resources are copied to the output directory
    from("src/main/resources") {
        include("**/*.*")
    }
}

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


// Clean task
tasks.clean {
    delete(dirTmp)
    delete(dirGenerated)
}

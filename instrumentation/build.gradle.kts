plugins {
    id("com.github.johnrengelman.shadow") version "6.0.0"
    `java-library`
}

val instrumentationTest = tasks.named("test")
val instrumentationDeps = dependencies

val versions: Map<String, String> by extra

subprojects {
    val subProj = this

    /*
    TODO: uncomment when adding tests
    plugins.withId("java") {
        // Make it so all instrumentation subproject tests can be run with a single command.
        instrumentationTest.configure {
            dependsOn(subProj.tasks.named("test"))
        }

        // exclude stub projects, they are only compile time dependencies
        if (!subProj.name.endsWith("-stub")) {
            instrumentationDeps.run {
                implementation(project(subProj.path))
            }
        }
    }
     */


    dependencies {
        implementation("org.slf4j:slf4j-api:${versions["slf4j"]}")
        implementation("net.bytebuddy:byte-buddy:${versions["byte_buddy"]}")

        compileOnly("com.google.auto.service:auto-service-annotations:1.0")
        annotationProcessor("com.google.auto.service:auto-service:1.0")

        implementation("io.opentelemetry:opentelemetry-api:${versions["opentelemetry"]}")
        implementation("io.opentelemetry.javaagent:opentelemetry-javaagent-api:${versions["opentelemetry_alpha"]}")
        implementation("io.opentelemetry.javaagent:opentelemetry-javaagent-tooling:${versions["opentelemetry_alpha"]}")
        implementation("io.opentelemetry.javaagent", "opentelemetry-javaagent-api", version = "${versions["opentelemetry_alpha"]}")

        implementation("io.opentelemetry.instrumentation:opentelemetry-instrumentation-api:${versions["opentelemetry_alpha"]}")

        implementation(project(":agent-core"))
        testImplementation(project(":testing-common"))
    }

    // This ensures to build jars for all dependencies in instrumentation module for ByteBuddy
    configurations.named(JavaPlugin.COMPILE_CLASSPATH_CONFIGURATION_NAME) {
        attributes {
            attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named<LibraryElements>(LibraryElements.JAR))
        }
    }
}

dependencies{
    implementation(project(":otel-extensions"))
}

tasks {
    // Keep in sync with https://github.com/open-telemetry/opentelemetry-java-instrumentation/blob/f893ca540b72a895fbf18c14d2df8d1cabaf2c7f/instrumentation/instrumentation.gradle#L51
    shadowJar {
        dependencies{
            // exclude core, it lives in the bootstrap classloader
            exclude(project(":agent-core"))
        }

        mergeServiceFiles()

        relocate("com.blogspot.mydailyjava.weaklockfree", "io.opentelemetry.instrumentation.api.internal.shaded.weaklockfree")

        exclude("**/module-info.class")

        relocate("org.slf4j", "io.opentelemetry.javaagent.slf4j")
        relocate("java.util.logging.Logger", "io.opentelemetry.javaagent.bootstrap.PatchLogger")

        // prevents conflict with library instrumentation
        relocate("io.opentelemetry.instrumentation.api", "io.opentelemetry.javaagent.shaded.instrumentation.api")
        //opentelemetry rewrite library instrumentation dependencies
        relocate("io.opentelemetry.instrumentation", "io.opentelemetry.javaagent.shaded.instrumentation") {
            exclude("io.opentelemetry.javaagent.instrumentation.**")
        }

        // relocate OpenTelemetry API
        relocate("io.opentelemetry.api", "io.opentelemetry.javaagent.shaded.io.opentelemetry.api")
        relocate("io.opentelemetry.semconv", "io.opentelemetry.javaagent.shaded.io.opentelemetry.semconv")
        relocate("io.opentelemetry.spi", "io.opentelemetry.javaagent.shaded.io.opentelemetry.spi")
        relocate("io.opentelemetry.context", "io.opentelemetry.javaagent.shaded.io.opentelemetry.context")
        relocate("io.opentelemetry.extension.kotlin", "io.opentelemetry.javaagent.shaded.io.opentelemetry.extension.kotlin")
        relocate("io.opentelemetry.extension.aws", "io.opentelemetry.javaagent.shaded.io.opentelemetry.extension.aws")
    }
}

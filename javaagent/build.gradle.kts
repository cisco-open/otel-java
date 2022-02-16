group = "com.cisco"
version = "1.0.0"

val versions: Map<String, String> by extra

plugins {
    `java-library`
    id("com.github.johnrengelman.shadow")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

base {
    archivesBaseName = "cisco-otel-agent"
}

dependencies {
    implementation("io.opentelemetry.javaagent", "opentelemetry-javaagent", version = "${versions["opentelemetry"]}", classifier = "all")
    implementation("com.google.auto.service:auto-service-annotations:1.0")
    implementation(project(":agent-core"))
}

tasks {
    processResources {
        val customizationShadowTask = project(":instrumentation").tasks.named<Jar>("shadowJar")
        val providerArchive = customizationShadowTask.get().archiveFile
        from(zipTree(providerArchive)) {
            into("inst")
            rename("(^.*)\\.class$", "$1.classdata")
        }
        exclude("**/META-INF/LICENSE")
        dependsOn(customizationShadowTask)
    }

    shadowJar {
        relocate("com.blogspot.mydailyjava.weaklockfree", "io.opentelemetry.instrumentation.api.internal.shaded.weaklockfree")

        dependencies {
            exclude(dependency("org.codehaus.mojo:animal-sniffer-annotations"))
            exclude(dependency("javax.annotation:javax.annotation-api"))
        }

        relocate("org.slf4j", "io.opentelemetry.javaagent.slf4j")
        relocate("java.util.logging.Logger", "io.opentelemetry.javaagent.bootstrap.PatchLogger")

        // prevents conflict with library instrumentation
        relocate("io.opentelemetry.instrumentation.api", "io.opentelemetry.javaagent.shaded.instrumentation.api")

        // relocate OpenTelemetry API
        relocate("io.opentelemetry.api", "io.opentelemetry.javaagent.shaded.io.opentelemetry.api")
        relocate("io.opentelemetry.semconv", "io.opentelemetry.javaagent.shaded.io.opentelemetry.semconv")
        relocate("io.opentelemetry.spi", "io.opentelemetry.javaagent.shaded.io.opentelemetry.spi")
        relocate("io.opentelemetry.context", "io.opentelemetry.javaagent.shaded.io.opentelemetry.context")
        relocate("io.opentelemetry.extension.kotlin", "io.opentelemetry.javaagent.shaded.io.opentelemetry.extension.kotlin")
        relocate("io.opentelemetry.extension.aws", "io.opentelemetry.javaagent.shaded.io.opentelemetry.extension.aws")

        mergeServiceFiles {
            include("inst/META-INF/services/*")
            // exclude because it would be shaded twice and the META-INF/services/ would be io.opentelemetry.javaagent.shaded.io.grpc
            exclude("inst/META-INF/services/io.grpc*")
        }

        exclude("**/module-info.class")

        manifest {
            attributes.put("Main-Class", "io.opentelemetry.javaagent.OpenTelemetryAgent")
            attributes.put("Agent-Class", "com.cisco.agent.bootstrap.CiscoAgentBootstrap")
            attributes.put("Premain-Class", "com.cisco.agent.bootstrap.CiscoAgentBootstrap")
            attributes.put("Can-Redefine-Classes", "true")
            attributes.put("Can-Retransform-Classes", "true")

            val versionString = project.version.toString()
            val implementationVersion: String

            if (versionString.endsWith("-SNAPSHOT")) {
                implementationVersion = "${versionString.dropLast("-SNAPSHOT".length)}-espagon-SNAPSHOT"

            } else {
                implementationVersion = "$versionString-cisco"
            }

            attributes.put("Implementation-Version", implementationVersion)
        }
    }

    assemble {
        dependsOn(shadowJar)
    }
}

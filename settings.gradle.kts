rootProject.name = "fso-java-sdk"
include("smoke-tests")

pluginManagement {
    plugins {
        id("com.diffplug.spotless") version "5.8.2"
        id("com.github.ben-manes.versions") version "0.38.0"
        id("com.github.jk1.dependency-license-report") version "1.16"
        id("com.github.johnrengelman.shadow") version "6.1.0"
        id("com.google.cloud.tools.jib") version "2.8.0"
        id("io.github.gradle-nexus.publish-plugin") version "1.0.0"
        id("nebula.release") version "15.3.0"
        id("org.springframework.boot") version "2.4.0"
        id("org.gradle.test-retry") version "1.2.0" apply false
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        mavenLocal()

        maven {
            setUrl("https://oss.sonatype.org/content/repositories/snapshots")
        }
    }
}

include("fso-agent")
include("instrumentation")
include("javaagent-tooling")
include("javaagent-bootstrap")
include("fso-core")

include(":testing-common")

include("otel-extensions")
include("testing-bootstrap")


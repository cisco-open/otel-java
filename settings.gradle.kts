rootProject.name = "otel-java"
include("smoke-tests")

pluginManagement {
    plugins {
        id("com.diffplug.spotless") version "5.8.2"
        id("com.github.johnrengelman.shadow") version "6.1.0"
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
include("fso-core")
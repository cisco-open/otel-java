plugins {
    `java-library`
    `maven-publish`
    id("com.diffplug.spotless") version "5.2.0" apply false
    id("org.gradle.test-retry") version "1.2.0" apply false
}

allprojects {
    apply(plugin="java-library")
    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    tasks.withType<JavaCompile> {
        options.compilerArgs.add("-Xlint:unchecked")
        options.isDeprecation = true
    }
}


val testDependencies by configurations.creating {
    extendsFrom(configurations.testImplementation.get())
    extendsFrom(configurations.testRuntimeOnly.get())
}

var OPENTELEMETRY_GENERAL_VERSION = "1.1.0"
var OPENTELEMETRY_INSTRUMENTATION_VERSION = "1.1.0-alpha"


subprojects {
    group = "com.cisco"
    version = "1.0.4"

    extra.set("versions", mapOf(
            "slf4j" to "1.7.30",
            "byte_buddy" to "1.10.18",
            "checkerFramework" to "3.6.1",

            "opentelemetry" to OPENTELEMETRY_GENERAL_VERSION,

            "opentelemetry_alpha" to OPENTELEMETRY_INSTRUMENTATION_VERSION
    ))

    apply<JavaPlugin>()
    apply(plugin = "com.diffplug.spotless")
    apply(from = "$rootDir/gradle/spotless.gradle")

    repositories {
        mavenCentral()
        jcenter()
        maven {
            url = uri("https://dl.bintray.com/open-telemetry/maven")
        }
        maven {
            url = uri("https://oss.jfrog.org/artifactory/oss-snapshot-local")
        }
    }

    dependencies {
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
        testImplementation("org.junit-pioneer:junit-pioneer:1.0.0")
    }

    tasks {
        test {
            useJUnitPlatform()
            reports {
                junitXml.isOutputPerTestCase = true
            }
        }
    }
}
publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.cisco"
            artifactId = "cisco-otel-javaagent"
            version = "1.0.0-SNAPSHOT"

            from(components["java"])
        }
    }

    repositories {
        maven {
            val user = System.getenv("OSSRH_JIRA_USERNAME")
            val pass = System.getenv("OSSRH_JIRA_PASSWORD")
            name = "cisco-otel-javaagent"
            url = uri("https://oss.sonatype.org/content/repositories/snapshots")
            credentials {
                username = user
                password = pass
            }
        }
    }
}

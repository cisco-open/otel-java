// This project is here to satisfy dependencies for the Muzzle gradle plugin (./buildSrc)

plugins {
    `java-library`
}

val instrumentationMuzzle by configurations.creating {
    extendsFrom(configurations.implementation.get())
    extendsFrom(configurations.api.get())
}

val versions: Map<String, String> by extra

dependencies {
    api("io.opentelemetry.javaagent:opentelemetry-javaagent-tooling:${versions["opentelemetry_alpha"]}")
    api("io.opentelemetry.javaagent:opentelemetry-javaagent-api:${versions["opentelemetry_alpha"]}")

    instrumentationMuzzle("com.google.auto.service:auto-service:1.0")
    instrumentationMuzzle("org.slf4j:slf4j-api:${versions["slf4j"]}")
    instrumentationMuzzle("net.bytebuddy:byte-buddy:${versions["byte_buddy"]}")
    instrumentationMuzzle("net.bytebuddy:byte-buddy-agent:${versions["byte_buddy"]}")
    instrumentationMuzzle("io.opentelemetry.javaagent:opentelemetry-javaagent-bootstrap:${versions["opentelemetry_alpha"]}")
}

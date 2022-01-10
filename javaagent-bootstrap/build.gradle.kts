// This project is here to satisfy dependencies for the Muzzle gradle plugin (./buildSrc)

plugins {
    `java-library`
}

val versions: Map<String, String> by extra

dependencies{
    api("io.opentelemetry.javaagent:opentelemetry-javaagent-bootstrap:${versions["opentelemetry_alpha"]}")
    api("io.opentelemetry.javaagent:opentelemetry-javaagent-api:${versions["opentelemetry_alpha"]}")
    implementation(project(":fso-core"))
}

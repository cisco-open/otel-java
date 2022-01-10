plugins {
    `java-library`
    id("net.bytebuddy.byte-buddy")
}

val versions: Map<String, String> by extra

configurations {
    implementation.get().extendsFrom(project(":").configurations["testDependencies"])
}

dependencies {
    implementation("io.opentelemetry.javaagent:opentelemetry-testing-common:${versions["opentelemetry_alpha"]}") {
        exclude("org.eclipse.jetty", "jetty-server")
    }
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("org.slf4j:log4j-over-slf4j:${versions["slf4j"]}")
    implementation("org.slf4j:jcl-over-slf4j:${versions["slf4j"]}")
    implementation("org.slf4j:jul-to-slf4j:${versions["slf4j"]}")
    api("io.opentelemetry:opentelemetry-sdk:${versions["opentelemetry"]}")
}
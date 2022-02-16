plugins {
    `java-library`
}

val versions: Map<String, String> by extra

dependencies {
    api("io.opentelemetry:opentelemetry-api:${versions["opentelemetry"]}")
    api("io.opentelemetry.javaagent:opentelemetry-javaagent-api:${versions["opentelemetry_alpha"]}")
    implementation("io.opentelemetry.instrumentation:opentelemetry-instrumentation-api-caching:${versions["opentelemetry_alpha"]}")
    implementation("org.slf4j:slf4j-api:${versions["slf4j"]}")
    implementation("org.json:json:20171018")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
    testImplementation("org.junit-pioneer:junit-pioneer:1.0.0")
}

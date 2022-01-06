plugins {
    `java-library`
    idea
}
// TODO: this package is note loded and auto serv. should fix that.
val versions: Map<String, String> by extra

dependencies {
    api(project(":fso-core"))

    compileOnly("io.opentelemetry:opentelemetry-sdk:${versions["opentelemetry"]}")
    compileOnly("io.opentelemetry:opentelemetry-sdk-extension-autoconfigure:${versions["opentelemetry_alpha"]}")
    compileOnly("io.opentelemetry.javaagent:opentelemetry-javaagent-extension-api:1.3.0-alpha")
    implementation("io.opentelemetry:opentelemetry-semconv:${versions["opentelemetry_alpha"]}")
    implementation("io.opentelemetry.javaagent:opentelemetry-javaagent-spi:${versions["opentelemetry_alpha"]}")
    implementation("io.opentelemetry.instrumentation:opentelemetry-instrumentation-api:${versions["opentelemetry_alpha"]}")
    implementation("io.opentelemetry:opentelemetry-proto:${versions["opentelemetry_alpha"]}")
    implementation("io.opentelemetry:opentelemetry-exporter-otlp:${versions["opentelemetry"]}")
    implementation("io.opentelemetry:opentelemetry-exporter-otlp-common:${versions["opentelemetry"]}")

    implementation("org.slf4j:slf4j-api:${versions["slf4j"]}")
    compileOnly("com.squareup.okhttp3:okhttp:3.4.1")
    compileOnly("com.google.auto.service:auto-service-annotations:1.0")
    compileOnly("javax.annotation:javax.annotation-api:1.3.2")
    implementation("net.bytebuddy:byte-buddy:${versions["byte_buddy"]}")
    annotationProcessor("com.google.auto.service:auto-service:1.0")

    testImplementation("io.opentelemetry:opentelemetry-sdk-extension-autoconfigure:${versions["opentelemetry_alpha"]}")
    testImplementation("io.opentelemetry:opentelemetry-sdk:${versions["opentelemetry"]}")
}

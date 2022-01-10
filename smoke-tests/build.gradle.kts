plugins {
    groovy
    `java-library`
    id("org.gradle.test-retry")
}

apply {
    from("$rootDir/gradle/java.gradle")
}

val versions: Map<String, String> by extra

dependencies{
    testImplementation(project(":fso-core"))
    testImplementation(project(":testing-common"))
    testImplementation("org.testcontainers:testcontainers:1.15.2")
    testImplementation("com.squareup.okhttp3:okhttp:4.9.0")
    testImplementation("org.awaitility:awaitility:4.0.3")
    testImplementation("io.opentelemetry:opentelemetry-proto:${versions["opentelemetry_alpha"]}")
    testImplementation("io.opentelemetry.javaagent:opentelemetry-testing-common:${versions["opentelemetry_alpha"]}")
    testImplementation("io.opentelemetry:opentelemetry-sdk:${versions["opentelemetry"]}")
    testImplementation("com.google.protobuf:protobuf-java-util:3.15.8")
    testImplementation("org.spockframework:spock-core:1.3-groovy-2.5")
    testImplementation("info.solidsoft.spock:spock-global-unroll:0.5.1")
    testImplementation("com.fasterxml.jackson.core:jackson-databind:2.11.2")
    testImplementation("org.codehaus.groovy:groovy-all:2.5.11")
    testImplementation("io.opentelemetry:opentelemetry-semconv:${versions["opentelemetry_alpha"]}")
    testImplementation(platform("io.grpc:grpc-bom:1.33.1"))
    testImplementation("io.grpc:grpc-netty-shaded")
    testImplementation("io.grpc:grpc-protobuf")
    testImplementation("io.grpc:grpc-stub")
}

tasks.test {
    useJUnitPlatform()
    reports {
        junitXml.isOutputPerTestCase = true
    }

    maxParallelForks = 2

    var suites : HashMap<String, String>
            = HashMap()

    val suite = findProperty("smokeTestSuite")

    suites.put("tomcat", "**/TomcatSmokeTest.*")
    suites.put("jetty", "**/JettySmokeTest.*")

    if (suite != null) {
        if ("other" == suite) {
            for ((key, value) in suites) {
                exclude(value)
            }
        } else if (suites.containsKey(suite)) {
            include(suites.get(suite))
        } else {
            throw GradleException("Unknown smoke test suite: " + suite)
        }
    }

    val shadowTask : Jar = project(":fso-agent").tasks.named<Jar>("shadowJar").get()
    inputs.files(layout.files(shadowTask))

    doFirst {
        jvmArgs("-Dsmoketest.javaagent.path=${shadowTask.archiveFile.get()}")
    }
}
# Cisco Smoke Tests

These tests are heavily based on Open Telemetry smoke tests.

It's very recommended getting familiar with theirs tests before
starting to expend and work with our smoke tests.

link: ```https://github.com/open-telemetry/opentelemetry-java-instrumentation/tree/main/smoke-tests```

Our tests consume their Docker images (which contains application to test the instrumented frameworks) from
'ghcr.io/open-telemetry/java-test-containers' where they can or from Cisco when
custom test app container is needed (included as subproject from this dir).

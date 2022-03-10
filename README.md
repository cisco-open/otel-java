# Cisco Java Agent OpenTelemetry Distribution


The Cisco OpenTelemetry Distribution of [OpenTelemetry Instrumentation for Java](https://github.com/open-telemetry/opentelemetry-java-instrumentation).

This package provides tracing to Java applications for the collection of distributed tracing and performance metrics in [Cisco Telescope](https://cisco-fso.com).

## Requirements

The agent works with Java runtimes version 8 and higher.

## Installation and getting started

Download the [latest version](https://cisco-java-sdk.s3.amazonaws.com/cisco-agent-1.0.0-all.jar)

* On Linux, run:
```shell
export OTEL_SERVICE_NAME=APP
export CISCO_TOKEN=<token>

java -javaagent:./cisco-otel-javaagent.jar \
  -jar <myapp>.jar
```
Insert the -javaagent flag before the -jar file, adding it as a JVM option, not as an application argument.

## Configuration
Cisco OTel Agent support all existing [Open Telemetry configurations](https://github.com/open-telemetry/opentelemetry-java-instrumentation/blob/main/docs/agent-config.md)
and provides new defaults for some of them.

In addition, Cisco OTel agent exports another set of Properties/Variables for the specific Cisco Agent configuration

Cisco OTel Agent configurations:

|System property                         |Environment variable          |Default          |Description
|----------------------------------------|------------------------------|-----------------|----------------|
|cisco.token                             | CISCO_TOKEN                  | None            | Cisco account token|
|otel.service.name                       | OTEL_SERVICE_NAME            | None            | Java service name|
|cisco.payloads_enabled                  | CISCO_PAYLOADS_ENABLED       | ```False```      | Whether to capture additional payloads and experimental attributes. Follow [Specifications](https://github.com/epsagon/cisco-otel-distribution-specifications) for more information.|
|cisco.debug                             | -                            | ```False```     |Enable debug prints for troubleshooting|


Open Telemetry defaults:

|System property                         |Environment variable          |Default          |Description    
|----------------------------------------|------------------------------|-----------------|----------------|
|OTEL_METRICS_EXPORTER                   | otel.metrics.exporter        | None            | By default, metrics are currently not supported|
|OTEL_INSTRUMENTATION_RUNTIME-METRICS_ENABLED                           | otel.instrumentation.runtime-metrics.enabled        | None            | By default, metrics are currently not supported|
|OTEL_TRACES_EXPORTER                   | otel.traces.exporter          | otlp            | Otlp over gRPC exporter|
|OTEL_EXPORTER_OTLP_ENDPOINT                   | otel.exporter.otlp.endpoint        | https://opentelemetry.tc.cisco.com/traces            | The Cisco Otlp-gRPC collector URL path|


## Getting Help

If you have any issue around using the library or the product, please don't hesitate to:

* Use the help widget inside the product.
* Open an issue in GitHub.


## Opening Issues

If you encounter a bug with the Cisco OpenTelemetry Distribution library for Java, we want to hear about it.

When opening a new issue, please provide as much information about the environment:
* Library version, Java runtime version, dependencies, etc.
* Snippet of the usage.
* A reproducible example can really help.

The GitHub issues are intended for bug reports and feature requests.
For help and questions about Cisco OpenTelemetry Distribution, use the help widget inside the product.

## License

Provided under the MIT license. See LICENSE for details.

Copyright 2020, Cisco


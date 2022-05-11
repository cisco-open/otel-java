/*
 * Copyright The Cisco Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cisco.opentelemetry.agent.custom;

import java.util.HashMap;
import java.util.Map;

import com.epsagon.specifications.consts.Consts;

import io.opentelemetry.javaagent.extension.config.ConfigPropertySource;

/**
 * {@link ConfigPropertySource} is an SPI provided by OpenTelemetry Java instrumentation agent. By
 * implementing it custom distributions can supply their own default configuration. The
 * configuration priority, from highest to lowest is: system properties -> environment variables ->
 * configuration file -> PropertySource SPI -> hard-coded defaults
 */
public class CiscoPropertySource implements ConfigPropertySource {

  private static String getDefaultExporterProtocol() {
    switch (Consts.DEFAULT_EXPORTER_TYPE) {
      case "otlp-http":
        return "http/protobuf";
      case "otlp-grpc":
        return "grpc";
      default:
        throw new VerifyError("Unsupported protocol: " + Consts.DEFAULT_EXPORTER_TYPE);
    }
  }

  @Override
  public Map<String, String> getProperties() {
    Map<String, String> properties = new HashMap<>();
    // OpenTelemetry (over gRPC) protocol
    properties.put("otel.exporter.otlp.protocol", getDefaultExporterProtocol());
    properties.put("otel.exporter.otlp.traces.endpoint", Consts.DEFAULT_COLLECTOR_ENDPOINT);

    // By default, metrics are currently not supported
    properties.put("otel.metrics.exporter", "none");
    properties.put("otel.instrumentation.runtime-metrics.enabled", "false");

    // Experimental Span Attributes
    properties.put("otel.instrumentation.servlet.experimental-span-attributes", "true");
    properties.put("otel.instrumentation.aws-sdk.experimental-span-attributes", "true");
    properties.put("otel.instrumentation.apache-camel.experimental-span-attributes", "true");

    return properties;
  }
}

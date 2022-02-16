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

package com.cisco.agent.otel.extensions.config;

import java.util.HashMap;
import java.util.Map;

import com.google.auto.service.AutoService;

import io.opentelemetry.javaagent.spi.config.PropertySource;

/**
 * A class defines default config properties to Otel. This class is called when no default otel
 * config exists. The logic here is to return the Default hardcoded value instead of Open Telemetry
 * value. for more info:
 * https://github.com/open-telemetry/opentelemetry-specification/blob/master/specification/sdk-environment-variables.md
 */
@AutoService(PropertySource.class)
public class DefaultOtelPropertySource implements PropertySource {
  @Override
  public Map<String, String> getProperties() {
    Map<String, String> configProperties = new HashMap<>();

    // By default, metrics are currently not supported
    configProperties.put("otel.metrics.exporter", "none");
    configProperties.put("otel.instrumentation.runtime-metrics.enabled", "false");

    // OpenTelemetry (over gRPC) protocol
    configProperties.put("otel.traces.exporter", "otlp");
    configProperties.put("otel.exporter.otlp.endpoint", "http://localhost:4317");

    // Experimental Span Attributes
    configProperties.put("otel.instrumentation.servlet.experimental-span-attributes", "true");
    configProperties.put("otel.instrumentation.aws-sdk.experimental-span-attributes", "true");
    configProperties.put("otel.instrumentation.apache-camel.experimental-span-attributes", "true");

    return configProperties;
  }
}

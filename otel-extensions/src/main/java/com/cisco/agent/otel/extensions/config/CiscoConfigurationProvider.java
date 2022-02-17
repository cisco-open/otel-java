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

import io.opentelemetry.instrumentation.api.config.Config;

/**
 * A class defines the Cisco configurations and implement an API to access all Cisco configurations.
 * The class converting the config from Open Telemetry config properties to Java types and also
 * provides default values to all un specified configs
 */
public class CiscoConfigurationProvider {
  public static boolean getPayloadsEnabled() {
    return Config.get().getBooleanProperty("cisco.payloads_enabled", false);
  }

  public static String getServiceName() {
    return Config.get().getProperty("otel.service.name");
  }

  public static String getToken() throws IllegalStateException {
    return Config.get().getProperty("cisco.token");
  }
}

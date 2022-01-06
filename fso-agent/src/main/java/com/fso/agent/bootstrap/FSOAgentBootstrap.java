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

package com.fso.agent.bootstrap;

import java.lang.instrument.Instrumentation;
import java.util.Locale;

import com.fso.agent.core.config.InstrumentationManager;

import io.opentelemetry.javaagent.OpenTelemetryAgent;

public class FSOAgentBootstrap {
  static class MissingTokenException extends Exception {
    MissingTokenException() {
      super("Token is not specified and should be. Please add token in format: fso.token");
    }
  }

  public static void premain(final String agentArgs, final Instrumentation inst) {

    // In case the user set fso.debug and not default otel.javaagent.debug
    // This is has to be done before Otel agent boot because he pools the config from System/Env and
    // not from api.config.Config
    if (Boolean.parseBoolean(System.getProperty("fso.debug"))) {
      System.setProperty("otel.javaagent.debug", "true");
    }

    InstrumentationManager.disableUnsupportedInstrumentations();

    OpenTelemetryAgent.agentmain(agentArgs, inst);

    System.out.println(
        "FSO agent is up. Version: "
            + FSOAgentBootstrap.class.getPackage().getImplementationVersion());
  }

  private static void addTokenToOtlpHeaders() throws MissingTokenException {
    String accessToken = getConfig("fso.token");
    if (accessToken != null) {
      String userOtlpHeaders = getConfig("otel.exporter.otlp.headers");
      String otlpHeaders =
          (userOtlpHeaders == null ? "" : userOtlpHeaders + ",") + "FSO-TOKEN" + accessToken;
      System.setProperty("otel.exporter.otlp.headers", otlpHeaders);
    } else {
      throw new MissingTokenException();
    }
  }

  /**
   * Parse a config property from System Properties/Environment Variables. We can not use OTel's
   * config here since it has to be done before the initialization.
   *
   * @param propertyName The property name to get from the system
   * @return The property values. The default here is empty String
   */
  private static String getConfig(String propertyName) {
    String value = System.getProperty(propertyName);
    if (value != null) {
      return value;
    }
    return System.getenv(propertyName.replace('.', '_').toUpperCase(Locale.ROOT));
  }
}

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

package com.cisco.opentelemetry.agent.bootstrap;

import java.lang.instrument.Instrumentation;
import java.util.Locale;

import com.epsagon.specifications.consts.Consts;

import io.opentelemetry.javaagent.OpenTelemetryAgent;

public class CiscoAgentBootstrap {
  static class MissingTokenException extends Exception {
    MissingTokenException() {
      super("Token is not specified and should be. Please add token in format: cisco.token");
    }
  }

  static class InvalidTokenException extends Exception {
    InvalidTokenException() {
      super(
          "Invalid token provided. checkout your token here: https://console.telescope.app/settings/account");
    }
  }

  public static void premain(final String agentArgs, final Instrumentation inst)
      throws MissingTokenException, InvalidTokenException {

    // In case the user set cisco.debug and not default otel.javaagent.debug
    // This is has to be done before Otel agent boot because he pools the config from System/Env and
    // not from api.config.Config
    if (Boolean.parseBoolean(getConfig(Consts.CISCO_DEBUG_ENV))) {
      System.setProperty("otel.javaagent.debug", "true");
    }

    addCiscoTokenToOtlpHeaders();

    OpenTelemetryAgent.agentmain(agentArgs, inst);

    System.out.println(
        Consts.TELESCOPE_IS_RUNNING_MESSAGE
            + "\nVersion: "
            + CiscoAgentBootstrap.class.getPackage().getImplementationVersion());
  }

  private static void addCiscoTokenToOtlpHeaders()
      throws MissingTokenException, InvalidTokenException {
    String token = getConfig(Consts.CISCO_TOKEN_ENV);
    if (token == null) {
      throw new MissingTokenException();
    } else if (token.contains("cisco") || token.contains("token")) {
      throw new InvalidTokenException();
    }

    String userOtlpHeaders = getConfig("OTEL_EXPORTER_OTLP_HEADERS");
    String otlpHeaders =
        (userOtlpHeaders == null ? "" : userOtlpHeaders + ",")
            + Consts.TOKEN_HEADER_KEY
            + "=Bearer "
            + token;
    System.setProperty("otel.exporter.otlp.headers", otlpHeaders);
  }

  private static String getConfig(String envName) {
    String propertyName = envName.replace('_', '.').toLowerCase(Locale.ROOT);

    String value = System.getProperty(propertyName);
    if (value != null) {
      return value;
    }

    return System.getenv(envName);
  }
}

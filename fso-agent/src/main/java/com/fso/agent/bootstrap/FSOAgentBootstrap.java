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

import com.fso.agent.core.config.InstrumentationManager;

import io.opentelemetry.javaagent.OpenTelemetryAgent;

public class FSOAgentBootstrap {
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

  private static void addSplunkAccessTokenToOtlpHeaders() {
    String accessToken = getConfig("splunk.access.token");
    if (accessToken != null) {
      String userOtlpHeaders = getConfig("otel.exporter.otlp.headers");
      String otlpHeaders =
          (userOtlpHeaders == null ? "" : userOtlpHeaders + ",") + "FSO-TOKEN" + accessToken;
      System.setProperty("otel.exporter.otlp.headers", otlpHeaders);
    }
  }
}

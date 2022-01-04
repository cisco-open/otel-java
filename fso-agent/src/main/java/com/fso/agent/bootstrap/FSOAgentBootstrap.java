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
}

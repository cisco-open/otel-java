package com.fso.agent.core.config;

import java.text.MessageFormat;

public class InstrumentationManager {
  static final String DISABLE_SYSTEM_PROPERTY_TEMPLATE = "otel.instrumentation.{0}.enabled";
  static final String[] UNSUPPORTED_INSTRUMENTATIONS = new String[] {};

  public static void disableUnsupportedInstrumentations() {
    for (String framework : UNSUPPORTED_INSTRUMENTATIONS) {
      String disable_property = MessageFormat.format(DISABLE_SYSTEM_PROPERTY_TEMPLATE, framework);
      System.setProperty(disable_property, "false");
    }
  }
}

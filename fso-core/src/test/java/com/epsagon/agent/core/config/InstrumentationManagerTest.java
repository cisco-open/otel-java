package com.fso.agent.core.config;

import java.text.MessageFormat;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InstrumentationManagerTest {
  @BeforeEach
  public void setUp() {
    for (String framework : InstrumentationManager.UNSUPPORTED_INSTRUMENTATIONS) {
      String disable_property =
          MessageFormat.format(InstrumentationManager.DISABLE_SYSTEM_PROPERTY_TEMPLATE, framework);
      System.setProperty(disable_property, "true");
    }
  }

  /**
   * Verify that the unsupported instrumentations disabled as expected (Check the system property).
   */
  @Test
  public void testDisablesInstrumentations() {
    InstrumentationManager.disableUnsupportedInstrumentations();
    for (String framework : InstrumentationManager.UNSUPPORTED_INSTRUMENTATIONS) {
      String disable_property =
          MessageFormat.format(InstrumentationManager.DISABLE_SYSTEM_PROPERTY_TEMPLATE, framework);
      Assertions.assertFalse(Boolean.parseBoolean(System.getProperty(disable_property)));
    }
  }
}

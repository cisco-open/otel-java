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

package com.cisco.agent.core.config;

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

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

import com.cisco.agent.core.config.CiscoConfiguration;
import com.google.auto.service.AutoService;

/**
 * An implementation of CiscoConfiguration. This implementation with installed also in the
 * instrumentations using Otel ComponentInstaller and will be available from both Cisco packages and
 * Instrumentations
 */
@AutoService(CiscoConfiguration.class)
public class CiscoConfigurationImpl implements CiscoConfiguration {
  private final boolean payloadsEnabled;

  /** An exception raised when failed to get/parse mandatory config from user */
  public static class InvalidConfigurationError extends IllegalStateException {
    public InvalidConfigurationError(String message) {
      super(message);
    }
  }

  public CiscoConfigurationImpl() {
    this.payloadsEnabled = CiscoConfigurationProvider.getPayloadsEnabled();
  }

  @Override
  public boolean payloadsEnabled() {
    return this.payloadsEnabled;
  }
}

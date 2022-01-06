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

package com.fso.agent.otel.extensions.config;

import com.fso.agent.core.config.FSOConfiguration;
import com.google.auto.service.AutoService;

/**
 * An implementation of FSOConfiguration. This implementation with installed also in the
 * instrumentations using Otel ComponentInstaller and will be available from both FSO packages and
 * Instrumentations
 */
@AutoService(FSOConfiguration.class)
public class FSOConfigurationImpl implements FSOConfiguration {
  private final boolean metadataOnly;

  /** An exception raised when failed to get/parse mandatory config from user */
  public static class InvalidConfigurationError extends IllegalStateException {
    public InvalidConfigurationError(String message) {
      super(message);
    }
  }

  public FSOConfigurationImpl() {
    this.metadataOnly = FSOConfigurationProvider.getMetadataOnly();
  }

  @Override
  public boolean metadataOnly() {
    return this.metadataOnly;
  }
}

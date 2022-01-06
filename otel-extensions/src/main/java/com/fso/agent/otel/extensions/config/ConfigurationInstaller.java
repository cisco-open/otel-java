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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fso.agent.core.config.FSOConfiguration.ConfigProvider;
import com.google.auto.service.AutoService;

import io.opentelemetry.instrumentation.api.config.Config;
import io.opentelemetry.javaagent.spi.ComponentInstaller;

@AutoService(ComponentInstaller.class)
public class ConfigurationInstaller implements ComponentInstaller {
  private static final Logger logger = LoggerFactory.getLogger(ConfigurationInstaller.class);

  @Override
  public void beforeByteBuddyAgent(Config config) {
    // get initializes singleton
    ConfigProvider.get();
    logger.debug("OTel Config: " + config);
  }
}

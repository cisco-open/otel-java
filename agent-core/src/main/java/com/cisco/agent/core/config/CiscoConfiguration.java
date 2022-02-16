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

import java.util.Iterator;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Instrumentation config holds configuration for the instrumentation. */
public interface CiscoConfiguration {
  boolean payloadsEnabled();

  class ConfigProvider {
    private static final Logger logger = LoggerFactory.getLogger(ConfigProvider.class);

    private static volatile CiscoConfiguration ciscoConfiguration;

    /** Reset the config, use only in tests. */
    public static void reset() {
      synchronized (ConfigProvider.class) {
        ciscoConfiguration = null;
      }
    }

    private static CiscoConfiguration load() {
      ServiceLoader<CiscoConfiguration> configs = ServiceLoader.load(CiscoConfiguration.class);
      Iterator<CiscoConfiguration> iterator = configs.iterator();
      if (!iterator.hasNext()) {
        logger.error("Failed to load instrumentation config");
        return null;
      } else {
        CiscoConfiguration configClass = iterator.next();
        logger.debug("Loaded: " + configClass.getClass().getName());
        return configClass;
      }
    }

    public static CiscoConfiguration get() {
      if (ciscoConfiguration == null) {
        synchronized (ConfigProvider.class) {
          if (ciscoConfiguration == null) {
            ciscoConfiguration = load();
          }
        }
      }
      return ciscoConfiguration;
    }
  }
}

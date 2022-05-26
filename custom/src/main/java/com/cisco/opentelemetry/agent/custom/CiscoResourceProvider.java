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

package com.cisco.opentelemetry.agent.custom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.epsagon.specifications.consts.Consts;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import io.opentelemetry.sdk.autoconfigure.spi.ResourceProvider;
import io.opentelemetry.sdk.resources.Resource;

public class CiscoResourceProvider implements ResourceProvider {
  private static final Logger log = LoggerFactory.getLogger(CiscoResourceProvider.class.getName());

  @Override
  public Resource createResource(ConfigProperties config) {
    Attributes attributes =
        Attributes.builder().put(Consts.CISCO_SDK_VERSION, getAgentVersion()).build();
    return Resource.create(attributes);
  }

  public static String getAgentVersion() {
    String agentVersion = "";
    try {
      Class<?> ciscoAgentClass =
          Class.forName("com.cisco.opentelemetry.agent.bootstrap.CiscoAgentBootstrap", true, null);
      agentVersion = ciscoAgentClass.getPackage().getImplementationVersion();
    } catch (ClassNotFoundException e) {
      log.warn("Could not load CiscoAgentBootstrap class");
    }
    return agentVersion;
  }
}

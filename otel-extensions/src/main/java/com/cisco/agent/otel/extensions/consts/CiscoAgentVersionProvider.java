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

package com.cisco.agent.otel.extensions.consts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** A class provides the Cisco Agent (extract from @see CiscoAgentBootstrap) */
public class CiscoAgentVersionProvider {
  private static final Logger log =
      LoggerFactory.getLogger(CiscoAgentVersionProvider.class.getName());

  public static String getAgentVersion() {
    String agentVersion = "";
    try {
      Class<?> ciscoAgentClass =
          Class.forName("com.cisco.agent.bootstrap.CiscoAgentBootstrap", true, null);
      agentVersion = ciscoAgentClass.getPackage().getImplementationVersion();
    } catch (ClassNotFoundException e) {
      log.warn("Could not load Cisco OTel Agent class");
    }

    return agentVersion;
  }
}

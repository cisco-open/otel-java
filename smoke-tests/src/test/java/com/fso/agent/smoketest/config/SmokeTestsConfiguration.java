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

package com.fso.agent.smoketest.config;

import java.util.HashMap;
import java.util.Map;

/** A class defines all Smoke Tests configuration */
public class SmokeTestsConfiguration {

  /* Containers consts */
  // The Port that will open for the container service access
  public static final int CONTAINER_TESTS_COMMUNICATION_PORT = 8080;
  public static final int APP_SERVER_PORT = CONTAINER_TESTS_COMMUNICATION_PORT;

  /* Backend config */
  public static final String BACKEND_ALIAS = "backend";
  public static final int BACKEND_PORT = CONTAINER_TESTS_COMMUNICATION_PORT;
  public static final String BACKEND_IMAGE_PATH =
      "ghcr.io/open-telemetry/java-test-containers:smoke-fake-backend-20210611.927888723";

  /* Collector config */
  public static final String COLLECTOR_ALIAS = "collector";
  public static final String COLLECTOR_IMAGE_PATH = "otel/opentelemetry-collector-dev:latest";
  public static final String COLLECTOR_CONFIG_RESOURCE = "/otel-config.yaml";

  /* Target config */
  public static final int TARGET_PORT = CONTAINER_TESTS_COMMUNICATION_PORT;
  // The path for the Our agent in the container
  public static final String DEST_AGENT_FILEPATH = "/fso-agent-all.jar";

  /* FSO config */
  private static final boolean FSO_DEBUG = true;
  private static final boolean METADATA_ONLY = false;

  /**
   * Generates map of all Env vars for Our agent
   *
   * @return: Map of Env var to its value
   */
  public static Map<String, String> generateAgentEnvVars() {
    Map<String, String> agentEnvVars = new HashMap<>();
    agentEnvVars.put(
        "JAVA_TOOL_OPTIONS",
        String.format(
            "-javaagent:%s -Dfso.debug=%b -Dotel.instrumentation.ep.enabled=false",
            DEST_AGENT_FILEPATH, FSO_DEBUG));
    agentEnvVars.put("OTEL_BSP_MAX_EXPORT_BATCH_SIZE", "1");
    agentEnvVars.put("OTEL_TRACES_EXPORTER", "otlp");
    agentEnvVars.put("OTEL_BSP_SCHEDULE_DELAY", "10ms");
    agentEnvVars.put("OTEL_EXPORTER_OTLP_ENDPOINT", "http://collector:55680");
    agentEnvVars.put("FSO_METADATA_ONLY", Boolean.toString(METADATA_ONLY));
    // TODO: maybe in the future, get this token from somewhere so we can see the test traces in
    // fso dashboard!
    agentEnvVars.put("FSO_TOKEN", "some-token");

    return agentEnvVars;
  }
}

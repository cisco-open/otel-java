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

package com.fso.agent.smoketest;

import static com.fso.agent.smoketest.config.SmokeTestsConfiguration.*;

import java.time.Duration;
import java.util.Map;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.output.ToStringConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import com.fso.agent.smoketest.config.SmokeTestsConfiguration;

/**
 * A class creating and manage all needed containers for a single smoke test which includes three
 * docker containers: * Collector - An open telemetry collector for receiving traces and spans *
 * Backend - A mock open telemetry backend that receive spans, traces and can expose them for
 * testing * Target - The server app image to test (Changes from test to test)
 */
public class TestContainerManager {
  private boolean started = false;
  private static final Logger logger = LoggerFactory.getLogger(TestContainerManager.class);
  private static final Logger collectorLogger = LoggerFactory.getLogger("Collector");
  private static final Logger backendLogger = LoggerFactory.getLogger("Backend");

  private final Network network = Network.newNetwork();
  private GenericContainer<?> backend = null;
  private GenericContainer<?> collector = null;
  private GenericContainer<?> target = null;

  /** Setup new Collector and Backend for testing */
  protected void startEnvironment() {
    logger.info("Starting Env!");
    backend =
        new GenericContainer<>(DockerImageName.parse(BACKEND_IMAGE_PATH))
            .withExposedPorts(BACKEND_PORT)
            .waitingFor(Wait.forHttp("/health").forPort(BACKEND_PORT))
            .withNetwork(network)
            .withNetworkAliases(BACKEND_ALIAS)
            .withLogConsumer(new Slf4jLogConsumer(backendLogger));
    backend.start();
    logger.info("Backend is up!");

    collector =
        new GenericContainer<>(DockerImageName.parse(COLLECTOR_IMAGE_PATH))
            .dependsOn(backend)
            .withNetwork(network)
            .withNetworkAliases(COLLECTOR_ALIAS)
            .withLogConsumer(new Slf4jLogConsumer(collectorLogger))
            .withCopyFileToContainer(
                MountableFile.forClasspathResource(COLLECTOR_CONFIG_RESOURCE), "/etc/otel.yaml")
            .withCommand("--config /etc/otel.yaml");
    collector.start();
    logger.info("Collector is up!");
  }

  protected void stopEnvironment() {
    if (backend != null) {
      backend.stop();
      backend = null;
    }

    if (collector != null) {
      collector.stop();
      collector = null;
    }

    network.close();
  }

  public int getBackendMappedPort() {
    return backend.getMappedPort(BACKEND_PORT);
  }

  public int getTargetMappedPort(int originalPort) {
    return target.getMappedPort(originalPort);
  }

  public void startEnvironmentOnce() {
    logger.info("Starting env once!");
    if (!started) {
      started = true;
      startEnvironment();
      Runtime.getRuntime().addShutdownHook(new Thread(this::stopEnvironment));
    }
  }

  /**
   * Starts the test app image. Configure and copy the agent to the target image, setup the env vars
   * and start the app
   *
   * @param targetImageName: The Docker image path
   * @param agentPath: The local path to our test agent
   * @param extraEnv: Specific test extra env varibles
   * @param extraResources: Specific test extra resources
   * @param waitStrategy: {@link TargetWaitStrategy} which control the timeout waiting for target
   *     app API's
   * @return The output log of the container {@link Consumer<OutputFrame>}
   */
  public Consumer<OutputFrame> startTarget(
      String targetImageName,
      String agentPath,
      Map<String, String> extraEnv,
      Map<String, String> extraResources,
      TargetWaitStrategy waitStrategy) {

    logger.info("Starting the taregt!");
    Consumer<OutputFrame> output = new ToStringConsumer();
    target =
        new GenericContainer<>(DockerImageName.parse(targetImageName))
            .withStartupTimeout(Duration.ofMinutes(5))
            .withExposedPorts(TARGET_PORT)
            .withNetwork(network)
            .withLogConsumer(output)
            .withLogConsumer(new Slf4jLogConsumer(logger))
            .withCopyFileToContainer(MountableFile.forHostPath(agentPath), DEST_AGENT_FILEPATH)
            .withEnv(SmokeTestsConfiguration.generateAgentEnvVars())
            .withEnv(extraEnv);

    extraResources.forEach(
        (file, path) ->
            target.withCopyFileToContainer(MountableFile.forClasspathResource(file), path));

    if (waitStrategy != null) {
      if (waitStrategy instanceof TargetWaitStrategy.Log) {
        target =
            target.waitingFor(
                Wait.forLogMessage(((TargetWaitStrategy.Log) waitStrategy).regex, 1)
                    .withStartupTimeout(waitStrategy.timeout));
      }
    }
    target.start();
    return output;
  }

  public void stopTarget() {
    if (target != null) {
      target.stop();
      target = null;
    }
  }
}

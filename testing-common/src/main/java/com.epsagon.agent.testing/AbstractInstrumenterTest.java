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

package com.fso.agent.testing;

import io.opentelemetry.instrumentation.testing.junit.AgentInstrumentationExtension;

// TODO: implement this after solve the agent-for-testing-issue
public abstract class AbstractInstrumenterTest {
  static {
    System.setProperty("fso.metadata_only", "true");
    System.setProperty("io.opentelemetry.javaagent.slf4j.simpleLogger.log.muzzleMatcher", "warn");
  }

  static final AgentInstrumentationExtension instrTesting = AgentInstrumentationExtension.create();
}

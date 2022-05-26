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

import static io.opentelemetry.sdk.testing.assertj.OpenTelemetryAssertions.assertThat;

import org.junit.jupiter.api.Test;

class CiscoResourceProviderTest {
  @Test
  void shouldGetDistroVersionFromProperties() {
    // given
    var provider = new CiscoResourceProvider();

    // when
    var resource = provider.createResource(null);

    // then
    assertThat(resource.getAttribute(CiscoResourceProvider.CISCO_DISTRO_VERSION))
        .isEqualTo(CiscoResourceProvider.getAgentVersion());
    assertThat(resource.getAttributes().size()).isEqualTo(1);
  }
}

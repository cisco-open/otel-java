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

package com.cisco.agent.otel.extensions;

import java.util.Collections;
import java.util.List;

import com.google.auto.service.AutoService;

import io.opentelemetry.javaagent.spi.BootstrapPackagesProvider;

@AutoService(BootstrapPackagesProvider.class)
public class CiscoBootstrapPackagesProvider implements BootstrapPackagesProvider {

  @Override
  public List<String> getPackagePrefixes() {
    return Collections.singletonList("com.cisco.agent");
  }
}
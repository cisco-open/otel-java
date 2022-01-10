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

package com.fso.agent.otel.extensions.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fso.agent.otel.extensions.config.FSOConfigurationProvider;
import com.fso.agent.otel.extensions.consts.FSOAgentVersionProvider;
import com.google.auto.service.AutoService;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.sdk.autoconfigure.ConfigProperties;
import io.opentelemetry.sdk.autoconfigure.spi.ResourceProvider;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import io.opentelemetry.sdk.resources.Resource;

@AutoService(ResourceProvider.class)
public class FSOResourceProvider implements ResourceProvider {

  private static final Logger log = LoggerFactory.getLogger(FSOResourceProvider.class.getName());

  @Override
  public Resource createResource(ConfigProperties config) {
    log.debug("Creating new Resource Provider");

    AttributesBuilder builder = Attributes.builder();
    builder.put(ResourceAttributes.SERVICE_NAME, FSOConfigurationProvider.getServiceName());
    builder.put(FSOResourceAttributes.FSO_VERSION.key, FSOAgentVersionProvider.getAgentVersion());
    return Resource.create(builder.build());
  }
}

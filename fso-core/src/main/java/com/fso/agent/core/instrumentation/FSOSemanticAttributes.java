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

package com.fso.agent.core.instrumentation;

import io.opentelemetry.api.common.AttributeKey;

public class FSOSemanticAttributes {
  private FSOSemanticAttributes() {}

  public static final AttributeKey<String> HTTP_REQUEST_HEADERS =
      AttributeKey.stringKey("http.request.headers");

  public static final AttributeKey<String> HTTP_RESPONSE_HEADERS =
      AttributeKey.stringKey("http.response.headers");

  public static final AttributeKey<String> HTTP_REQUEST_PATH =
      AttributeKey.stringKey("http.request.path");

  public static final AttributeKey<String> HTTP_REQUEST_QUERY =
      AttributeKey.stringKey("http.request.query");
}

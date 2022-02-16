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

package com.cisco.agent.core.instrumentation.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.json.JSONObject;

import com.cisco.agent.core.config.CiscoConfiguration;
import com.cisco.agent.core.instrumentation.CiscoSemanticAttributes;

import io.opentelemetry.api.trace.Span;

/**
 * This class responsible to extract properties from HTTP Requests and Responses and add the
 * relevant info to Spans
 */
public abstract class HttpDecorator<REQUEST, RESPONSE> {
  protected final CiscoConfiguration CISCO_CONFIGURATION = CiscoConfiguration.ConfigProvider.get();

  protected abstract String url(REQUEST request);

  protected abstract Map<String, String> requestHeaders(REQUEST request);

  protected abstract Map<String, String> responseHeaders(RESPONSE response);

  protected abstract String serverAddress(REQUEST request);

  /**
   * Create JSONObject from all relevant HTTP request headers
   *
   * @param request: An HTTP request
   */
  protected JSONObject getFilteredRequestHeaders(REQUEST request) {
    JSONObject filteredHeaders = new JSONObject();
    for (Map.Entry<String, String> entry : requestHeaders(request).entrySet()) {
      // TODO: implement ignored keys mechanism
      filteredHeaders.put(entry.getKey(), entry.getValue());
    }

    return filteredHeaders;
  }

  /**
   * Create JSONObject from all relevant HTTP response headers
   *
   * @param response: An HTTP response
   */
  protected JSONObject getFilteredResponseHeaders(RESPONSE response) {
    JSONObject filteredHeaders = new JSONObject();
    for (Map.Entry<String, String> entry : responseHeaders(response).entrySet()) {
      // TODO: implement ignored keys mechanism
      filteredHeaders.put(entry.getKey(), entry.getValue());
    }

    return filteredHeaders;
  }

  /** Returns the new span name according to Cisco specs */
  protected String name(REQUEST request) {
    return serverAddress(request);
  }

  protected URI getUrl(REQUEST request) {
    try {
      return new URI(url(request));
    } catch (URISyntaxException e) {
      return null;
    }
  }

  public void decorateRequest(Span span, REQUEST request) {
    if (CISCO_CONFIGURATION.payloadsEnabled()) {
      span.setAttribute(
          CiscoSemanticAttributes.HTTP_REQUEST_HEADERS,
          getFilteredRequestHeaders(request).toString());
    }
    URI url = getUrl(request);

    if (url != null) {
      span.setAttribute(CiscoSemanticAttributes.HTTP_REQUEST_PATH, url.getPath());
      span.setAttribute(CiscoSemanticAttributes.HTTP_REQUEST_QUERY, url.getQuery());
    }
  }

  public void decorateResponse(Span span, RESPONSE response) {
    if (CISCO_CONFIGURATION.payloadsEnabled()) {
      span.setAttribute(
          CiscoSemanticAttributes.HTTP_RESPONSE_HEADERS,
          getFilteredResponseHeaders(response).toString());
    }
  }
}

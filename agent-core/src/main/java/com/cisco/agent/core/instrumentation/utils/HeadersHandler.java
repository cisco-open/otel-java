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

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cisco.agent.core.config.CiscoConfiguration;
import com.cisco.agent.core.instrumentation.CiscoSemanticAttributes;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.trace.Span;

/** This class responsible to capture and filter HTTP headers and add the relevant info to Spans */
// TODO: merge logic and code dup with HttpDecorator
public final class HeadersHandler {
  private static final Logger log = LoggerFactory.getLogger(HeadersHandler.class);
  private static final CiscoConfiguration ciscoConfiguration =
      CiscoConfiguration.ConfigProvider.get();

  public static void addRequestHeaders(
      Span span, Iterator<?> headerIterator, Class<?> headerClass) {
    addHeaders(span, headerIterator, headerClass, CiscoSemanticAttributes.HTTP_REQUEST_HEADERS);
  }

  public static void addRequestHeaders(Span span, Map<String, String> headersMap) {
    addHeaders(span, headersMap, CiscoSemanticAttributes.HTTP_REQUEST_HEADERS);
  }

  public static void addResponseHeaders(
      Span span, Iterator<?> headerIterator, Class<?> headerClass) {
    addHeaders(span, headerIterator, headerClass, CiscoSemanticAttributes.HTTP_RESPONSE_HEADERS);
  }

  public static void addResponseHeaders(Span span, Map<String, String> headersMap) {
    addHeaders(span, headersMap, CiscoSemanticAttributes.HTTP_RESPONSE_HEADERS);
  }

  /**
   * Add HTTP headers to required span as JSON dump String from Map matching header Name, header
   * Value
   *
   * @param span The span to add the headers to
   * @param headersMap A map matching header Name to Header Value
   * @param headerAttributeKey: The headerAttribute key name
   */
  private static void addHeaders(
      Span span, Map<String, String> headersMap, AttributeKey<String> headerAttributeKey) {
    JSONObject filteredHeaders = new JSONObject();
    for (Map.Entry<String, String> entry : headersMap.entrySet()) {
      // TODO: implement ignored keys mechanism
      filteredHeaders.put(entry.getKey(), entry.getValue());
    }

    span.setAttribute(headerAttributeKey, filteredHeaders.toString(4));
  }

  /**
   * Add HTTP headers to required span as JSON dump String from Iterator of Objects.
   *
   * <p>The method currenty hardcoded checks for declared virtual methods with name: 'getName',
   * 'getValue' as the getters.
   *
   * @param span The span to add the headers to
   * @param headerIterator: Iterator of headers objects
   * @param headerClass: The header class
   * @param headerAttributeKey: The headerAttribute key name
   */
  private static void addHeaders(
      Span span,
      Iterator<?> headerIterator,
      Class<?> headerClass,
      AttributeKey<String> headerAttributeKey) {
    MethodHandles.Lookup lookup = MethodHandles.publicLookup();
    JSONObject filteredHeaders = new JSONObject();

    MethodHandle getName = null;
    MethodHandle getValue = null;
    try {
      getName = lookup.findVirtual(headerClass, "getName", MethodType.methodType(String.class));
      getValue = lookup.findVirtual(headerClass, "getValue", MethodType.methodType(String.class));
    } catch (NoSuchMethodException | IllegalAccessException e) {
      log.warn("Failed to find needed methods: ", e);
      return;
    }

    if (getName == null || getValue == null) {
      log.warn("Failed to find needed methods for header extraction");
      return;
    }

    while (headerIterator.hasNext()) {
      Object header = headerIterator.next();
      String name = null;
      String value = null;
      try {
        name = (String) getName.invoke(header);
        value = (String) getValue.invoke(header);
      } catch (Throwable t) {
        log.warn("Failed to invoke Header getters ", t);
        break;
      }

      // TODO: implement ignored keys mechanism
      filteredHeaders.put(name, value);
    }

    span.setAttribute(headerAttributeKey, filteredHeaders.toString(4));
  }
}

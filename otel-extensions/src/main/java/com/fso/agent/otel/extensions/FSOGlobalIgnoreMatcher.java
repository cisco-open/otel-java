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

package com.fso.agent.otel.extensions;

import com.google.auto.service.AutoService;

import io.opentelemetry.javaagent.spi.IgnoreMatcherProvider;

@AutoService(IgnoreMatcherProvider.class)
public class FSOGlobalIgnoreMatcher implements IgnoreMatcherProvider {

  @Override
  public Result type(net.bytebuddy.description.type.TypeDescription target) {
    String actualName = target.getActualName();

    if (actualName.startsWith("com.yourkit")) {
      return Result.IGNORE;
    }

    if (actualName.startsWith("java.io")) {
      if (actualName.equals("java.io.InputStream")
          || actualName.equals("java.io.OutputStream")
          || actualName.equals("java.io.ByteArrayInputStream")
          || actualName.equals("java.io.ByteArrayOutputStream")
          // servlet request/response body capture instrumentation
          || actualName.equals("java.io.BufferedReader")
          || actualName.equals("java.io.PrintWriter")) {
        return Result.ALLOW;
      }
    }
    return Result.DEFAULT;
  }

  @Override
  public Result classloader(ClassLoader classLoader) {
    // bootstrap
    if (classLoader == null) {
      return Result.DEFAULT;
    }

    String name = classLoader.getClass().getName();
    if (name.startsWith("com.singularity.")
        || name.startsWith("com.yourkit.")
        || name.startsWith("com.cisco.mtagent.")) {
      return Result.IGNORE;
    }
    return Result.DEFAULT;
  }
}

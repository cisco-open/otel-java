/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cisco.agent.smoketest

import org.testcontainers.containers.output.ToStringConsumer

import static java.util.stream.Collectors.toSet

import io.opentelemetry.api.trace.TraceId
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest
import java.util.jar.Attributes
import java.util.jar.JarFile
import okhttp3.Request
import spock.lang.IgnoreIf
import spock.lang.Unroll

@IgnoreIf({ os.windows })
class SpringBootSmokeTest extends SmokeTest {

  protected String getTargetImage(String jdk) {
    "ghcr.io/open-telemetry/java-test-containers:smoke-springboot-jdk$jdk-20210218.577304949"
  }

  @Unroll
  def "spring boot smoke test on JDK #jdk"(int jdk) {
    setup:
    def output = startTarget(jdk)
    String url = "http://localhost:${containerManager.getTargetMappedPort(8080)}/greeting"
    def request = new Request.Builder().url(url).get().build()

    def currentAgentVersion = new JarFile(agentPath).getManifest().getMainAttributes().get(Attributes.Name.IMPLEMENTATION_VERSION).toString()

    when:
    def response = client.newCall(request).execute()
    Collection<ExportTraceServiceRequest> traces = waitForTraces()

    then: "spans are exported"
    response.body().string() == "Hi!"
    countSpansByName(traces, '/greeting') == 1
    countSpansByName(traces, 'WebController.greeting') == 1
    countSpansByName(traces, 'WebController.withSpan') == 1

    then: "correct agent version is captured in the resource"
    [currentAgentVersion] as Set == findResourceAttribute(traces, "telemetry.auto.version")
      .map { it.stringValue }
      .collect(toSet())

    then: "OS is captured in the resource"
    findResourceAttribute(traces, "os.type")
      .map { it.stringValue }
      .findAny()
      .isPresent()

    then: "javaagent logs its version on startup"
    isVersionLogged(output as ToStringConsumer, currentAgentVersion)

    then: "correct traceIds are logged via MDC instrumentation"
    def loggedTraceIds = getLoggedTraceIds(output as ToStringConsumer)
    def spanTraceIds = getSpanStream(traces)
      .map({ TraceId.fromBytes(it.getTraceId().toByteArray()) })
      .collect(toSet())
    loggedTraceIds == spanTraceIds

    cleanup:
    stopTarget()

    where:
    jdk << [11]
  }
}

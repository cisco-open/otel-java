/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.fso.agent.smoketest

import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest
import okhttp3.Request
import spock.lang.IgnoreIf

@IgnoreIf({ os.windows })
class PlaySmokeTest extends SmokeTest {

  protected String getTargetImage(String jdk) {
    "ghcr.io/open-telemetry/java-test-containers:smoke-play-jdk$jdk-20201128.1734635"
  }

  def "play smoke test on JDK #jdk"(int jdk) {
    setup:
    startTarget(jdk)
    String url = "http://localhost:${containerManager.getTargetMappedPort(8080)}/welcome?id=1"
    def request = new Request.Builder().url(url).get().build()

    when:
    def response = client.newCall(request).execute()
    Collection<ExportTraceServiceRequest> traces = waitForTraces()

    then:
    response.body().string() == "Welcome 1."
    //Both play and akka-http support produce spans with the same name.
    //One internal, one SERVER
    countSpansByName(traces, '/welcome') == 2

    cleanup:
    stopTarget()

    where:
    jdk << [11]
  }

}

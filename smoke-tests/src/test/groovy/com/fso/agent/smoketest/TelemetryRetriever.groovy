package com.fso.agent.smoketest


import com.fasterxml.jackson.databind.ObjectMapper
import com.google.protobuf.GeneratedMessageV3
import com.google.protobuf.util.JsonFormat
import io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceRequest
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest
import okhttp3.OkHttpClient
import okhttp3.Request

import java.util.concurrent.TimeUnit
import java.util.function.Supplier

class TelemetryRetriever {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
    protected OkHttpClient client
    protected int backendPort

    TelemetryRetriever(OkHttpClient client, int backendPort) {
        this.client = client
        this.backendPort = backendPort
    }

    void clearTelemetry() {
        client
            .newCall(
                    new Request.Builder()
                    .url("http://localhost:${backendPort}/clear")
                    .build())
            .execute()
            .close()
    }

    String getFromClient(String path) {
        def request = new Request.Builder().url("http://localhost:${backendPort}/${path}").get().build()
        def response = client.newCall(request).execute()
        String responseBody = response.body().string()
        response?.close()
        return responseBody
    }

    Collection<ExportTraceServiceRequest> waitForTraces() {
        return waitForTelemetry("get-traces", { ExportTraceServiceRequest.newBuilder() })
    }

    Collection<ExportMetricsServiceRequest> waitForMetrics() {
        return waitForTelemetry("get-metrics", { ExportMetricsServiceRequest.newBuilder() })
    }

    private <T extends GeneratedMessageV3, B extends GeneratedMessageV3.Builder> Collection<T> waitForTelemetry(String path, Supplier<B> builderConstructor) {
        def content = waitForContent(path)

        return OBJECT_MAPPER.readTree(content).collect {
            def builder = builderConstructor.get()
            JsonFormat.parser().merge(OBJECT_MAPPER.writeValueAsString(it), builder)
            return (T) builder.build()
        }
    }

    private String waitForContent(String path) {
        long previousSize = 0
        long deadline = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30)
        String content = "[]"
        while (System.currentTimeMillis() < deadline) {
            content = getFromClient(path)
            if (content.length() > 2 && content.length() == previousSize) {
                break
            }
            previousSize = content.length()
            println "Curent content size $previousSize"
            TimeUnit.MILLISECONDS.sleep(500)
        }

        return content
    }
}
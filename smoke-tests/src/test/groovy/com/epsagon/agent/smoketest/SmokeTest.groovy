package com.epsagon.agent.smoketest

import io.opentelemetry.instrumentation.test.utils.OkHttpUtils
import io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceRequest
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest
import io.opentelemetry.proto.common.v1.AnyValue
import io.opentelemetry.proto.trace.v1.Span
import okhttp3.OkHttpClient
import org.testcontainers.containers.output.ToStringConsumer
import spock.lang.Shared
import spock.lang.Specification

import java.util.regex.Pattern
import java.util.stream.Stream

import static java.util.stream.Collectors.toSet
import static com.epsagon.agent.smoketest.config.SmokeTestsConfiguration.TARGET_PORT


abstract class SmokeTest extends Specification {
  private static final Pattern TRACE_ID_PATTERN = Pattern.compile(".*trace_id=(?<traceId>[a-zA-Z0-9]+).*")
  protected static final OkHttpClient client = OkHttpUtils.client()

  protected static final TestContainerManager containerManager = new TestContainerManager()

  // A class provides API to get info from the Mock backend
  @Shared
  private TelemetryRetriever telemetryRetriever

  @Shared
  protected String agentPath = System.getProperty("smoketest.javaagent.path")

  /**
   * Subclasses can override this method to customise target application's environment
   */
  protected static Map<String, String> getExtraEnv() {
    return Collections.emptyMap()
  }

  /**
   * Subclasses can override this method to provide additional files to copy to target container
   */
  protected static Map<String, String> getExtraResources() {
    return [:]
  }

  protected static String getUrl(String path, boolean originalPort) {
    int port = originalPort ? TARGET_PORT : containerManager.getTargetMappedPort(TARGET_PORT)
    return String.format("http://localhost:%d%s", port, path)
  }


  def setupSpec() {
    containerManager.startEnvironmentOnce()
    telemetryRetriever = new TelemetryRetriever(client, containerManager.getBackendMappedPort())
  }

  def startTarget(int jdk) {
    startTarget(String.valueOf(jdk), null)
  }

  def startTarget(String jdk, String serverVersion) {
    def targetImage = getTargetImage(jdk, serverVersion)
    return containerManager.startTarget(targetImage, agentPath, extraEnv, extraResources, getWaitStrategy())
  }

  protected abstract String getTargetImage(String jdk)

  protected String getTargetImage(String jdk, String serverVersion) {
    return getTargetImage(jdk)
  }

  protected TargetWaitStrategy getWaitStrategy() {
    return null
  }

  def cleanup() {
    telemetryRetriever.clearTelemetry()
  }

  def stopTarget() {
    containerManager.stopTarget()
  }

  protected static Stream<AnyValue> findResourceAttribute(Collection<ExportTraceServiceRequest> traces,
                                                          String attributeKey) {
    return traces.stream()
            .flatMap { it.getResourceSpansList().stream() }
            .flatMap { it.getResource().getAttributesList().stream() }
            .filter { it.key == attributeKey }
            .map { it.value }
  }

  protected static int countSpansByName(Collection<ExportTraceServiceRequest> traces, String spanName) {
    return getSpanStream(traces).filter { it.name == spanName }.count()
  }

  protected static Stream<Span> getSpanStream(Collection<ExportTraceServiceRequest> traces) {
    return traces.stream()
            .flatMap { it.getResourceSpansList().stream() }
            .flatMap { it.getInstrumentationLibrarySpansList().stream() }
            .flatMap { it.getSpansList().stream() }
  }

  protected Collection<ExportTraceServiceRequest> waitForTraces() {
    return telemetryRetriever.waitForTraces()
  }

  protected Collection<ExportMetricsServiceRequest> waitForMetrics() {
    return telemetryRetriever.waitForMetrics()
  }

  protected static Set<String> getLoggedTraceIds(ToStringConsumer output) {
    output.toUtf8String().lines()
            .flatMap(SmokeTest.&findTraceId)
            .collect(toSet())
  }

  private static Stream<String> findTraceId(String log) {
    def m = TRACE_ID_PATTERN.matcher(log)
    m.matches() ? Stream.of(m.group("traceId")) : Stream.empty() as Stream<String>
  }

  protected static boolean isVersionLogged(ToStringConsumer output, String version) {
    output.toUtf8String().lines()
            .filter({ it.contains("opentelemetry-javaagent - version: " + version) })
            .findFirst()
            .isPresent()
  }
}
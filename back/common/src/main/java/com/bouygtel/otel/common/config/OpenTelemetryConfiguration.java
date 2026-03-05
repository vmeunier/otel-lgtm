package com.bouygtel.otel.common.config;

import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.observation.OpenTelemetryServerRequestObservationConvention;
import org.springframework.util.function.SingletonSupplier;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.jvm.convention.otel.OpenTelemetryJvmClassLoadingMeterConventions;
import io.micrometer.core.instrument.binder.jvm.convention.otel.OpenTelemetryJvmCpuMeterConventions;
import io.micrometer.core.instrument.binder.jvm.convention.otel.OpenTelemetryJvmMemoryMeterConventions;
import io.micrometer.core.instrument.binder.jvm.convention.otel.OpenTelemetryJvmThreadMeterConventions;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.registry.otlp.ExemplarContextProvider;
import io.micrometer.registry.otlp.OtlpConfig;
import io.micrometer.registry.otlp.OtlpExemplarContext;
import io.micrometer.registry.otlp.OtlpMeterRegistry;
import io.micrometer.registry.otlp.OtlpMetricsSender;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;

@Configuration(proxyBeanMethods = false)
public class OpenTelemetryConfiguration {

    @Bean
    OpenTelemetryServerRequestObservationConvention openTelemetryServerRequestObservationConvention() {
        return new OpenTelemetryServerRequestObservationConvention();
    }

    @Bean
    OpenTelemetryJvmCpuMeterConventions openTelemetryJvmCpuMeterConventions() {
        return new OpenTelemetryJvmCpuMeterConventions(Tags.empty());
    }

    @Bean
    ProcessorMetrics processorMetrics() {
        return new ProcessorMetrics(List.of(), new OpenTelemetryJvmCpuMeterConventions(Tags.empty()));
    }

    @Bean
    JvmMemoryMetrics jvmMemoryMetrics() {
        return new JvmMemoryMetrics(List.of(), new OpenTelemetryJvmMemoryMeterConventions(Tags.empty()));
    }

    @Bean
    JvmThreadMetrics jvmThreadMetrics() {
        return new JvmThreadMetrics(List.of(), new OpenTelemetryJvmThreadMeterConventions(Tags.empty()));
    }

    @Bean
    ClassLoaderMetrics classLoaderMetrics() {
        return new ClassLoaderMetrics(new OpenTelemetryJvmClassLoadingMeterConventions());
    }

    @Bean
    OtlpMeterRegistry otlpMeterRegistry(OtlpConfig otlpConfig, Clock clock, ObjectProvider<OtlpMetricsSender> metricsSender, ExemplarContextProvider exemplarContextProvider) {
        OtlpMeterRegistry.Builder builder = OtlpMeterRegistry.builder(otlpConfig)
            .clock(clock)
            .exemplarContextProvider(exemplarContextProvider);
        metricsSender.ifAvailable(builder::metricsSender);
        return builder.build();
    }

    @Bean
    ExemplarContextProvider exemplarContextProvider(ObjectProvider<Tracer> tracerProvider) {
        return new DemoExemplarContextProvider(tracerProvider);
    }

    static class DemoExemplarContextProvider implements ExemplarContextProvider {

        private final SingletonSupplier<Tracer> tracer;

        DemoExemplarContextProvider(ObjectProvider<Tracer> tracerProvider) {
            this.tracer = SingletonSupplier.of(tracerProvider::getObject);
        }

        @Override
        public @Nullable OtlpExemplarContext getExemplarContext() {
            Span span = tracer.obtain().currentSpan();
            if (isSampled(span)) {
                TraceContext context = span.context();
                return new OtlpExemplarContext(context.traceId(), context.spanId());
            }
            return null;
        }

        private boolean isSampled(@Nullable Span span) {
            if (span == null) {
                return false;
            }
            Boolean sampled = span.context().sampled();
            return sampled != null && sampled;
        }
    }
}

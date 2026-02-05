package com.bouygtel.otel.microservices.payment.logging;

import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.logback.appender.v1_0.OpenTelemetryAppender;

@Component
public class LoggingOtlpInstaller {

    private final OpenTelemetry openTelemetry;

    public LoggingOtlpInstaller(OpenTelemetry openTelemetry) {
        this.openTelemetry = openTelemetry;
    }

    @PostConstruct
    public void initOtlp() {
        OpenTelemetryAppender.install(openTelemetry);
    }
}

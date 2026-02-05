package com.bouygtel.otel.common.web.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final ObservationRegistry observationRegistry;

    public GlobalExceptionHandler(ObservationRegistry observationRegistry) {
        this.observationRegistry = observationRegistry;
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ProblemDetail> handlerRuntimeException(RuntimeException ex) {
        final var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        problemDetail.setProperty("message", ex.getMessage());
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        Observation currentObservation = observationRegistry.getCurrentObservation();
        if (currentObservation != null) {
            currentObservation.error(ex);
        }
        log.error("Problème technique détecté", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }
}

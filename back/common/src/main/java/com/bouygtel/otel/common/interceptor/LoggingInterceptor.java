package com.bouygtel.otel.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Intercepteur pour simuler des problèmes de performance et des erreurs
 * afin de générer des logs ERROR et INFO pour les tests de charge
 */
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUri = request.getRequestURI();
        String method = request.getMethod();

        log.info("📥 Incoming request: {} {}", method, requestUri);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String requestUri = request.getRequestURI();
        int status = response.getStatus();

        if (status >= 500) {
            log.error("❌ Request failed: {} - Status: {}", requestUri, status, ex);
        } else if (status >= 400) {
            log.warn("⚠️ Client error: {} - Status: {}", requestUri, status, ex);
        } else {
            log.info("✅ Request successful: {} - Status: {}", requestUri, status);
        }

        if (ex != null) {
            log.error("💣 Exception occurred during request: {}", requestUri, ex);
        }
    }
}

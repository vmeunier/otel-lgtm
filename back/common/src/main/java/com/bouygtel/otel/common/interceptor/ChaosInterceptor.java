package com.bouygtel.otel.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Random;

/**
 * Intercepteur pour simuler des problèmes de performance et des erreurs
 * afin de générer des logs ERROR et INFO pour les tests de charge
 */
@Component
public class ChaosInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(ChaosInterceptor.class);
    private final Random random = new Random();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUri = request.getRequestURI();
        String method = request.getMethod();
        // 5% de chance d'avoir un délai (simulation de lenteur)
        if (random.nextDouble() < 0.05) {
            int delay = 2000 + random.nextInt(3000); // 2-5 secondes
            log.warn("⏱️ SLOW REQUEST DETECTED: {} {} - Artificial delay of {}ms", method, requestUri, delay);
            Thread.sleep(delay);
        }

        // 3% de chance d'avoir une erreur simulée
        if (random.nextDouble() < 0.03) {
            log.error("💥 SIMULATED ERROR: {} {} - Random failure for testing", method, requestUri);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Simulated internal server error for testing\"}");
            response.setContentType("application/json");
            return false;
        }

        // 3% de chance d'avoir un timeout simulé
        if (random.nextDouble() < 0.03) {
            int timeout = 8000 + random.nextInt(4000); // 8-12 secondes
            log.error("⏰ TIMEOUT SIMULATION: {} {} - Delay of {}ms (simulating timeout)", method, requestUri, timeout);
            Thread.sleep(timeout);
        }
        return true;
    }
}

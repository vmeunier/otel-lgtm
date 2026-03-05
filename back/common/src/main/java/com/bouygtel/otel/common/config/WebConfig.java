package com.bouygtel.otel.common.config;


import com.bouygtel.otel.common.filters.MDCCleanerFilter;
import com.bouygtel.otel.common.interceptor.ChaosInterceptor;
import com.bouygtel.otel.common.interceptor.LoggingInterceptor;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration pour enregistrer l'intercepteur de chaos
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final ChaosInterceptor chaosInterceptor;
    private final LoggingInterceptor loggingInterceptor;

    public WebConfig(ChaosInterceptor chaosInterceptor, LoggingInterceptor loggingInterceptor) {
        this.chaosInterceptor = chaosInterceptor;
        this.loggingInterceptor = loggingInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(chaosInterceptor)
                .addPathPatterns("/api/smartphones/**"); // Appliquer à toutes les routes smartphones uniquement

        registry.addInterceptor(loggingInterceptor)
            .addPathPatterns("/api/**"); // Appliquer à toutes les routes API
    }

    @Bean
    public FilterRegistrationBean<MDCCleanerFilter> filtreMdcCleaner() {
        final var frb = new FilterRegistrationBean<MDCCleanerFilter>();
        frb.addUrlPatterns("/api/carts/*");
        frb.setFilter(new MDCCleanerFilter());
        frb.setName("[MDC CLEANER] Cleaner for MDC in REST APIs");
        frb.setOrder(10);
        return frb;
    }
}

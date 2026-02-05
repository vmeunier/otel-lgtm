package com.bouygtel.otel.microservices.order;

import org.jspecify.annotations.Nullable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.client.observation.ClientRequestObservationContext;
import org.springframework.http.client.observation.DefaultClientRequestObservationConvention;
import org.springframework.web.client.RestClient;

import io.micrometer.common.KeyValue;
import io.micrometer.observation.ObservationRegistry;

@SpringBootApplication
@ComponentScan(basePackages = {"com.bouygtel.otel.microservices.order", "com.bouygtel.otel.common"})
public class OrderServiceApplication {

    static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }

    @Bean
    public RestClient restClientCartService(ObservationRegistry observationRegistry) {
        return RestClient.builder().observationRegistry(observationRegistry).observationConvention(new DefaultClientRequestObservationConvention() {

            @Override
            public @Nullable String getContextualName(ClientRequestObservationContext context) {
                String contextualName = super.getContextualName(context);
                return clientName(context).getValue() + " - " + contextualName + " " + uri(context).getValue();
            }

            @Override
            protected KeyValue clientName(ClientRequestObservationContext context) {
                return KeyValue.of("client.name", "cart-service");
            }
        }).build();
    }


    @Bean
    public RestClient restClientInventory(ObservationRegistry observationRegistry) {
        return RestClient.builder().observationRegistry(observationRegistry).observationConvention(new DefaultClientRequestObservationConvention() {

            @Override
            public @Nullable String getContextualName(ClientRequestObservationContext context) {
                String contextualName = super.getContextualName(context);
                return clientName(context).getValue() + " - " + contextualName + " " + uri(context).getValue();
            }

            @Override
            protected KeyValue clientName(ClientRequestObservationContext context) {
                return KeyValue.of("client.name", "inventory-service");
            }
        }).build();
    }

    @Bean
    public RestClient restClientPaymentService(ObservationRegistry observationRegistry) {
        return RestClient.builder().observationRegistry(observationRegistry).observationConvention(new DefaultClientRequestObservationConvention() {

            @Override
            public @Nullable String getContextualName(ClientRequestObservationContext context) {
                String contextualName = super.getContextualName(context);
                return clientName(context).getValue() + " - " + contextualName + " " + uri(context).getValue();
            }

            @Override
            protected KeyValue clientName(ClientRequestObservationContext context) {
                return KeyValue.of("client.name", "payment-service");
            }
        }).build();
    }
}


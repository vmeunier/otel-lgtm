package com.bouygtel.otel.microservices.inventory.dto;

public class QuantityRequest {
    private Integer quantity;

    public QuantityRequest() {
    }

    public QuantityRequest(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}

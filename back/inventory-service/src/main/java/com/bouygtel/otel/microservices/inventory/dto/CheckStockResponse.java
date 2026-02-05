package com.bouygtel.otel.microservices.inventory.dto;

public class CheckStockResponse {
    private boolean available;

    public CheckStockResponse() {
    }

    public CheckStockResponse(boolean available) {
        this.available = available;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}

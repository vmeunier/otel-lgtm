package com.bouygtel.otel.microservices.order.dto;

public class ReduceStockRequestDTO {
    private Integer quantity;

    public ReduceStockRequestDTO() {
    }

    public ReduceStockRequestDTO(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}

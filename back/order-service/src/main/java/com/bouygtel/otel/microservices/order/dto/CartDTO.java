package com.bouygtel.otel.microservices.order.dto;

import java.util.List;

public class CartDTO {
    private Long id;
    private String userId;
    private List<CartItemDTO> items;
    private String status;

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<CartItemDTO> getItems() {
        return items;
    }

    public void setItems(List<CartItemDTO> items) {
        this.items = items;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getTotalPrice() {
        return items != null ? items.stream()
                .mapToDouble(CartItemDTO::getTotalPrice)
                .sum() : 0.0;
    }
}


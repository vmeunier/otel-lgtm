package com.bouygtel.otel.microservices.order.dto;

public class ProcessPaymentRequestDTO {
    private Long cartId;
    private Double amount;
    private String paymentMethod;

    public ProcessPaymentRequestDTO() {
    }

    public ProcessPaymentRequestDTO(Long cartId, Double amount, String paymentMethod) {
        this.cartId = cartId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
    }

    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}

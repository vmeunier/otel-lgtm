package com.bouygtel.otel.microservices.payment.dto;

public class ProcessPaymentRequest {
    private Long cartId;
    private Double amount;
    private String paymentMethod = "CARD";

    public ProcessPaymentRequest() {
    }

    public ProcessPaymentRequest(Long cartId, Double amount, String paymentMethod) {
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

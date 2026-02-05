package com.bouygtel.otel.microservices.order.dto;

public class CreateOrderRequest {
    private String cartId;
    private String paymentMethod = "CARD";

    public CreateOrderRequest() {
    }

    public CreateOrderRequest(String cartId, String paymentMethod) {
        this.cartId = cartId;
        this.paymentMethod = paymentMethod;
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}

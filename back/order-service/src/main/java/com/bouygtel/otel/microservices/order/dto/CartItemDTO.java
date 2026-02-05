package com.bouygtel.otel.microservices.order.dto;

public class CartItemDTO {
    private Long id;
    private Long smartphoneId;
    private String smartphoneBrand;
    private String smartphoneModel;
    private Double smartphonePrice;
    private Integer quantity;

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSmartphoneId() {
        return smartphoneId;
    }

    public void setSmartphoneId(Long smartphoneId) {
        this.smartphoneId = smartphoneId;
    }

    public String getSmartphoneBrand() {
        return smartphoneBrand;
    }

    public void setSmartphoneBrand(String smartphoneBrand) {
        this.smartphoneBrand = smartphoneBrand;
    }

    public String getSmartphoneModel() {
        return smartphoneModel;
    }

    public void setSmartphoneModel(String smartphoneModel) {
        this.smartphoneModel = smartphoneModel;
    }

    public Double getSmartphonePrice() {
        return smartphonePrice;
    }

    public void setSmartphonePrice(Double smartphonePrice) {
        this.smartphonePrice = smartphonePrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getTotalPrice() {
        return smartphonePrice != null && quantity != null ? smartphonePrice * quantity : 0.0;
    }
}


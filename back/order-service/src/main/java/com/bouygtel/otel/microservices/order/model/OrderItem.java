package com.bouygtel.otel.microservices.order.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private Order order;

    @Column(nullable = false)
    private Long smartphoneId;

    @Column(nullable = false)
    private String smartphoneBrand;

    @Column(nullable = false)
    private String smartphoneModel;

    @Column(nullable = false)
    private Double smartphonePrice;

    @Column(nullable = false)
    private Integer quantity;

    // Constructeurs
    public OrderItem() {}

    public OrderItem(Long smartphoneId, String smartphoneBrand, String smartphoneModel,
                    Double smartphonePrice, Integer quantity) {
        this.smartphoneId = smartphoneId;
        this.smartphoneBrand = smartphoneBrand;
        this.smartphoneModel = smartphoneModel;
        this.smartphonePrice = smartphonePrice;
        this.quantity = quantity;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
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
}


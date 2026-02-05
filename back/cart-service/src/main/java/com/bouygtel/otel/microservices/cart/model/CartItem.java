package com.bouygtel.otel.microservices.cart.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    @JsonIgnore
    private Cart cart;

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
    public CartItem() {}

    public CartItem(Long smartphoneId, String smartphoneBrand, String smartphoneModel,
                   Double smartphonePrice, Integer quantity) {
        this.smartphoneId = smartphoneId;
        this.smartphoneBrand = smartphoneBrand;
        this.smartphoneModel = smartphoneModel;
        this.smartphonePrice = smartphonePrice;
        this.quantity = quantity;
    }

    // Méthodes utilitaires
    public Double getTotalPrice() {
        return smartphonePrice * quantity;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
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


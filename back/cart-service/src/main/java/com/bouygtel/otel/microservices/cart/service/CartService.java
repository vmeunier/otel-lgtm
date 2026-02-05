package com.bouygtel.otel.microservices.cart.service;

import com.bouygtel.otel.microservices.cart.exception.CartAccessException;
import com.bouygtel.otel.microservices.cart.model.Cart;
import com.bouygtel.otel.microservices.cart.model.CartItem;
import com.bouygtel.otel.microservices.cart.repository.CartRepository;

import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ObservationRegistry observationRegistry;

    public CartService(CartRepository cartRepository, ObservationRegistry observationRegistry) {
        this.cartRepository = cartRepository;
        this.observationRegistry = observationRegistry;
    }

    public Cart getOrCreateCart(String userId) {
        Optional<Cart> cartOpt = cartRepository.findByUserIdAndStatus(userId, "ACTIVE");

        if (cartOpt.isPresent()) {
            Cart cart = cartOpt.get();
            Observation currentObservation = observationRegistry.getCurrentObservation();
            if (currentObservation != null) {
                currentObservation.highCardinalityKeyValue("ctx.userId", cart.getUserId());
                MDC.put("ctx.userId", cart.getUserId());
            }
            // Vérification spécifique Pixel 8 Pro, histoire de générer des erreurs pour les tests
            boolean hasPixel8Pro = cart.getItems().stream()
                    .anyMatch(item -> "Pixel 8 Pro".equals(item.getSmartphoneModel()));

            if (hasPixel8Pro && Integer.parseInt(cart.getUserId()) >= 8) {
                throw new CartAccessException("Erreur lors de la consultation du panier : impossible de récupérer le panier pour l'utilisateur " + userId);
            }

            return cart;
        } else {
            Cart newCart = new Cart(userId);
            return cartRepository.save(newCart);
        }
    }

    public Optional<Cart> getCartById(Long cartId) {
        Optional<Cart> cartOpt = cartRepository.findById(cartId);

        if (cartOpt.isPresent()) {
            Cart cart = cartOpt.get();
            Observation currentObservation = observationRegistry.getCurrentObservation();
            if (currentObservation != null) {
                currentObservation.highCardinalityKeyValue("ctx.userId", cart.getUserId());
                MDC.put("ctx.userId", cart.getUserId());
            }
            // Vérification spécifique Pixel 8 Pro
            boolean hasPixel8Pro = cart.getItems().stream()
                    .anyMatch(item -> "Pixel 8 Pro".equals(item.getSmartphoneModel()));

            if (hasPixel8Pro && Integer.parseInt(cart.getUserId()) >= 8) {
                throw new CartAccessException("Erreur lors de la consultation du panier : impossible de récupérer le panier avec l'ID " + cartId);
            }
        }

        return cartOpt;
    }

    @Transactional
    public Cart addItemToCart(String userId, CartItem item) {
        Cart cart = getOrCreateCart(userId);

        // Vérifier si l'article existe déjà dans le panier
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(i -> i.getSmartphoneId().equals(item.getSmartphoneId()))
                .findFirst();

        if (existingItem.isPresent()) {
            // Mettre à jour la quantité
            existingItem.get().setQuantity(existingItem.get().getQuantity() + item.getQuantity());
        } else {
            // Ajouter un nouvel article
            cart.addItem(item);
        }

        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart updateItemQuantity(Long cartId, Long itemId, Integer quantity) {
        Optional<Cart> cartOpt = cartRepository.findById(cartId);
        if (cartOpt.isEmpty()) {
            throw new RuntimeException("Panier introuvable");
        }

        Cart cart = cartOpt.get();
        Optional<CartItem> itemOpt = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst();

        if (itemOpt.isEmpty()) {
            throw new RuntimeException("Article introuvable dans le panier");
        }

        if (quantity <= 0) {
            cart.removeItem(itemOpt.get());
        } else {
            itemOpt.get().setQuantity(quantity);
        }

        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }

    @Transactional
    public void removeItemFromCart(Long cartId, Long itemId) {
        Optional<Cart> cartOpt = cartRepository.findById(cartId);
        if (cartOpt.isEmpty()) {
            throw new RuntimeException("Panier introuvable");
        }

        Cart cart = cartOpt.get();
        cart.getItems().removeIf(item -> item.getId().equals(itemId));
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }

    @Transactional
    public void clearCart(Long cartId) {
        Optional<Cart> cartOpt = cartRepository.findById(cartId);
        if (cartOpt.isPresent()) {
            Cart cart = cartOpt.get();
            cart.getItems().clear();
            cart.setUpdatedAt(LocalDateTime.now());
            cartRepository.save(cart);
        }
    }

    @Transactional
    public void markCartAsOrdered(Long cartId) {
        Optional<Cart> cartOpt = cartRepository.findById(cartId);
        if (cartOpt.isPresent()) {
            Cart cart = cartOpt.get();
            cart.setStatus("ORDERED");
            cart.setUpdatedAt(LocalDateTime.now());
            cartRepository.save(cart);
        }
    }
}

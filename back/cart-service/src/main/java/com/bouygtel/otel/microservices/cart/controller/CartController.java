package com.bouygtel.otel.microservices.cart.controller;

import com.bouygtel.otel.microservices.cart.dto.UpdateQuantityRequest;
import com.bouygtel.otel.microservices.cart.model.Cart;
import com.bouygtel.otel.microservices.cart.model.CartItem;
import com.bouygtel.otel.microservices.cart.service.CartService;

import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;

@RestController
@RequestMapping("/api/carts")
@CrossOrigin(origins = "*")
public class CartController {

    private final CartService cartService;
    private final ObservationRegistry observationRegistry;

    public CartController(CartService cartService, ObservationRegistry observationRegistry) {
        this.cartService = cartService;
        this.observationRegistry = observationRegistry;
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<Cart> getCartByUserId(@PathVariable String userId) {
        Cart cart = cartService.getOrCreateCart(userId);
        // Shouldn't be done here, should be done during the authentication phas or at least in a ControllerAdvice.
        // But since we don't manage authentication we can't code that right now
        Observation currentObservation = observationRegistry.getCurrentObservation();
        if (currentObservation != null) {
            currentObservation.highCardinalityKeyValue("ctx.userId", cart.getUserId());
            MDC.put("ctx.userId", cart.getUserId());
        }
        return ResponseEntity.ok(cart);
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<Cart> getCartById(@PathVariable Long cartId) {
        return cartService.getCartById(cartId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/users/{userId}/items")
    public ResponseEntity<Cart> addItemToCart(
            @PathVariable String userId,
            @RequestBody CartItem item) {
        Cart cart = cartService.addItemToCart(userId, item);
        return ResponseEntity.ok(cart);
    }

    @PutMapping("/{cartId}/items/{itemId}")
    public ResponseEntity<Cart> updateItemQuantity(
            @PathVariable Long cartId,
            @PathVariable Long itemId,
            @RequestBody UpdateQuantityRequest request) {

        Integer quantity = request.getQuantity();
        Cart cart = cartService.updateItemQuantity(cartId, itemId, quantity);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/{cartId}/items/{itemId}")
    public ResponseEntity<Void> removeItemFromCart(
            @PathVariable Long cartId,
            @PathVariable Long itemId) {
        cartService.removeItemFromCart(cartId, itemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<Void> clearCart(@PathVariable Long cartId) {
        cartService.clearCart(cartId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{cartId}/mark-ordered")
    public ResponseEntity<Void> markCartAsOrdered(@PathVariable Long cartId) {
        cartService.markCartAsOrdered(cartId);
        return ResponseEntity.ok().build();
    }
}


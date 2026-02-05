package com.bouygtel.otel.microservices.order.controller;

import com.bouygtel.otel.microservices.order.dto.CreateOrderRequest;
import com.bouygtel.otel.microservices.order.model.Order;
import com.bouygtel.otel.microservices.order.service.OrderService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody CreateOrderRequest request) {
        Long cartId = Long.valueOf(request.getCartId());
        String paymentMethod = request.getPaymentMethod() != null ? request.getPaymentMethod() : "CARD";

        try {
            Order order = orderService.createOrder(cartId, paymentMethod);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            LOGGER.error("Impossible de passer commande pour le panier {}", cartId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId) {
        return orderService.getOrderById(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }
}


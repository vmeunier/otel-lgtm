package com.bouygtel.otel.microservices.payment.controller;

import com.bouygtel.otel.microservices.payment.dto.ProcessPaymentRequest;
import com.bouygtel.otel.microservices.payment.model.Payment;
import com.bouygtel.otel.microservices.payment.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<Payment> processPayment(@RequestBody ProcessPaymentRequest request) {
        Long cartId = request.getCartId();
        Double amount = request.getAmount();
        String paymentMethod = request.getPaymentMethod() != null ? request.getPaymentMethod() : "CARD";

        Payment payment = paymentService.processPayment(cartId, amount, paymentMethod);

        if ("SUCCESS".equals(payment.getStatus())) {
            return ResponseEntity.ok(payment);
        } else {
            return ResponseEntity.badRequest().body(payment);
        }
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long paymentId) {
        return paymentService.getPaymentById(paymentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cart/{cartId}")
    public ResponseEntity<List<Payment>> getPaymentsByCartId(@PathVariable Long cartId) {
        List<Payment> payments = paymentService.getPaymentsByCartId(cartId);
        return ResponseEntity.ok(payments);
    }
}


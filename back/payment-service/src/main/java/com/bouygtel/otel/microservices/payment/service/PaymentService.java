package com.bouygtel.otel.microservices.payment.service;

import com.bouygtel.otel.microservices.payment.model.Payment;
import com.bouygtel.otel.microservices.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public Payment processPayment(Long cartId, Double amount, String paymentMethod) {
        Payment payment = new Payment(cartId, amount, paymentMethod);
        payment = paymentRepository.save(payment);

        // Simulation du traitement du paiement
        boolean paymentSuccess = simulatePaymentProcessing(amount, paymentMethod);

        if (paymentSuccess) {
            payment.setStatus("SUCCESS");
            payment.setTransactionId(UUID.randomUUID().toString());
            payment.setProcessedAt(LocalDateTime.now());
        } else {
            payment.setStatus("FAILED");
            payment.setFailureReason("Simulation d'échec de paiement");
            payment.setProcessedAt(LocalDateTime.now());
        }

        return paymentRepository.save(payment);
    }

    public Optional<Payment> getPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId);
    }

    public List<Payment> getPaymentsByCartId(Long cartId) {
        return paymentRepository.findByCartId(cartId);
    }

    /**
     * Simulation du traitement de paiement
     * Dans un vrai système, cela ferait appel à une API de paiement (Stripe, PayPal, etc.)
     */
    private boolean simulatePaymentProcessing(Double amount, String paymentMethod) {
        // Simulation : 95% de succès, 5% d'échec
        // Pour les montants > 10000, taux d'échec plus élevé (simulation de détection de fraude)
        if (amount > 10000) {
            return Math.random() > 0.2; // 80% de succès
        }
        return Math.random() > 0.05; // 95% de succès
    }
}


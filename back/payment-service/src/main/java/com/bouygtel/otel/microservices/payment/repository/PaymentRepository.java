package com.bouygtel.otel.microservices.payment.repository;

import com.bouygtel.otel.microservices.payment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByCartId(Long cartId);
}


package com.bouygtel.otel.microservices.cart.repository;

import com.bouygtel.otel.microservices.cart.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserIdAndStatus(String userId, String status);
}


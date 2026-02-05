package com.bouygtel.otel.microservices.inventory.repository;

import com.bouygtel.otel.microservices.inventory.model.Smartphone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SmartphoneRepository extends JpaRepository<Smartphone, Long> {
}


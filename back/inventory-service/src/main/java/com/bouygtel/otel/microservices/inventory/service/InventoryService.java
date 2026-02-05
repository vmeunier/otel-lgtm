package com.bouygtel.otel.microservices.inventory.service;

import com.bouygtel.otel.microservices.inventory.model.Smartphone;
import com.bouygtel.otel.microservices.inventory.repository.SmartphoneRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {

    private final SmartphoneRepository smartphoneRepository;

    public InventoryService(SmartphoneRepository smartphoneRepository) {
        this.smartphoneRepository = smartphoneRepository;
    }

    public List<Smartphone> getAllSmartphones() {
        return smartphoneRepository.findAll();
    }

    public Optional<Smartphone> getSmartphoneById(Long id) {
        return smartphoneRepository.findById(id);
    }

    @Transactional
    public boolean reduceStock(Long smartphoneId, int quantity) {
        Optional<Smartphone> smartphoneOpt = smartphoneRepository.findById(smartphoneId);
        if (smartphoneOpt.isEmpty()) {
            return false;
        }

        Smartphone smartphone = smartphoneOpt.get();
        if (smartphone.getStock() < quantity) {
            return false; // Stock insuffisant
        }

        smartphone.setStock(smartphone.getStock() - quantity);
        smartphoneRepository.save(smartphone);
        return true;
    }

    @Transactional
    public boolean checkStock(Long smartphoneId, int quantity) {
        Optional<Smartphone> smartphoneOpt = smartphoneRepository.findById(smartphoneId);
        return smartphoneOpt.isPresent() && smartphoneOpt.get().getStock() >= quantity;
    }
}


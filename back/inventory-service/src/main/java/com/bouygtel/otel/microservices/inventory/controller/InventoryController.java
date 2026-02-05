package com.bouygtel.otel.microservices.inventory.controller;

import com.bouygtel.otel.microservices.inventory.dto.CheckStockResponse;
import com.bouygtel.otel.microservices.inventory.dto.QuantityRequest;
import com.bouygtel.otel.microservices.inventory.dto.StockOperationResponse;
import com.bouygtel.otel.microservices.inventory.model.Smartphone;
import com.bouygtel.otel.microservices.inventory.service.InventoryService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/smartphones")
@CrossOrigin(origins = "*")
public class InventoryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryController.class);

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public List<Smartphone> getAllSmartphones() {
        LOGGER.info("Finding all the smartphones in inventory");
        return inventoryService.getAllSmartphones();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Smartphone> getSmartphoneById(@PathVariable Long id) {
        return inventoryService.getSmartphoneById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/reduce-stock")
    public ResponseEntity<StockOperationResponse> reduceStock(
            @PathVariable Long id,
            @RequestBody QuantityRequest request) {

        int quantity = request.getQuantity() != null ? request.getQuantity() : 0;
        boolean success = inventoryService.reduceStock(id, quantity);

        if (success) {
            return ResponseEntity.ok(new StockOperationResponse(true, "Stock réduit avec succès"));
        } else {
            return ResponseEntity.badRequest()
                    .body(new StockOperationResponse(false, "Stock insuffisant ou produit introuvable"));
        }
    }

    @PostMapping("/{id}/check-stock")
    public ResponseEntity<CheckStockResponse> checkStock(
            @PathVariable Long id,
            @RequestBody QuantityRequest request) {

        int quantity = request.getQuantity() != null ? request.getQuantity() : 0;
        boolean available = inventoryService.checkStock(id, quantity);

        return ResponseEntity.ok(new CheckStockResponse(available));
    }
}


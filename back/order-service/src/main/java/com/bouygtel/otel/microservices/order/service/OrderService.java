package com.bouygtel.otel.microservices.order.service;

import com.bouygtel.otel.microservices.order.dto.CartDTO;
import com.bouygtel.otel.microservices.order.dto.CartItemDTO;
import com.bouygtel.otel.microservices.order.dto.PaymentDTO;
import com.bouygtel.otel.microservices.order.dto.ProcessPaymentRequestDTO;
import com.bouygtel.otel.microservices.order.dto.ReduceStockRequestDTO;
import com.bouygtel.otel.microservices.order.dto.StockOperationResponseDTO;
import com.bouygtel.otel.microservices.order.model.Order;
import com.bouygtel.otel.microservices.order.model.OrderItem;
import com.bouygtel.otel.microservices.order.repository.OrderRepository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestClient restClientCartService;
    private final RestClient restClientPayment;
    private final RestClient restClientInventory;

    @Value("${services.cart.url}")
    private String cartServiceUrl;

    @Value("${services.payment.url}")
    private String paymentServiceUrl;

    @Value("${services.inventory.url}")
    private String inventoryServiceUrl;

    public OrderService(OrderRepository orderRepository, @Qualifier("restClientPaymentService") RestClient restClientPayment, @Qualifier("restClientCartService") RestClient restClientCartService,
        @Qualifier("restClientInventory") RestClient restClientInventory) {
        this.orderRepository = orderRepository;
        this.restClientCartService = restClientCartService;
        this.restClientPayment = restClientPayment;
        this.restClientInventory = restClientInventory;
    }

    @Transactional
    public Order createOrder(Long cartId, String paymentMethod) {
        try {
            // Étape 1 : Récupérer le panier
            CartDTO cart = getCart(cartId);
            if (cart == null || cart.getItems().isEmpty()) {
                throw new RuntimeException("Panier vide ou introuvable");
            }

            // Créer la commande
            Order order = new Order(cartId, cart.getTotalPrice());
            order = orderRepository.save(order);

            // Copier les items du panier dans la commande
            for (CartItemDTO cartItem : cart.getItems()) {
                OrderItem orderItem = new OrderItem(
                    cartItem.getSmartphoneId(),
                    cartItem.getSmartphoneBrand(),
                    cartItem.getSmartphoneModel(),
                    cartItem.getSmartphonePrice(),
                    cartItem.getQuantity()
                );
                order.addItem(orderItem);
            }
            order = orderRepository.save(order);

            // Étape 2 : Traiter le paiement
            PaymentDTO payment = processPayment(cartId, cart.getTotalPrice(), paymentMethod);
            order.setPaymentId(payment.getId());

            if (!"SUCCESS".equals(payment.getStatus())) {
                order.setStatus("PAYMENT_FAILED");
                order.setFailureReason("Échec du paiement : " + payment.getFailureReason());
                return orderRepository.save(order);
            }

            order.setStatus("PAYMENT_SUCCESS");
            order = orderRepository.save(order);

            // Étape 3 : Réduire le stock pour chaque article
            boolean allStockReduced = true;
            StringBuilder stockErrors = new StringBuilder();

            for (CartItemDTO item : cart.getItems()) {
                boolean stockReduced = reduceStock(item.getSmartphoneId(), item.getQuantity());
                if (!stockReduced) {
                    allStockReduced = false;
                    stockErrors.append("Stock insuffisant pour ")
                              .append(item.getSmartphoneBrand())
                              .append(" ")
                              .append(item.getSmartphoneModel())
                              .append(". ");
                }
            }

            if (!allStockReduced) {
                order.setStatus("FAILED");
                order.setFailureReason(stockErrors.toString());
                return orderRepository.save(order);
            }

            // Étape 4 : Marquer le panier comme commandé
            markCartAsOrdered(cartId);

            // Finaliser la commande
            order.setStatus("COMPLETED");
            order.setCompletedAt(LocalDateTime.now());

            return orderRepository.save(order);

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la création de la commande : " + e.getMessage(), e);
        }
    }

    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // Appel au service de panier
    private CartDTO getCart(Long cartId) {
        return restClientCartService.get()
                .uri(cartServiceUrl + "/api/carts/" + cartId)
                .retrieve()
                .body(CartDTO.class);
    }

    private void markCartAsOrdered(Long cartId) {
        restClientCartService.post()
                .uri(cartServiceUrl + "/api/carts/" + cartId + "/mark-ordered")
                .retrieve()
                .toBodilessEntity();
    }

    // Appel au service de paiement
    private PaymentDTO processPayment(Long cartId, Double amount, String paymentMethod) {
        ProcessPaymentRequestDTO paymentRequest = new ProcessPaymentRequestDTO(cartId, amount, paymentMethod);

        return restClientPayment.post()
                .uri(paymentServiceUrl + "/api/payments")
                .body(paymentRequest)
                .retrieve()
                .body(PaymentDTO.class);
    }

    // Appel au service d'inventaire
    private boolean reduceStock(Long smartphoneId, Integer quantity) {
        try {
            ReduceStockRequestDTO request = new ReduceStockRequestDTO(quantity);

            StockOperationResponseDTO response = restClientInventory.post()
                    .uri(inventoryServiceUrl + "/api/smartphones/" + smartphoneId + "/reduce-stock")
                    .body(request)
                    .retrieve()
                    .body(StockOperationResponseDTO.class);

            return response != null && response.isSuccess();
        } catch (Exception _) {
            return false;
        }
    }
}


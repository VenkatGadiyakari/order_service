package com.ticketing.orderservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketing.orderservice.entity.Order;
import com.ticketing.orderservice.entity.OrderItem;
import com.ticketing.orderservice.entity.OrderStatus;
import com.ticketing.orderservice.exception.OrderNotFoundException;
import com.ticketing.orderservice.repository.OrderRepository;
import com.ticketing.orderservice.repository.TicketTierRepository;
import com.ticketing.orderservice.util.AuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class PaymentWebhookService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentWebhookService.class);

    private final OrderRepository orderRepository;
    private final TicketTierRepository ticketTierRepository;
    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    public PaymentWebhookService(OrderRepository orderRepository, TicketTierRepository ticketTierRepository,
                                 AuditService auditService, ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.ticketTierRepository = ticketTierRepository;
        this.auditService = auditService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void processWebhook(String payload) {
        try {
            JsonNode root = objectMapper.readTree(payload);
            String eventType = root.get("event").asText();

            if ("payment_link.paid".equals(eventType)) {
                processPaymentLinkPaid(root, eventType);
            } else if ("payment.failed".equals(eventType)) {
                processPaymentFailed(root, eventType);
            }
        } catch (Exception e) {
            logger.error("Failed to parse webhook payload", e);
        }
    }

    private void processPaymentLinkPaid(JsonNode root, String eventType) {
        JsonNode paymentLinkEntity = root.path("payload").path("payment_link").path("entity");
        JsonNode notes = paymentLinkEntity.path("notes");

        if (notes.isMissingNode() || !notes.has("orderId")) {
            logger.error("Webhook missing orderId in payment_link notes");
            return;
        }

        UUID orderId = UUID.fromString(notes.get("orderId").asText());
        String eventId = root.path("payload").path("payment").path("entity").path("id").asText("unknown");
        auditService.logWebhookReceived(eventId, eventType, orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.getStatus() == OrderStatus.CONFIRMED) {
            logger.info("Order {} already confirmed, skipping", orderId);
            return;
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            logger.warn("Order {} is not in PENDING status, current status: {}", orderId, order.getStatus());
            return;
        }

        boolean allDecremented = true;
        for (OrderItem item : order.getItems()) {
            int rowsUpdated = ticketTierRepository.decrementRemainingQty(item.getTierId(), item.getQuantity());
            if (rowsUpdated != 1) {
                allDecremented = false;
                break;
            }
            auditService.logInventoryDecremented(item.getTierId(), item.getQuantity());
        }

        if (allDecremented) {
            order.setStatus(OrderStatus.CONFIRMED);
            order.setUpdatedAt(Instant.now());
            orderRepository.save(order);
            auditService.logOrderConfirmed(orderId);
        } else {
            order.setStatus(OrderStatus.FAILED);
            order.setUpdatedAt(Instant.now());
            orderRepository.save(order);
            auditService.logOrderFailed(orderId, "Insufficient inventory at payment confirmation");
        }
    }

    private void processPaymentFailed(JsonNode root, String eventType) {
        JsonNode paymentEntity = root.path("payload").path("payment").path("entity");
        JsonNode notes = paymentEntity.path("notes");

        if (notes.isMissingNode() || !notes.has("orderId")) {
            logger.error("Webhook missing orderId in payment notes");
            return;
        }

        UUID orderId = UUID.fromString(notes.get("orderId").asText());
        String eventId = paymentEntity.path("id").asText("unknown");
        auditService.logWebhookReceived(eventId, eventType, orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        order.setStatus(OrderStatus.FAILED);
        order.setUpdatedAt(Instant.now());
        orderRepository.save(order);

        auditService.logOrderFailed(orderId, "Payment failed on Razorpay");
    }
}

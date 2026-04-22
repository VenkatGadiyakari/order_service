package com.ticketing.orderservice.service;

import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
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
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentWebhookService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentWebhookService.class);

    private final OrderRepository orderRepository;
    private final TicketTierRepository ticketTierRepository;
    private final AuditService auditService;

    public PaymentWebhookService(OrderRepository orderRepository, TicketTierRepository ticketTierRepository,
                                 AuditService auditService) {
        this.orderRepository = orderRepository;
        this.ticketTierRepository = ticketTierRepository;
        this.auditService = auditService;
    }

    @Transactional
    public void processWebhook(Event event) {
        String eventType = event.getType();

        if ("checkout.session.completed".equals(eventType)) {
            processCheckoutSessionCompleted(event);
        } else if (eventType.contains("payment") && eventType.contains("failed")) {
            processPaymentFailed(event);
        }
    }

    private void processCheckoutSessionCompleted(Event event) {
        Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
        if (session == null) {
            logger.error("Failed to deserialize checkout session from webhook");
            return;
        }

        Map<String, String> metadata = session.getMetadata();
        if (metadata == null || !metadata.containsKey("orderId")) {
            logger.error("Webhook missing orderId in metadata");
            return;
        }

        UUID orderId = UUID.fromString(metadata.get("orderId"));
        auditService.logWebhookReceived(event.getId(), event.getType(), orderId);

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

        OrderItem orderItem = order.getItems().get(0);
        int rowsUpdated = ticketTierRepository.decrementRemainingQty(orderItem.getTierId(), orderItem.getQuantity());

        if (rowsUpdated == 1) {
            order.setStatus(OrderStatus.CONFIRMED);
            order.setUpdatedAt(Instant.now());
            orderRepository.save(order);

            auditService.logInventoryDecremented(orderItem.getTierId(), orderItem.getQuantity());
            auditService.logOrderConfirmed(orderId);
        } else {
            order.setStatus(OrderStatus.FAILED);
            order.setUpdatedAt(Instant.now());
            orderRepository.save(order);

            auditService.logOrderFailed(orderId, "Insufficient inventory at payment confirmation");
        }
    }

    private void processPaymentFailed(Event event) {
        Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
        if (session == null) {
            logger.error("Failed to deserialize session from webhook");
            return;
        }

        Map<String, String> metadata = session.getMetadata();
        if (metadata == null || !metadata.containsKey("orderId")) {
            logger.error("Webhook missing orderId in metadata");
            return;
        }

        UUID orderId = UUID.fromString(metadata.get("orderId"));
        auditService.logWebhookReceived(event.getId(), event.getType(), orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        order.setStatus(OrderStatus.FAILED);
        order.setUpdatedAt(Instant.now());
        orderRepository.save(order);

        auditService.logOrderFailed(orderId, "Payment failed on Stripe");
    }
}

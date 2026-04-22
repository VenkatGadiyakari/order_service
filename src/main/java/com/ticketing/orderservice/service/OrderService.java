package com.ticketing.orderservice.service;

import com.stripe.model.checkout.Session;
import com.ticketing.orderservice.dto.*;
import com.ticketing.orderservice.entity.Order;
import com.ticketing.orderservice.entity.OrderItem;
import com.ticketing.orderservice.entity.OrderStatus;
import com.ticketing.orderservice.exception.*;
import com.ticketing.orderservice.repository.EventRepository;
import com.ticketing.orderservice.repository.OrderItemRepository;
import com.ticketing.orderservice.repository.OrderRepository;
import com.ticketing.orderservice.repository.TicketTierRepository;
import com.ticketing.orderservice.util.AuditService;
import com.ticketing.orderservice.util.Event;
import com.ticketing.orderservice.util.TicketTier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final TicketTierRepository ticketTierRepository;
    private final EventRepository eventRepository;
    private final StripeService stripeService;
    private final AuditService auditService;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                        TicketTierRepository ticketTierRepository, EventRepository eventRepository,
                        StripeService stripeService, AuditService auditService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.ticketTierRepository = ticketTierRepository;
        this.eventRepository = eventRepository;
        this.stripeService = stripeService;
        this.auditService = auditService;
    }

    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest request, UUID buyerId) {
        TicketTier tier = ticketTierRepository.findById(request.getTierId())
                .orElseThrow(() -> new TierNotFoundException(request.getTierId()));

        if (!"ACTIVE".equals(tier.getStatus())) {
            throw new InvalidTierStatusException(tier.getStatus());
        }

        if (tier.getRemainingQty() < request.getQuantity()) {
            throw new InsufficientInventoryException(request.getQuantity(), tier.getRemainingQty());
        }

        if (request.getQuantity() > tier.getMaxPerOrder()) {
            throw new QuantityExceedsMaxPerOrderException(request.getQuantity(), tier.getMaxPerOrder());
        }

        Event event = eventRepository.findById(tier.getEventId())
                .orElseThrow(() -> new TierNotFoundException(request.getTierId()));

        if (!"PUBLISHED".equals(event.getStatus())) {
            throw new InvalidEventStatusException(event.getStatus());
        }

        BigDecimal totalAmount = tier.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));
        UUID orderId = UUID.randomUUID();
        Instant now = Instant.now();

        Order order = new Order();
        order.setId(orderId);
        order.setBuyerId(buyerId);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(totalAmount);
        order.setCreatedAt(now);
        order.setUpdatedAt(now);

        OrderItem orderItem = new OrderItem();
        orderItem.setId(UUID.randomUUID());
        orderItem.setOrder(order);
        orderItem.setTierId(tier.getId());
        orderItem.setTierName(tier.getName());
        orderItem.setEventTitle(event.getTitle());
        orderItem.setEventDate(event.getEventDate());
        orderItem.setQuantity(request.getQuantity());
        orderItem.setUnitPrice(tier.getPrice());
        orderItem.setCreatedAt(now);

        order.addItem(orderItem);
        orderRepository.save(order);

        auditService.logOrderCreated(orderId, buyerId, tier.getId(), request.getQuantity(), totalAmount.toString());

        String successUrl = "http://localhost:3000/orders/" + orderId + "/success";
        String cancelUrl = "http://localhost:3000/events/" + event.getId();

        Long unitPriceInCents = tier.getPrice().multiply(BigDecimal.valueOf(100)).longValue();
        Session session = stripeService.createCheckoutSession(
                orderId,
                tier.getName(),
                request.getQuantity(),
                unitPriceInCents,
                successUrl,
                cancelUrl
        );

        order.setStripeSessionId(session.getId());
        order.setUpdatedAt(Instant.now());
        orderRepository.save(order);

        auditService.logStripeSessionCreated(orderId, session.getId());

        return new CreateOrderResponse(orderId, session.getUrl());
    }

    @Transactional(readOnly = true)
    public OrderHistoryResponse getMyOrders(UUID buyerId, Integer page, Integer size) {
        if (page < 0) {
            page = 0;
        }
        if (size < 1 || size > 100) {
            size = 10;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));
        Page<Order> orderPage = orderRepository.findByBuyerIdAndStatus(buyerId, OrderStatus.CONFIRMED, pageable);

        List<OrderSummary> summaries = orderPage.getContent().stream()
                .map(this::mapToOrderSummary)
                .collect(Collectors.toList());

        return new OrderHistoryResponse(
                summaries,
                orderPage.getNumber(),
                orderPage.getSize(),
                orderPage.getTotalElements(),
                orderPage.getTotalPages()
        );
    }

    @Transactional(readOnly = true)
    public OrderDetailResponse getOrderById(UUID orderId, UUID buyerId) {
        Order order = orderRepository.findByIdAndBuyerId(orderId, buyerId)
                .orElseThrow(() -> {
                    if (orderRepository.existsById(orderId)) {
                        throw new OrderAccessDeniedException(orderId);
                    }
                    throw new OrderNotFoundException(orderId);
                });

        return mapToOrderDetail(order);
    }

    private OrderSummary mapToOrderSummary(Order order) {
        List<OrderItemSummary> items = order.getItems().stream()
                .map(item -> new OrderItemSummary(
                        item.getId(),
                        item.getTierName(),
                        item.getEventTitle(),
                        item.getEventDate(),
                        item.getQuantity(),
                        item.getUnitPrice()
                ))
                .collect(Collectors.toList());

        return new OrderSummary(
                order.getId(),
                order.getStatus().name(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                items
        );
    }

    private OrderDetailResponse mapToOrderDetail(Order order) {
        List<OrderItemDetail> items = order.getItems().stream()
                .map(item -> new OrderItemDetail(
                        item.getId(),
                        item.getTierId(),
                        item.getTierName(),
                        item.getEventTitle(),
                        item.getEventDate(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return new OrderDetailResponse(
                order.getId(),
                order.getStatus().name(),
                order.getTotalAmount(),
                order.getStripeSessionId(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                items
        );
    }
}

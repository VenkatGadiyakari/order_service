package com.ticketing.orderservice.service;

import com.razorpay.PaymentLink;
import com.ticketing.orderservice.client.EventServiceClient;
import com.ticketing.orderservice.dto.*;
import com.ticketing.orderservice.entity.Order;
import com.ticketing.orderservice.entity.OrderItem;
import com.ticketing.orderservice.entity.OrderStatus;
import com.ticketing.orderservice.exception.*;
import com.ticketing.orderservice.repository.OrderItemRepository;
import com.ticketing.orderservice.repository.OrderRepository;
import com.ticketing.orderservice.util.AuditService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final EventServiceClient eventServiceClient;
    private final RazorpayService razorpayService;
    private final AuditService auditService;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                        EventServiceClient eventServiceClient,
                        RazorpayService razorpayService, AuditService auditService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.eventServiceClient = eventServiceClient;
        this.razorpayService = razorpayService;
        this.auditService = auditService;
    }

    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest request, UUID buyerId) {
        EventServiceResponse event = eventServiceClient.getEvent(request.getEventId())
                .orElseThrow(() -> new EventNotFoundException(request.getEventId()));

        if (!"PUBLISHED".equals(event.getStatus())) {
            throw new InvalidEventStatusException(event.getStatus());
        }

        UUID orderId = UUID.randomUUID();
        Instant now = Instant.now();
        BigDecimal totalAmount = BigDecimal.ZERO;

        Order order = new Order();
        order.setId(orderId);
        order.setBuyerId(buyerId);
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(now);
        order.setUpdatedAt(now);

        List<OrderItemResponse> responseItems = new ArrayList<>();

        for (OrderItemRequest itemRequest : request.getItems()) {
            EventServiceResponse.TierResponse tier = event.getTiers().stream()
                    .filter(t -> t.getId().equals(itemRequest.getTierId()))
                    .findFirst()
                    .orElseThrow(() -> new TierNotFoundException(itemRequest.getTierId()));

            if (!"ACTIVE".equals(tier.getStatus())) {
                throw new InvalidTierStatusException(tier.getStatus());
            }

            if (itemRequest.getQuantity() > tier.getMaxPerOrder()) {
                throw new QuantityExceedsMaxPerOrderException(itemRequest.getQuantity(), tier.getMaxPerOrder());
            }

            if (tier.getRemainingQty() < itemRequest.getQuantity()) {
                throw new InsufficientInventoryException(itemRequest.getQuantity(), tier.getRemainingQty());
            }

            BigDecimal itemTotal = tier.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);

            OrderItem orderItem = new OrderItem();
            orderItem.setId(UUID.randomUUID());
            orderItem.setOrder(order);
            orderItem.setTierId(tier.getId());
            orderItem.setTierName(tier.getName());
            orderItem.setEventTitle(event.getTitle());
            orderItem.setEventDate(event.getEventDate());
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setUnitPrice(tier.getPrice());
            orderItem.setCreatedAt(now);
            order.addItem(orderItem);

            responseItems.add(new OrderItemResponse(tier.getId(), itemRequest.getQuantity(), tier.getPrice()));
        }

        order.setTotalAmount(totalAmount);
        orderRepository.save(order);

        auditService.logOrderCreated(orderId, buyerId, request.getItems().size(), totalAmount.toString());

        String successUrl = "http://localhost:3000/orders/" + orderId + "/success";
        String cancelUrl = "http://localhost:3000/events/" + event.getId();

        long totalAmountInPaise = totalAmount.multiply(BigDecimal.valueOf(100)).longValue();
        PaymentLink paymentLink = razorpayService.createPaymentLink(orderId, totalAmountInPaise, successUrl, cancelUrl);

        String paymentLinkId = paymentLink.get("id");
        order.setRazorpayPaymentLinkId(paymentLinkId);
        order.setUpdatedAt(Instant.now());
        orderRepository.save(order);

        auditService.logRazorpayLinkCreated(orderId, paymentLinkId);

        return new CreateOrderResponse(orderId, OrderStatus.PENDING.name(), totalAmount, responseItems);
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
                order.getRazorpayPaymentLinkId(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                items
        );
    }
}

package com.ticketing.orderservice.controller;

import com.ticketing.orderservice.dto.CreateOrderRequest;
import com.ticketing.orderservice.dto.CreateOrderResponse;
import com.ticketing.orderservice.dto.OrderDetailResponse;
import com.ticketing.orderservice.dto.OrderHistoryResponse;
import com.ticketing.orderservice.service.OrderService;
import com.ticketing.orderservice.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final JwtUtil jwtUtil;

    public OrderController(OrderService orderService, JwtUtil jwtUtil) {
        this.orderService = orderService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            @RequestHeader("Authorization") String authorizationHeader) {

        String token = extractToken(authorizationHeader);
        jwtUtil.validateBuyerRole(token);
        UUID buyerId = jwtUtil.getBuyerIdFromToken(token);

        CreateOrderResponse response = orderService.createOrder(request, buyerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/my")
    public ResponseEntity<OrderHistoryResponse> getMyOrders(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestHeader("Authorization") String authorizationHeader) {

        String token = extractToken(authorizationHeader);
        jwtUtil.validateBuyerRole(token);
        UUID buyerId = jwtUtil.getBuyerIdFromToken(token);

        OrderHistoryResponse response = orderService.getMyOrders(buyerId, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDetailResponse> getOrderById(
            @PathVariable UUID id,
            @RequestHeader("Authorization") String authorizationHeader) {

        String token = extractToken(authorizationHeader);
        jwtUtil.validateBuyerRole(token);
        UUID buyerId = jwtUtil.getBuyerIdFromToken(token);

        OrderDetailResponse response = orderService.getOrderById(id, buyerId);
        return ResponseEntity.ok(response);
    }

    private String extractToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new com.ticketing.orderservice.exception.UnauthorizedException("Missing or invalid Authorization header");
        }
        return authorizationHeader.substring(7);
    }
}

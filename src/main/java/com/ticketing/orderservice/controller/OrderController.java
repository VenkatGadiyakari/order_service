package com.ticketing.orderservice.controller;

import com.ticketing.orderservice.dto.CreateOrderRequest;
import com.ticketing.orderservice.dto.CreateOrderResponse;
import com.ticketing.orderservice.dto.OrderDetailResponse;
import com.ticketing.orderservice.dto.OrderHistoryResponse;
import com.ticketing.orderservice.service.OrderService;
import com.ticketing.orderservice.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final JwtUtil jwtUtil;

    public OrderController(OrderService orderService, JwtUtil jwtUtil) {
        this.orderService = orderService;
        this.jwtUtil = jwtUtil;
    }

    @Operation(summary = "Create an order",
            description = "Validates tier availability via event-service, creates the order, and returns a Razorpay payment link. Order starts as PENDING until webhook confirms payment.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Order created — contains Razorpay payment URL"),
        @ApiResponse(responseCode = "400", description = "Validation error or business rule violation (e.g. sold out, max per order exceeded)", content = @Content),
        @ApiResponse(responseCode = "401", description = "Missing or invalid JWT", content = @Content),
        @ApiResponse(responseCode = "404", description = "Event or tier not found", content = @Content)
    })
    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader) {

        String token = extractToken(authorizationHeader);
        jwtUtil.validateBuyerRole(token);
        UUID buyerId = jwtUtil.getBuyerIdFromToken(token);

        CreateOrderResponse response = orderService.createOrder(request, buyerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get my orders",
            description = "Returns paginated order history for the authenticated buyer.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Paginated order history"),
        @ApiResponse(responseCode = "401", description = "Missing or invalid JWT", content = @Content)
    })
    @GetMapping("/my")
    public ResponseEntity<OrderHistoryResponse> getMyOrders(
            @Parameter(description = "Page index (0-based)") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader) {

        String token = extractToken(authorizationHeader);
        jwtUtil.validateBuyerRole(token);
        UUID buyerId = jwtUtil.getBuyerIdFromToken(token);

        OrderHistoryResponse response = orderService.getMyOrders(buyerId, page, size);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get order by ID",
            description = "Returns full order details. Only the buyer who placed the order can access it.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Order details with line items"),
        @ApiResponse(responseCode = "401", description = "Missing or invalid JWT", content = @Content),
        @ApiResponse(responseCode = "403", description = "Order belongs to a different buyer", content = @Content),
        @ApiResponse(responseCode = "404", description = "Order not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderDetailResponse> getOrderById(
            @PathVariable UUID id,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader) {

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

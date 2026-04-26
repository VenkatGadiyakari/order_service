package com.ticketing.orderservice.controller;

import com.ticketing.orderservice.dto.ConfirmOrderRequest;
import com.ticketing.orderservice.dto.FailOrderRequest;
import com.ticketing.orderservice.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class InternalOrderController {

    private final OrderService orderService;

    public InternalOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<Map<String, String>> confirmOrder(
            @PathVariable("id") UUID orderId,
            @RequestBody ConfirmOrderRequest request) {

        String status = orderService.confirmOrder(orderId, request.getPaymentReferenceId());
        return ResponseEntity.ok(Map.of("status", status));
    }

    @PostMapping("/{id}/fail")
    public ResponseEntity<Void> failOrder(
            @PathVariable("id") UUID orderId,
            @RequestBody FailOrderRequest request) {

        orderService.failOrder(orderId, request.getReason());
        return ResponseEntity.ok().build();
    }
}

package com.ticketing.orderservice.controller;

import com.ticketing.orderservice.dto.WebhookResponse;
import com.ticketing.orderservice.exception.PaymentServiceException;
import com.ticketing.orderservice.service.PaymentWebhookService;
import com.ticketing.orderservice.service.RazorpayService;
import com.ticketing.orderservice.util.AuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentWebhookController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentWebhookController.class);

    private final RazorpayService razorpayService;
    private final PaymentWebhookService paymentWebhookService;
    private final AuditService auditService;

    public PaymentWebhookController(RazorpayService razorpayService, PaymentWebhookService paymentWebhookService,
                                    AuditService auditService) {
        this.razorpayService = razorpayService;
        this.paymentWebhookService = paymentWebhookService;
        this.auditService = auditService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<WebhookResponse> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Razorpay-Signature") String sigHeader) {

        try {
            razorpayService.verifyWebhookSignature(payload, sigHeader);
            paymentWebhookService.processWebhook(payload);
            return ResponseEntity.ok(new WebhookResponse(true));
        } catch (PaymentServiceException e) {
            logger.error("Invalid webhook signature", e);
            auditService.logInvalidWebhookSignature("Razorpay webhook");
            return ResponseEntity.badRequest().body(new WebhookResponse(false));
        } catch (Exception e) {
            logger.error("Error processing webhook", e);
            return ResponseEntity.ok(new WebhookResponse(true));
        }
    }
}

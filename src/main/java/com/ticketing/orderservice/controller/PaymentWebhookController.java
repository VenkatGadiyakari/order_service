package com.ticketing.orderservice.controller;

import com.stripe.model.Event;
import com.ticketing.orderservice.dto.WebhookResponse;
import com.ticketing.orderservice.service.PaymentWebhookService;
import com.ticketing.orderservice.service.StripeService;
import com.ticketing.orderservice.util.AuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentWebhookController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentWebhookController.class);

    private final StripeService stripeService;
    private final PaymentWebhookService paymentWebhookService;
    private final AuditService auditService;

    public PaymentWebhookController(StripeService stripeService, PaymentWebhookService paymentWebhookService,
                                    AuditService auditService) {
        this.stripeService = stripeService;
        this.paymentWebhookService = paymentWebhookService;
        this.auditService = auditService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<WebhookResponse> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        try {
            Event event = stripeService.constructEvent(payload, sigHeader);
            paymentWebhookService.processWebhook(event);
            return ResponseEntity.ok(new WebhookResponse(true));
        } catch (com.ticketing.orderservice.exception.StripeServiceException e) {
            logger.error("Invalid webhook signature", e);
            auditService.logInvalidWebhookSignature("Stripe webhook");
            return ResponseEntity.badRequest().body(new WebhookResponse(false));
        } catch (Exception e) {
            logger.error("Error processing webhook", e);
            return ResponseEntity.ok(new WebhookResponse(true));
        }
    }
}

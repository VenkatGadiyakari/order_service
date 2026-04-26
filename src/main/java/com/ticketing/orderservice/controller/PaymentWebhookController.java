package com.ticketing.orderservice.controller;

import com.ticketing.orderservice.dto.WebhookResponse;
import com.ticketing.orderservice.exception.PaymentServiceException;
import com.ticketing.orderservice.service.PaymentWebhookService;
import com.ticketing.orderservice.service.RazorpayService;
import com.ticketing.orderservice.util.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Webhooks", description = "Razorpay webhook receiver — called by Razorpay, not by clients")
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

    @Operation(summary = "Razorpay webhook",
            description = "Receives payment events from Razorpay (payment.captured, payment.failed, etc.). " +
                    "Verifies the HMAC-SHA256 signature and updates order status to CONFIRMED or FAILED.")
    @ApiResponse(responseCode = "200", description = "Webhook acknowledged (always 200 to prevent Razorpay retries)")
    @PostMapping("/webhook")
    public ResponseEntity<WebhookResponse> handleWebhook(
            @RequestBody String payload,
            @Parameter(description = "HMAC-SHA256 signature from Razorpay") @RequestHeader("X-Razorpay-Signature") String sigHeader) {

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

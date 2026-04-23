package com.ticketing.orderservice.service;

import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import com.ticketing.orderservice.exception.PaymentServiceException;
import jakarta.annotation.PostConstruct;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RazorpayService {

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    @Value("${razorpay.webhook.secret}")
    private String webhookSecret;

    private RazorpayClient razorpayClient;

    @PostConstruct
    public void init() {
        try {
            razorpayClient = new RazorpayClient(keyId, keySecret);
        } catch (RazorpayException e) {
            throw new PaymentServiceException("Failed to initialize Razorpay client", e);
        }
    }

    public PaymentLink createPaymentLink(UUID orderId, long totalAmountInPaise, String successUrl, String cancelUrl) {
        try {
            JSONObject notes = new JSONObject();
            notes.put("orderId", orderId.toString());

            JSONObject request = new JSONObject();
            request.put("amount", totalAmountInPaise);
            request.put("currency", "INR");
            request.put("description", "Order " + orderId);
            request.put("notes", notes);
            request.put("callback_url", successUrl);
            request.put("callback_method", "get");
            request.put("cancel_url", cancelUrl);

            return razorpayClient.paymentLink.create(request);
        } catch (RazorpayException e) {
            throw new PaymentServiceException("Failed to create Razorpay payment link", e);
        }
    }

    public void verifyWebhookSignature(String payload, String signature) {
        try {
            boolean valid = Utils.verifyWebhookSignature(payload, signature, webhookSecret);
            if (!valid) {
                throw new PaymentServiceException("Invalid Razorpay webhook signature");
            }
        } catch (RazorpayException e) {
            throw new PaymentServiceException("Webhook signature verification failed", e);
        }
    }
}

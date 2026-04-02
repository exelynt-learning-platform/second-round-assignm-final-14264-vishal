package com.vishal.ecommerce.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.exception.StripeException;
import com.vishal.ecommerce.dto.req.PaymentIntentReqDto;
import com.vishal.ecommerce.dto.res.PaymentIntentResDto;
import com.vishal.ecommerce.service.PaymentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/payments")

public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @PostMapping("/create-intent")
public ResponseEntity<PaymentIntentResDto> createPaymentIntent(@Valid @RequestBody PaymentIntentReqDto request) throws StripeException {
        String username = getCurrentUsername();
        return ResponseEntity.ok(paymentService.createPaymentIntent(request, username));
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        paymentService.handleWebhook(payload, sigHeader);
        return ResponseEntity.ok("Webhook received");
    }
    
}

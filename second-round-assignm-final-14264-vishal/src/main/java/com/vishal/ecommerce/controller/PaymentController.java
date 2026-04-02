package com.vishal.ecommerce.controller;

import com.stripe.exception.StripeException;
import com.vishal.ecommerce.dto.req.PaymentIntentReqDto;
import com.vishal.ecommerce.dto.res.PaymentIntentResDto;
import com.vishal.ecommerce.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @PostMapping("/create-intent")
    public ResponseEntity<PaymentIntentResDto> createPaymentIntent(@Valid @RequestBody PaymentIntentReqDto request) throws StripeException {
        return ResponseEntity.ok(paymentService.createPaymentIntent(request, getCurrentUsername()));
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        paymentService.handleWebhook(payload, sigHeader);
        return ResponseEntity.ok("Webhook received");
    }
}
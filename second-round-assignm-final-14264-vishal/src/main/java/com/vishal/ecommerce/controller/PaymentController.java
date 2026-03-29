package com.vishal.ecommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vishal.ecommerce.service.PaymentService;

@RestController
@RequestMapping("/api/payments")

public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create-intent/{orderId}")
    public ResponseEntity<String> createPaymentIntent(@PathVariable Long orderId) {
        try {
            String clientSecret = paymentService.createPaymentIntent(orderId);
            return ResponseEntity.ok(clientSecret);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/update-status/{orderId}")
public ResponseEntity<String> updatePaymentStatus(@PathVariable Long orderId, @RequestParam String status) {
    paymentService.updatePaymentStatus(orderId, status);
    return ResponseEntity.ok("Payment status updated to " + status);
}
    
}

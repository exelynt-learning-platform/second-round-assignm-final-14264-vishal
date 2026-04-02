package com.vishal.ecommerce.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConfigValidation {

    @Value("${jwt.secret}")  // No colon – property must exist
    private String jwtSecret;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @PostConstruct
    public void validate() {
        if (jwtSecret == null || jwtSecret.trim().isEmpty() || jwtSecret.length() < 32) {
            throw new IllegalStateException("JWT_SECRET environment variable must be set and at least 32 characters");
        }
        if (stripeSecretKey == null || stripeSecretKey.trim().isEmpty()) {
            throw new IllegalStateException("STRIPE_SECRET_KEY environment variable must be set");
        }
    }
}
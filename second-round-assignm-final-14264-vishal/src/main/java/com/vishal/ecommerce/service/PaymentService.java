package com.vishal.ecommerce.service;

import com.stripe.exception.StripeException;
import com.vishal.ecommerce.dto.req.PaymentIntentReqDto;
import com.vishal.ecommerce.dto.res.PaymentIntentResDto;

public interface PaymentService {

    PaymentIntentResDto createPaymentIntent(PaymentIntentReqDto request, String username) throws StripeException;
    void handleWebhook(String payload, String sigHeader);
}

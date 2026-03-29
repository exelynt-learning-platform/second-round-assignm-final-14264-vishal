package com.vishal.ecommerce.service;

public interface PaymentService {

    String createPaymentIntent(Long orderId) throws Exception;
    
    void updatePaymentStatus(Long orderId, String status);
}

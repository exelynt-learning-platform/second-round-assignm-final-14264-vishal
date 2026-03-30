package com.vishal.ecommerce.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.vishal.ecommerce.entity.Order;
import com.vishal.ecommerce.exception.ResourceNotFoundException;
import com.vishal.ecommerce.repository.OrderRepository;
import com.vishal.ecommerce.service.PaymentService;


@Service
public class PaymentServiceImpl implements PaymentService {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public String createPaymentIntent(Long orderId) throws Exception {

        Order order = orderRepository.findById(orderId).orElse(null);

        if (order == null) {
            throw new ResourceNotFoundException("Order not found");
        }

        if (stripeSecretKey == null || stripeSecretKey.isEmpty()) {
    throw new IllegalStateException("Stripe secret key is not configured");
}

        Stripe.apiKey = stripeSecretKey;

        long amount = (long) (order.getTotalPrice() * 100);

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount)
                .setCurrency("usd")
                .build();

        PaymentIntent intent = PaymentIntent.create(params);

        return intent.getClientSecret();
    }

    @Override
public void updatePaymentStatus(Long orderId, String status) {

    Order order = orderRepository.findById(orderId).orElse(null);

    if (order == null) {
        throw new ResourceNotFoundException("Order not found");
    }

    
    order.setPaymentStatus(status);
    orderRepository.save(order);
}
    
}

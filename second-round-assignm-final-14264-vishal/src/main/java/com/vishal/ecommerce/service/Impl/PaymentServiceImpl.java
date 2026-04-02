package com.vishal.ecommerce.service.Impl;

import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import com.vishal.ecommerce.dto.req.PaymentIntentReqDto;
import com.vishal.ecommerce.dto.res.PaymentIntentResDto;
import com.vishal.ecommerce.entity.Order;
import com.vishal.ecommerce.entity.User;
import com.vishal.ecommerce.exception.BadRequestException;
import com.vishal.ecommerce.exception.ResourceNotFoundException;
import com.vishal.ecommerce.repository.OrderRepository;
import com.vishal.ecommerce.repository.UserRepository;
import com.vishal.ecommerce.service.PaymentService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    public PaymentServiceImpl(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void validateStripeKey() {
        if (stripeSecretKey == null || stripeSecretKey.trim().isEmpty()) {
            throw new IllegalStateException("Stripe secret key is not configured. Please set STRIPE_SECRET_KEY environment variable.");
        }
        Stripe.apiKey = stripeSecretKey;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void validateWebhookSecret() {
        String webhookSecret = System.getenv("STRIPE_WEBHOOK_SECRET");
        if (webhookSecret == null || webhookSecret.trim().isEmpty()) {
            // Log warning but don't fail startup – webhook may not be used
            System.err.println("WARNING: STRIPE_WEBHOOK_SECRET not configured. Webhook processing will fail.");
        }
    }

    @Override
    public PaymentIntentResDto createPaymentIntent(PaymentIntentReqDto request, String username) throws com.stripe.exception.StripeException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You are not authorized to pay for this order");
        }

        if (order.getTotalPrice() == null || order.getTotalPrice() <= 0) {
            throw new BadRequestException("Order total must be greater than zero");
        }

        long amount = (long) (order.getTotalPrice() * 100);

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setCurrency("usd")
                .setAmount(amount)
                .setDescription("Order #" + order.getId())
                .putMetadata("orderId", order.getId().toString())
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);
        return new PaymentIntentResDto(paymentIntent.getClientSecret(), "Payment intent created successfully");
    }

    @Override
    @Transactional
    public void handleWebhook(String payload, String sigHeader) {
        String webhookSecret = System.getenv("STRIPE_WEBHOOK_SECRET");
        if (webhookSecret == null || webhookSecret.trim().isEmpty()) {
            throw new BadRequestException("Webhook secret is not configured. Please set STRIPE_WEBHOOK_SECRET environment variable.");
        }
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);

            if ("payment_intent.succeeded".equals(event.getType())) {
                PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().get();
                String orderIdStr = paymentIntent.getMetadata().get("orderId");
                Long orderId = Long.parseLong(orderIdStr);

                Order order = orderRepository.findById(orderId)
                        .orElseThrow(() -> new ResourceNotFoundException("Order not found for webhook"));
                order.setPaymentStatus("PAID");
                orderRepository.save(order);
            }

            if ("payment_intent.payment_failed".equals(event.getType())) {
                PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().get();
                String orderIdStr = paymentIntent.getMetadata().get("orderId");
                Long orderId = Long.parseLong(orderIdStr);

                Order order = orderRepository.findById(orderId)
                        .orElseThrow(() -> new ResourceNotFoundException("Order not found for webhook"));
                order.setPaymentStatus("FAILED");
                orderRepository.save(order);
            }
        } catch (SignatureVerificationException e) {
            throw new BadRequestException("Invalid webhook signature");
        } catch (Exception e) {
            throw new RuntimeException("Webhook processing error: " + e.getMessage(), e);
        }
    }
}
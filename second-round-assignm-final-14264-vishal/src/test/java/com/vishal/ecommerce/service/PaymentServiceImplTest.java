package com.vishal.ecommerce.service;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.vishal.ecommerce.dto.req.PaymentIntentReqDto;
import com.vishal.ecommerce.entity.Order;
import com.vishal.ecommerce.entity.User;
import com.vishal.ecommerce.exception.BadRequestException;
import com.vishal.ecommerce.exception.ResourceNotFoundException;
import com.vishal.ecommerce.repository.OrderRepository;
import com.vishal.ecommerce.repository.UserRepository;
import com.vishal.ecommerce.service.Impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private User user;
    private Order order;
    private PaymentIntentReqDto paymentReq;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setTotalPrice(50.0);
        order.setPaymentStatus("PENDING");

        paymentReq = new PaymentIntentReqDto();
        paymentReq.setOrderId(1L);

        // Set stripe key to avoid validation failure during test
        ReflectionTestUtils.setField(paymentService, "stripeSecretKey", "sk_test_mock");
    }

    @Test
    void createPaymentIntent_success() throws StripeException {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        
        assertDoesNotThrow(() -> paymentService.createPaymentIntent(paymentReq, "testuser"));
    }

    @Test
    void createPaymentIntent_orderNotFound_throws() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentService.createPaymentIntent(paymentReq, "testuser"));
    }

    @Test
    void createPaymentIntent_unauthorized_throws() {
        User otherUser = new User();
        otherUser.setId(2L);
        order.setUser(otherUser);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(BadRequestException.class, () -> paymentService.createPaymentIntent(paymentReq, "testuser"));
    }

    @Test
    void createPaymentIntent_zeroTotal_throws() {
        order.setTotalPrice(0.0);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(BadRequestException.class, () -> paymentService.createPaymentIntent(paymentReq, "testuser"));
    }
}
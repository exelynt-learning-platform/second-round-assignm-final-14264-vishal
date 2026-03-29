package com.vishal.ecommerce.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vishal.ecommerce.entity.Order;
import com.vishal.ecommerce.exception.ResourceNotFoundException;
import com.vishal.ecommerce.repository.OrderRepository;
import com.vishal.ecommerce.service.Impl.PaymentServiceImpl;

public class PaymentServiceImplTest {
    
    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Mock
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdatePaymentStatus_success() {

        Order order = new Order();
        order.setId(1L);
        order.setPaymentStatus("PENDING");

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        paymentService.updatePaymentStatus(1L, "PAID");

        assertEquals("PAID", order.getPaymentStatus());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void testUpdatePaymentStatus_failed() {

        Order order = new Order();
        order.setId(1L);
        order.setPaymentStatus("PENDING");

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        paymentService.updatePaymentStatus(1L, "FAILED");

        assertEquals("FAILED", order.getPaymentStatus());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void testUpdatePaymentStatus_orderNotFound() {

        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentService.updatePaymentStatus(99L, "PAID"));
    }
}

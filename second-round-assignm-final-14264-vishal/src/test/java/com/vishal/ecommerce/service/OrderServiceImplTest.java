package com.vishal.ecommerce.service;

import com.vishal.ecommerce.dto.req.OrderReqDto;
import com.vishal.ecommerce.dto.res.OrderResDto;
import com.vishal.ecommerce.entity.*;
import com.vishal.ecommerce.exception.BadRequestException;
import com.vishal.ecommerce.exception.ResourceNotFoundException;
import com.vishal.ecommerce.repository.*;
import com.vishal.ecommerce.service.Impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartService cartService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User user;
    private Cart cart;
    private Product product;
    private OrderReqDto orderReq;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(20.0);
        product.setStockQuantity(10);

        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);

        cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setItems(new ArrayList<>());
        cart.getItems().add(cartItem);

        orderReq = new OrderReqDto();
        orderReq.setShippingAddress("123 Test St");
    }

    @Test
    void createOrder_fromCart_success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            o.setId(1L);
            return o;
        });
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(new OrderItem());
        doNothing().when(cartService).clearCart("testuser");

        OrderResDto result = orderService.createOrder("testuser", orderReq);

        assertNotNull(result);
        assertEquals(40.0, result.getTotalPrice());
        assertEquals("PENDING", result.getPaymentStatus());
        verify(productRepository, times(1)).save(any(Product.class));
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(cartService, times(1)).clearCart("testuser");
    }

    @Test
    void createOrder_emptyCart_throws() {
        cart.setItems(new ArrayList<>());
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));

        assertThrows(BadRequestException.class, () -> orderService.createOrder("testuser", orderReq));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_insufficientStock_throws() {
        product.setStockQuantity(1);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));

        assertThrows(BadRequestException.class, () -> orderService.createOrder("testuser", orderReq));
    }

    @Test
    void getOrderById_success() {
        Order order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setTotalPrice(40.0);
        order.setPaymentStatus("PENDING");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderResDto result = orderService.getOrderById("testuser", 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(40.0, result.getTotalPrice());
    }

    @Test
    void getOrderById_notFound_throws() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderById("testuser", 99L));
    }

    @Test
    void getOrderById_unauthorized_throws() {
        User otherUser = new User();
        otherUser.setId(2L);
        Order order = new Order();
        order.setId(1L);
        order.setUser(otherUser);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(BadRequestException.class, () -> orderService.getOrderById("testuser", 1L));
    }
}
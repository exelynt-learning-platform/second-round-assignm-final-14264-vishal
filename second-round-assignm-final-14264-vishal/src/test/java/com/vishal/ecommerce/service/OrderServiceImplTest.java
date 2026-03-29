package com.vishal.ecommerce.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vishal.ecommerce.dto.req.OrderReqDto;
import com.vishal.ecommerce.dto.res.OrderResDto;
import com.vishal.ecommerce.entity.Cart;
import com.vishal.ecommerce.entity.CartItem;
import com.vishal.ecommerce.entity.Product;
import com.vishal.ecommerce.entity.User;
import com.vishal.ecommerce.exception.BadRequestException;
import com.vishal.ecommerce.exception.ResourceNotFoundException;
import com.vishal.ecommerce.repository.CartRepository;
import com.vishal.ecommerce.repository.OrderRepository;
import com.vishal.ecommerce.repository.UserRepository;
import com.vishal.ecommerce.service.Impl.OrderServiceImpl;

public class OrderServiceImplTest {
    
    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOrder_fromCart_success() {

        User user = new User();
        user.setUsername("vishal");

        Product product = new Product();
        product.setName("iPhone 15");
        product.setPrice(999.99);

        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(2);

        List<CartItem> items = new ArrayList<>();
        items.add(cartItem);

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setItems(items);

        OrderReqDto request = new OrderReqDto();
        request.setShippingAddress("Pune, Maharashtra");

        when(userRepository.findByUsername("vishal")).thenReturn(user);
        when(cartRepository.findByUser(user)).thenReturn(cart);

        OrderResDto response = orderService.createOrder("vishal", request);

        assertNotNull(response);
        assertEquals("PENDING", response.getPaymentStatus());
        assertEquals(1999.98, response.getTotalPrice(), 0.01);
    }

    @Test
    void testCreateOrder_emptyCart_throws() {

        User user = new User();
        user.setUsername("vishal");

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setItems(new ArrayList<>());

        OrderReqDto request = new OrderReqDto();
        request.setShippingAddress("Pune, Maharashtra");

        when(userRepository.findByUsername("vishal")).thenReturn(user);
        when(cartRepository.findByUser(user)).thenReturn(cart);

        assertThrows(BadRequestException.class, () -> orderService.createOrder("vishal", request));
    }

    @Test
    void testGetOrder_notFound_throws404() {

        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderById("vishal", 99L));
    }

}

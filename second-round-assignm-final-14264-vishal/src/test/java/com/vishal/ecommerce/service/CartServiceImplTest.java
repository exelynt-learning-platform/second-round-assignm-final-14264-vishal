package com.vishal.ecommerce.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vishal.ecommerce.dto.req.CartItemReqDto;
import com.vishal.ecommerce.dto.res.CartResDto;
import com.vishal.ecommerce.entity.Cart;
import com.vishal.ecommerce.entity.CartItem;
import com.vishal.ecommerce.entity.Product;
import com.vishal.ecommerce.entity.User;
import com.vishal.ecommerce.exception.BadRequestException;
import com.vishal.ecommerce.repository.CartItemRepository;
import com.vishal.ecommerce.repository.CartRepository;
import com.vishal.ecommerce.repository.ProductRepository;
import com.vishal.ecommerce.repository.UserRepository;
import com.vishal.ecommerce.service.Impl.CartServiceImpl;

public class CartServiceImplTest {
    
    @InjectMocks
    private CartServiceImpl cartService;

    @Mock
private CartRepository cartRepository;

@Mock
private CartItemRepository cartItemRepository;

@Mock
private ProductRepository productRepository;

@Mock
private UserRepository userRepository;



    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddItem_success() {

        User user = new User();
user.setUsername("vishal");

Product product = new Product();
product.setId(1L);
product.setName("iPhone 15");
product.setPrice(999.99);
product.setStock(10);

Cart cart = new Cart();
cart.setUser(user);
cart.setItems(new ArrayList<>());

CartItemReqDto request = new CartItemReqDto();
request.setProductId(1L);
request.setQuantity(2);

when(userRepository.findByUsername("vishal")).thenReturn(user);
when(cartRepository.findByUser(user)).thenReturn(cart);
when(productRepository.findById(1L)).thenReturn(Optional.of(product));

CartResDto response = cartService.addItem("vishal", request);

assertNotNull(response);
assertEquals(1, response.getItems().size());

    }

    @Test
    void testAddItem_outOfStock_throws() {

        User user = new User();
user.setUsername("vishal");

Product product = new Product();
product.setId(1L);
product.setStock(1);

Cart cart = new Cart();
cart.setUser(user);
cart.setItems(new ArrayList<>());

CartItemReqDto request = new CartItemReqDto();
request.setProductId(1L);
request.setQuantity(10);

when(userRepository.findByUsername("vishal")).thenReturn(user);
when(cartRepository.findByUser(user)).thenReturn(cart);
when(productRepository.findById(1L)).thenReturn(Optional.of(product));

assertThrows(BadRequestException.class, () -> cartService.addItem("vishal", request));
    }

    @Test
    void testRemoveItem_success() {

        CartItem item = new CartItem();
item.setId(1L);

when(cartItemRepository.findById(1L)).thenReturn(Optional.of(item));

cartService.removeItem("vishal", 1L);

verify(cartItemRepository, times(1)).delete(item);
    }

    @Test
    void testClearCart_success() {

        User user = new User();
user.setUsername("vishal");

Cart cart = new Cart();
cart.setUser(user);
cart.setItems(new ArrayList<>());

when(userRepository.findByUsername("vishal")).thenReturn(user);
when(cartRepository.findByUser(user)).thenReturn(cart);

cartService.clearCart("vishal");

verify(cartRepository, times(1)).save(cart);
    }


}

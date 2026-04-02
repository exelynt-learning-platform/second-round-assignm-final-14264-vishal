package com.vishal.ecommerce.service;

import com.vishal.ecommerce.dto.req.CartItemReqDto;
import com.vishal.ecommerce.dto.res.CartResDto;
import com.vishal.ecommerce.entity.*;
import com.vishal.ecommerce.exception.BadRequestException;
import com.vishal.ecommerce.exception.ResourceNotFoundException;
import com.vishal.ecommerce.repository.*;
import com.vishal.ecommerce.service.Impl.CartServiceImpl;
import com.vishal.ecommerce.service.StockValidator;
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
class CartServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StockValidator stockValidator;

    @InjectMocks
    private CartServiceImpl cartService;

    private User user;
    private Product product;
    private Cart cart;
    private CartItemReqDto cartItemReq;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(10.0);
        product.setStockQuantity(10);

        cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setItems(new ArrayList<>());

        cartItemReq = new CartItemReqDto();
        cartItemReq.setProductId(1L);
        cartItemReq.setQuantity(2);
    }

    @Test
    void getCart_success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));

        CartResDto result = cartService.getCart("testuser");

        assertNotNull(result);
        assertEquals(1L, result.getCartId());
        assertTrue(result.getItems().isEmpty());
        assertEquals(0.0, result.getTotalPrice());
    }

    @Test
    void addItemToCart_success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        doNothing().when(stockValidator).validateStock(product, 2);
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(new CartItem());

        CartResDto result = cartService.addItemToCart("testuser", cartItemReq);

        assertNotNull(result);
        verify(stockValidator, times(1)).validateStock(product, 2);
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    void addItemToCart_outOfStock_throws() {
        doThrow(new BadRequestException("Insufficient stock")).when(stockValidator).validateStock(product, 2);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(BadRequestException.class, () -> cartService.addItemToCart("testuser", cartItemReq));
        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    @Test
    void updateCartItem_quantityNull_throws() {
        assertThrows(BadRequestException.class, () -> cartService.updateCartItem("testuser", 1L, null));
    }

    @Test
    void updateCartItem_quantityZero_removesItem() {
        CartItem item = new CartItem();
        item.setId(1L);
        item.setProduct(product);
        item.setQuantity(2);
        cart.getItems().add(item);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));

        CartResDto result = cartService.updateCartItem("testuser", 1L, 0);

        assertNotNull(result);
        verify(cartItemRepository, times(1)).delete(item);
    }

    @Test
    void removeCartItem_success() {
        CartItem item = new CartItem();
        item.setId(1L);
        item.setProduct(product);
        item.setQuantity(2);
        cart.getItems().add(item);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));

        CartResDto result = cartService.removeCartItem("testuser", 1L);

        assertNotNull(result);
        verify(cartItemRepository, times(1)).delete(item);
    }

    @Test
    void clearCart_success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        doNothing().when(cartItemRepository).deleteAll(any());

        assertDoesNotThrow(() -> cartService.clearCart("testuser"));
        verify(cartItemRepository, times(1)).deleteAll(cart.getItems());
    }
}
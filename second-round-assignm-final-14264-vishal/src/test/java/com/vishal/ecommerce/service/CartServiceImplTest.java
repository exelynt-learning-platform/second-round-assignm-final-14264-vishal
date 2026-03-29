package com.vishal.ecommerce.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import com.vishal.ecommerce.service.Impl.CartServiceImpl;

public class CartServiceImplTest {
    
    @InjectMocks
    private CartServiceImpl cartService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddItem_success() {
    }

    @Test
    void testAddItem_outOfStock_throws() {
    }

    @Test
    void testRemoveItem_success() {
    }

    @Test
    void testClearCart_success() {
    }


}

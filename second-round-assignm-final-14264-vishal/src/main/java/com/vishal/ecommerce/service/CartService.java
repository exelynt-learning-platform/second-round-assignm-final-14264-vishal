package com.vishal.ecommerce.service;

import com.vishal.ecommerce.dto.req.CartItemReqDto;
import com.vishal.ecommerce.dto.res.CartResDto;

public interface CartService {

    CartResDto getCart(String username);

    CartResDto addItem(String username, CartItemReqDto request);

    CartResDto updateItem(String username, Long cartItemId, Integer quantity);

    void removeItem(String username, Long cartItemId);

    void clearCart(String username);
    
}

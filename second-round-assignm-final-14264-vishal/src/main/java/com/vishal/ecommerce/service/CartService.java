package com.vishal.ecommerce.service;

import com.vishal.ecommerce.dto.req.CartItemReqDto;
import com.vishal.ecommerce.dto.res.CartResDto;

public interface CartService {

    CartResDto getCart(String username);
    CartResDto addItemToCart(String username, CartItemReqDto request);
    CartResDto updateCartItem(String username, Long itemId, Integer quantity);
    CartResDto removeCartItem(String username, Long itemId);
    
}

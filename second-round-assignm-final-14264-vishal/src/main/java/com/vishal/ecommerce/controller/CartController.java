package com.vishal.ecommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;


import com.vishal.ecommerce.dto.req.CartItemReqDto;
import com.vishal.ecommerce.dto.res.CartResDto;
import com.vishal.ecommerce.service.CartService;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;



    @GetMapping
    public ResponseEntity<CartResDto> getCart(@AuthenticationPrincipal String username) {
        return ResponseEntity.ok(cartService.getCart(username));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResDto> addItem(@AuthenticationPrincipal String username, @RequestBody CartItemReqDto request) {
        return ResponseEntity.ok(cartService.addItem(username, request));
    }

   @PutMapping("/items/{itemId}")
public ResponseEntity<CartResDto> updateItem(@AuthenticationPrincipal String username, @PathVariable Long itemId, @RequestBody CartItemReqDto request) {
    return ResponseEntity.ok(cartService.updateItem(username, itemId, request.getQuantity()));
}

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<String> removeItem(@AuthenticationPrincipal String username, @PathVariable Long itemId) {
        cartService.removeItem(username, itemId);
        return ResponseEntity.ok("Item removed");
    }

    @DeleteMapping
    public ResponseEntity<String> clearCart(@AuthenticationPrincipal String username) {
        cartService.clearCart(username);
        return ResponseEntity.ok("Cart cleared");
    }
    
}

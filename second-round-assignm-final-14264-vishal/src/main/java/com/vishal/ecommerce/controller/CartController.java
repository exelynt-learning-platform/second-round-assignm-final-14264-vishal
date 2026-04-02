package com.vishal.ecommerce.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vishal.ecommerce.dto.req.CartItemReqDto;
import com.vishal.ecommerce.dto.res.CartResDto;
import com.vishal.ecommerce.service.CartService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @GetMapping
    public ResponseEntity<CartResDto> getCart() {
        return ResponseEntity.ok(cartService.getCart(getCurrentUsername()));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResDto> addItem(@Valid @RequestBody CartItemReqDto request) {
        return ResponseEntity.ok(cartService.addItemToCart(getCurrentUsername(), request));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartResDto> updateItem(@PathVariable Long itemId, @RequestParam Integer quantity) {
        return ResponseEntity.ok(cartService.updateCartItem(getCurrentUsername(), itemId, quantity));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartResDto> removeItem(@PathVariable Long itemId) {
        return ResponseEntity.ok(cartService.removeCartItem(getCurrentUsername(), itemId));
    }
}

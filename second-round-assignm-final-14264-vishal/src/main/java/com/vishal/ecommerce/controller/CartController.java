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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vishal.ecommerce.dto.req.CartItemReqDto;
import com.vishal.ecommerce.dto.res.CartResDto;
import com.vishal.ecommerce.service.CartService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @GetMapping
    public ResponseEntity<CartResDto> getCart() {
        String username = getCurrentUsername();
        return ResponseEntity.ok(cartService.getCart(username));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResDto> addItem(@Valid @RequestBody CartItemReqDto request) {
        String username = getCurrentUsername();
        return ResponseEntity.ok(cartService.addItemToCart(username, request));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartResDto> updateItem(@PathVariable Long itemId, @RequestParam Integer quantity) {
        String username = getCurrentUsername();
        return ResponseEntity.ok(cartService.updateCartItem(username, itemId, quantity));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartResDto> removeItem(@PathVariable Long itemId) {
        String username = getCurrentUsername();
        return ResponseEntity.ok(cartService.removeCartItem(username, itemId));
    }
}

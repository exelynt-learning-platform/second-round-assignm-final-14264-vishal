package com.vishal.ecommerce.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vishal.ecommerce.dto.req.OrderReqDto;
import com.vishal.ecommerce.dto.res.OrderResDto;
import com.vishal.ecommerce.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @PostMapping
    public ResponseEntity<OrderResDto> createOrder(@Valid @RequestBody OrderReqDto request) {
        return new ResponseEntity<>(orderService.createOrder(getCurrentUsername(), request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResDto> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(getCurrentUsername(), id));
    }

    @GetMapping
    public ResponseEntity<List<OrderResDto>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrdersForUser(getCurrentUsername()));
    }
}
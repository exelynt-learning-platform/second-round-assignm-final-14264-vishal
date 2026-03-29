package com.vishal.ecommerce.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    private String getUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @PostMapping
    public ResponseEntity<OrderResDto> createOrder(@RequestBody OrderReqDto request) {
        OrderResDto response = orderService.createOrder(getUsername(), request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResDto> getOrderById(@PathVariable Long id) {
        OrderResDto response = orderService.getOrderById(getUsername(), id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<OrderResDto>> getAllOrders() {
        List<OrderResDto> response = orderService.getAllOrders(getUsername());
        return ResponseEntity.ok(response);
    }
    
}

package com.vishal.ecommerce.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

   

    @PostMapping
    public ResponseEntity<OrderResDto> createOrder(@AuthenticationPrincipal String username, @RequestBody OrderReqDto request) {
        return ResponseEntity.ok(orderService.createOrder(username, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResDto> getOrderById(@AuthenticationPrincipal String username,@PathVariable Long id) {

        return ResponseEntity.ok(orderService.getOrderById(username, id));
    }

    @GetMapping
    public ResponseEntity<List<OrderResDto>> getAllOrders(@AuthenticationPrincipal String username) {
        return ResponseEntity.ok(orderService.getAllOrders(username));
    }
    
}

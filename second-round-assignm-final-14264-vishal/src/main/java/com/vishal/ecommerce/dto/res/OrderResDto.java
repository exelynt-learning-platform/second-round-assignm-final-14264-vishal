package com.vishal.ecommerce.dto.res;

import java.time.LocalDateTime;
import java.util.List;

import com.vishal.ecommerce.dto.req.OrderItemResDto;

import lombok.Data;

@Data
public class OrderResDto {

    private Long id;
    private Long userId;
    private String username;
    private LocalDateTime orderDate;
    private String shippingAddress;
    private Double totalPrice;
    private String paymentStatus;
    private List<OrderItemResDto> items;

    public OrderResDto(Long id, Long userId, String username, LocalDateTime orderDate,
                       String shippingAddress, Double totalPrice, String paymentStatus,
                       List<OrderItemResDto> items) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.orderDate = orderDate;
        this.shippingAddress = shippingAddress;
        this.totalPrice = totalPrice;
        this.paymentStatus = paymentStatus;
        this.items = items;
    }
    
}

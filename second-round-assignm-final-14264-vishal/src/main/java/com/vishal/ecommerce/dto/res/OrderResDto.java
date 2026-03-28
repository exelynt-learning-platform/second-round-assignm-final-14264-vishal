package com.vishal.ecommerce.dto.res;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class OrderResDto {

    private Long id;
    private List<String> productNames;
    private Double totalPrice;
    private String shippingAddress;
    private String paymentStatus;
    private LocalDateTime createdAt;
    
}

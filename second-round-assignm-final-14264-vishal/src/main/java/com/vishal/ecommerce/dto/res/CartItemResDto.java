package com.vishal.ecommerce.dto.res;

import lombok.Data;

@Data
public class CartItemResDto {
    
    private Long id;
    private String productName;
    private Double price;
    private Integer quantity;
}

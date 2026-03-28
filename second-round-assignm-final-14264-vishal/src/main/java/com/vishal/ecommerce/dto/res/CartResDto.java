package com.vishal.ecommerce.dto.res;

import java.util.List;

import lombok.Data;

@Data
public class CartResDto {
    

    private Long id;
    private List<CartItemResDto> items;
    private Double totalPrice;
}

package com.vishal.ecommerce.dto.res;

import lombok.Data;

@Data
public class CartItemResDto {
    
    private Long id;
    private Long productId;
    private String productName;
    private Double productPrice;
    private Integer quantity;
    private Double subtotal;

    public CartItemResDto(Long id, Long productId, String productName, Double productPrice, Integer quantity, Double subtotal) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }

}

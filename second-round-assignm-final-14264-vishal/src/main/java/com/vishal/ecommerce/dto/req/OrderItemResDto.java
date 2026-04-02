package com.vishal.ecommerce.dto.req;

public class OrderItemResDto {
 
    
    private Long id;
    private String productName;
    private Double productPrice;
    private Integer quantity;
    private Double subtotal;

    public OrderItemResDto(Long id, String productName, Double productPrice, Integer quantity, Double subtotal) {
        this.id = id;
        this.productName = productName;
        this.productPrice = productPrice;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }
}

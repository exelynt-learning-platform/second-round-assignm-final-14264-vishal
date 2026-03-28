package com.vishal.ecommerce.dto.req;

import lombok.Data;

@Data
public class CartItemReqDto {

    private Long productId;
    private Integer quantity;
    
}

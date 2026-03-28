package com.vishal.ecommerce.dto.res;

import lombok.Data;

@Data
public class ProductResDto {

     private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    
}

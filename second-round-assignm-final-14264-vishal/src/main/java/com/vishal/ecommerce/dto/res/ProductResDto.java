package com.vishal.ecommerce.dto.res;

import lombok.Data;

@Data
public class ProductResDto {

    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer stockQuantity;
    private String imageUrl;

    public ProductResDto(Long id, String name, String description, Double price, Integer stockQuantity, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.imageUrl = imageUrl;
    }
}

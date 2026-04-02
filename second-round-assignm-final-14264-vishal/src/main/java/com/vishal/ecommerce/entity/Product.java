package com.vishal.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "products")
public class Product {
@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    private Double price;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    private String imageUrl;

    @Version
    private Integer version;  // For optimistic locking

    public Product() {}

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Double getPrice() { return price; }
    public Integer getStockQuantity() { return stockQuantity; }
    public String getImageUrl() { return imageUrl; }
    public Integer getVersion() { return version; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(Double price) { this.price = price; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setVersion(Integer version) { this.version = version; }
}

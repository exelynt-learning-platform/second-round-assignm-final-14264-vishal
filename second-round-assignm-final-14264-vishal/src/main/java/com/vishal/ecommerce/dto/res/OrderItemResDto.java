package com.vishal.ecommerce.dto.res;

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

    // Getters
    public Long getId() { return id; }
    public String getProductName() { return productName; }
    public Double getProductPrice() { return productPrice; }
    public Integer getQuantity() { return quantity; }
    public Double getSubtotal() { return subtotal; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setProductName(String productName) { this.productName = productName; }
    public void setProductPrice(Double productPrice) { this.productPrice = productPrice; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }
}
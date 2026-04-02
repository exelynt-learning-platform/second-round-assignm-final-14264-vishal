package com.vishal.ecommerce.dto.res;

import java.util.List;

import lombok.Data;

@Data
public class CartResDto {
    

    private Long cartId;
    private List<CartItemResDto> items;
    private Double totalPrice;

    public CartResDto(Long cartId, List<CartItemResDto> items, Double totalPrice) {
        this.cartId = cartId;
        this.items = items;
        this.totalPrice = totalPrice;
    }

    public Long getCartId() { return cartId; }
    public List<CartItemResDto> getItems() { return items; }
    public Double getTotalPrice() { return totalPrice; }

    public void setCartId(Long cartId) { this.cartId = cartId; }
    public void setItems(List<CartItemResDto> items) { this.items = items; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
}

package com.vishal.ecommerce.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderReqDto {
 
@NotBlank(message = "Shipping address is required")
    private String shippingAddress;

    public OrderReqDto() {}

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }}

package com.vishal.ecommerce.dto.req;

import jakarta.validation.constraints.NotNull;

public class PaymentIntentReqDto {
    
    @NotNull(message = "Order ID is required")
    private Long orderId;

    public PaymentIntentReqDto() {}

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
}


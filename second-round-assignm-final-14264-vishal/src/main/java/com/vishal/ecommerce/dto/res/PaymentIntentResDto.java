package com.vishal.ecommerce.dto.res;

public class PaymentIntentResDto {
    

     private String clientSecret;
    private String message;

    public PaymentIntentResDto(String clientSecret, String message) {
        this.clientSecret = clientSecret;
        this.message = message;
    }

    public String getClientSecret() { return clientSecret; }
    public String getMessage() { return message; }
    public void setClientSecret(String clientSecret) { this.clientSecret = clientSecret; }
    public void setMessage(String message) { this.message = message; }
}

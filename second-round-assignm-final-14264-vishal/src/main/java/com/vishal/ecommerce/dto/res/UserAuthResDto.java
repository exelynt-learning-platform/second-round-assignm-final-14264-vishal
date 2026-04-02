package com.vishal.ecommerce.dto.res;

import lombok.Data;

@Data
public class UserAuthResDto {

   private Long id;
    private String username;
    private String email;
    private String role;
    private String message;

    public UserAuthResDto(Long id, String username, String email, String role, String message) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.message = message;
    }
}
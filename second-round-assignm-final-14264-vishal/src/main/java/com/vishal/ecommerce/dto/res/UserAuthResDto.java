package com.vishal.ecommerce.dto.res;

import lombok.Data;

@Data
public class UserAuthResDto {

  private Long id;
    private String username;
    private String email;
    private String role;
    private String token;
    private String message;

    // Constructor with all 6 fields
    public UserAuthResDto(Long id, String username, String email, String role, String token, String message) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.token = token;
        this.message = message;
    }

}
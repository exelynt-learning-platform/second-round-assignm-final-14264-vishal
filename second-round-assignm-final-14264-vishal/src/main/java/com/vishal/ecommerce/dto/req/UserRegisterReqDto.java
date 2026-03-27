package com.vishal.ecommerce.dto.req;

import lombok.Data;

@Data
public class UserRegisterReqDto {

    private String username;
    private String email;
    private String password;
}
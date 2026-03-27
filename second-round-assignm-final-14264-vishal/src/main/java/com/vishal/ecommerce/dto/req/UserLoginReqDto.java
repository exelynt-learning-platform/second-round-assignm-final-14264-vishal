package com.vishal.ecommerce.dto.req;

import lombok.Data;

@Data
public class UserLoginReqDto {

    private String email;
    private String password;
}
package com.vishal.ecommerce.dto.res;

import lombok.Data;

@Data
public class UserAuthResDto {

    private String token;
    private String username;
}
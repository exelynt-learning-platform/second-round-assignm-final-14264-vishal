package com.vishal.ecommerce.service;

import com.vishal.ecommerce.dto.req.UserRegisterReqDto;
import com.vishal.ecommerce.dto.res.UserAuthResDto;

public interface UserService {

    UserAuthResDto registerUser(UserRegisterReqDto request);

}
package com.vishal.ecommerce.service;

import com.vishal.ecommerce.dto.req.UserLoginReqDto;
import com.vishal.ecommerce.dto.req.UserRegisterReqDto;
import com.vishal.ecommerce.dto.res.UserAuthResDto;

public interface UserService {

    UserAuthResDto register(UserRegisterReqDto request);

    UserAuthResDto login(UserLoginReqDto request);
}
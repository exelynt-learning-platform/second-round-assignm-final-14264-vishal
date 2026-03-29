package com.vishal.ecommerce.controller;

import com.vishal.ecommerce.dto.req.UserLoginReqDto;
import com.vishal.ecommerce.dto.req.UserRegisterReqDto;
import com.vishal.ecommerce.dto.res.UserAuthResDto;
import com.vishal.ecommerce.service.UserService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserAuthResDto> register( @Valid @RequestBody UserRegisterReqDto request) {
        UserAuthResDto response = userService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<UserAuthResDto> login(@Valid @RequestBody UserLoginReqDto request) {
        UserAuthResDto response = userService.login(request);
        return ResponseEntity.ok(response);
    }
}
package com.vishal.ecommerce.controller;

import com.vishal.ecommerce.dto.req.UserLoginReqDto;
import com.vishal.ecommerce.dto.req.UserRegisterReqDto;
import com.vishal.ecommerce.dto.res.UserAuthResDto;
import com.vishal.ecommerce.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserAuthResDto> register(@Valid @RequestBody UserRegisterReqDto request) {
        UserAuthResDto response = userService.registerUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UserAuthResDto> login(@Valid @RequestBody UserLoginReqDto request) {
        UserAuthResDto response = userService.loginUser(request);
        return ResponseEntity.ok(response);
    }
}
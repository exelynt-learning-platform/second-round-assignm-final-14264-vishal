package com.vishal.ecommerce.service.Impl;

import com.vishal.ecommerce.dto.req.UserLoginReqDto;
import com.vishal.ecommerce.dto.req.UserRegisterReqDto;
import com.vishal.ecommerce.dto.res.UserAuthResDto;
import com.vishal.ecommerce.entity.User;
import com.vishal.ecommerce.exception.BadRequestException;
import com.vishal.ecommerce.repository.UserRepository;
import com.vishal.ecommerce.security.JwtUtil;
import com.vishal.ecommerce.service.UserService;

import lombok.RequiredArgsConstructor;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

     private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


   

    @Override
    public UserAuthResDto registerUser(UserRegisterReqDto request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("ROLE_USER");

        User saved = userRepository.save(user);

        return new UserAuthResDto(
            saved.getId(),
            saved.getUsername(),
            saved.getEmail(),
            saved.getRole(),
            null,
            "Registration successful"
        );
    }

    @Override
    public UserAuthResDto loginUser(UserLoginReqDto request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadRequestException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        return new UserAuthResDto(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getRole(),
            token,
            "Login successful"
        );
    }
}

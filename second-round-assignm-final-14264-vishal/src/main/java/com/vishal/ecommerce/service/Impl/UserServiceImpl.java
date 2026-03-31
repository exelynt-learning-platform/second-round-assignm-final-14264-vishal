package com.vishal.ecommerce.service.Impl;

import com.vishal.ecommerce.dto.req.UserLoginReqDto;
import com.vishal.ecommerce.dto.req.UserRegisterReqDto;
import com.vishal.ecommerce.dto.res.UserAuthResDto;
import com.vishal.ecommerce.entity.User;
import com.vishal.ecommerce.exception.BadRequestException;
import com.vishal.ecommerce.exception.UnauthorizedException;
import com.vishal.ecommerce.repository.UserRepository;
import com.vishal.ecommerce.service.UserService;
import com.vishal.ecommerce.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public UserAuthResDto register(UserRegisterReqDto request) {

    if (userRepository.findByUsername(request.getUsername()) != null) {
        throw new BadRequestException("Username already exists");
    }

    if (userRepository.findByEmail(request.getEmail()) != null) {
        throw new BadRequestException("Email already registered");
    }

    User user = new User();
    user.setUsername(request.getUsername());

        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("ROLE_USER");

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        UserAuthResDto response = new UserAuthResDto();
        response.setUsername(user.getUsername());
        response.setToken(token);

        return response;
    }

    @Override
    public UserAuthResDto login(UserLoginReqDto request) {

        User user = userRepository.findByEmail(request.getEmail());

        if (user == null) {
            throw new UnauthorizedException("User not found");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Wrong password");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        UserAuthResDto response = new UserAuthResDto();
        response.setUsername(user.getUsername());
        response.setToken(token);

        return response;
    }
}
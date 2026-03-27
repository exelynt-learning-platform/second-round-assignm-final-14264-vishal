package com.vishal.ecommerce.service.Impl;

import com.vishal.ecommerce.dto.req.UserLoginReqDto;
import com.vishal.ecommerce.dto.req.UserRegisterReqDto;
import com.vishal.ecommerce.dto.res.UserAuthResDto;
import com.vishal.ecommerce.entity.User;
import com.vishal.ecommerce.repository.UserRepository;
import com.vishal.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserAuthResDto register(UserRegisterReqDto request) {

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("ROLE_USER");

        userRepository.save(user);

        UserAuthResDto response = new UserAuthResDto();
        response.setUsername(user.getUsername());

        return response;
    }

    @Override
    public UserAuthResDto login(UserLoginReqDto request) {

        User user = userRepository.findByEmail(request.getEmail());

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Wrong password");
        }

        UserAuthResDto response = new UserAuthResDto();
        response.setUsername(user.getUsername());

        return response;
    }
}
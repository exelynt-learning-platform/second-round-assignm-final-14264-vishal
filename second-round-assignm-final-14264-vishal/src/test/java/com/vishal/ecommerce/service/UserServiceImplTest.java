package com.vishal.ecommerce.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.vishal.ecommerce.dto.req.UserLoginReqDto;
import com.vishal.ecommerce.dto.req.UserRegisterReqDto;
import com.vishal.ecommerce.dto.res.UserAuthResDto;
import com.vishal.ecommerce.entity.User;
import com.vishal.ecommerce.exception.UnauthorizedException;
import com.vishal.ecommerce.repository.UserRepository;
import com.vishal.ecommerce.security.JwtUtil;
import com.vishal.ecommerce.service.Impl.UserServiceImpl;

public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser_success() {

        UserRegisterReqDto request = new UserRegisterReqDto();
        request.setUsername("vishal");
        request.setEmail("vishal@gmail.com");
        request.setPassword("123456");

        when(passwordEncoder.encode("123456")).thenReturn("hashedpassword");
        when(jwtUtil.generateToken("vishal")).thenReturn("mocktoken");

        UserAuthResDto response = userService.register(request);

        assertNotNull(response);
        assertEquals("vishal", response.getUsername());
        assertEquals("mocktoken", response.getToken());
    }

    @Test
    void testLogin_validCredentials_returnsJWT() {

        UserLoginReqDto request = new UserLoginReqDto();
        request.setEmail("vishal@gmail.com");
        request.setPassword("123456");

        User user = new User();
        user.setUsername("vishal");
        user.setPassword("hashedpassword");

        when(userRepository.findByEmail("vishal@gmail.com")).thenReturn(user);
        when(passwordEncoder.matches("123456", "hashedpassword")).thenReturn(true);
        when(jwtUtil.generateToken("vishal")).thenReturn("mocktoken");

        UserAuthResDto response = userService.login(request);

        assertNotNull(response);
        assertEquals("vishal", response.getUsername());
        assertEquals("mocktoken", response.getToken());
    }

    @Test
    void testLogin_wrongPassword_throws401() {

        UserLoginReqDto request = new UserLoginReqDto();
        request.setEmail("vishal@gmail.com");
        request.setPassword("wrongpassword");

        User user = new User();
        user.setUsername("vishal");
        user.setPassword("hashedpassword");

        when(userRepository.findByEmail("vishal@gmail.com")).thenReturn(user);
        when(passwordEncoder.matches("wrongpassword", "hashedpassword")).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> userService.login(request));
    }

    @Test
    void testLogin_userNotFound_throws401() {

        UserLoginReqDto request = new UserLoginReqDto();
        request.setEmail("unknown@gmail.com");
        request.setPassword("123456");

        when(userRepository.findByEmail("unknown@gmail.com")).thenReturn(null);

        assertThrows(UnauthorizedException.class, () -> userService.login(request));
    }
    
}

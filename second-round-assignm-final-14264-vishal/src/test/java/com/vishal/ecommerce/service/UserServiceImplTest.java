package com.vishal.ecommerce.service;

import com.vishal.ecommerce.dto.req.UserLoginReqDto;
import com.vishal.ecommerce.dto.req.UserRegisterReqDto;
import com.vishal.ecommerce.dto.res.UserAuthResDto;
import com.vishal.ecommerce.entity.User;
import com.vishal.ecommerce.exception.BadRequestException;
import com.vishal.ecommerce.repository.UserRepository;
import com.vishal.ecommerce.security.JwtUtil;
import com.vishal.ecommerce.service.Impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRegisterReqDto registerReq;
    private UserLoginReqDto loginReq;
    private User user;

    @BeforeEach
    void setUp() {
        registerReq = new UserRegisterReqDto();
        registerReq.setUsername("testuser");
        registerReq.setEmail("test@example.com");
        registerReq.setPassword("password123");

        loginReq = new UserLoginReqDto();
        loginReq.setUsername("testuser");
        loginReq.setPassword("password123");

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setRole("ROLE_USER");
    }

    @Test
    void registerUser_success() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserAuthResDto result = userService.registerUser(registerReq);

        assertNotNull(result);
        assertEquals("Registration successful", result.getMessage());
        assertEquals("testuser", result.getUsername());
        assertNull(result.getToken());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_duplicateUsername_throws() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> userService.registerUser(registerReq));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_duplicateEmail_throws() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> userService.registerUser(registerReq));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginUser_success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("testuser", "ROLE_USER")).thenReturn("jwt-token");

        UserAuthResDto result = userService.loginUser(loginReq);

        assertNotNull(result);
        assertEquals("Login successful", result.getMessage());
        assertEquals("jwt-token", result.getToken());
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void loginUser_wrongPassword_throws() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(false);

        assertThrows(BadRequestException.class, () -> userService.loginUser(loginReq));
    }

    @Test
    void loginUser_userNotFound_throws() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> userService.loginUser(loginReq));
    }
}
package com.vishal.ecommerce.config;

import com.vishal.ecommerce.repository.UserRepository;
import com.vishal.ecommerce.security.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private UserRepository userRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            com.vishal.ecommerce.entity.User user = userRepository.findByUsername(username);
            if (user == null) {
                throw new UsernameNotFoundException("User not found");

            }
           String role = user.getRole();

if (role == null || role.isBlank()) {
    role = "ROLE_USER";
}  else {
    role = role.trim().toUpperCase();
    if (!role.startsWith("ROLE_")) {
        role = "ROLE_" + role;
    }
}
if (!role.equals("ROLE_USER") && !role.equals("ROLE_ADMIN")) {
    throw new IllegalArgumentException("Invalid role: " + role);
}
    


            return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
        .password(user.getPassword())
        .authorities(role)
        .build();

        };
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable());

        http.securityContext(context -> context.requireExplicitSave(false));

        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/products/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/products/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/products/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")
                .requestMatchers("/api/payments/**").authenticated()
                .anyRequest().authenticated());

  

        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
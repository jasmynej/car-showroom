package com.slingshot.carshowroom.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Security configuration for password encoding.
 * Only uses BCrypt encoder, does not enable Spring Security filter chain.
 */
@Configuration
public class SecurityConfig {
    
    /**
     * BCrypt password encoder bean for secure password hashing.
     * @return PasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

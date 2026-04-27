package com.slingshot.carshowroom.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Disable Spring Security's default filter chain to avoid locking down endpoints
        // We only use BCryptPasswordEncoder for password hashing
        http
            .csrf(csrf -&gt; csrf.disable())
            .authorizeHttpRequests(auth -&gt; auth.anyRequest().permitAll());
        return http.build();
    }
}

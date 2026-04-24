package com.slingshot.carshowroom.service;

import com.slingshot.carshowroom.dto.LoginRequest;
import com.slingshot.carshowroom.dto.LoginResponse;
import com.slingshot.carshowroom.exception.InvalidCredentialsException;
import com.slingshot.carshowroom.model.User;
import com.slingshot.carshowroom.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse authenticate(LoginRequest request) {
        // 1. Find user by userId
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -&gt; new InvalidCredentialsException("Invalid credentials"));

        // 2. Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        // 3. Verify role matches
        if (!user.getRole().name().equals(request.getRole())) {
            throw new InvalidCredentialsException("Invalid credentials or role mismatch");
        }

        // 4. Return success response
        return new LoginResponse(user.getUserId(), user.getName(), user.getRole().name());
    }
}

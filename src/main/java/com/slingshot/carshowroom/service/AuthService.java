package com.slingshot.carshowroom.service;

import com.slingshot.carshowroom.dto.LoginRequest;
import com.slingshot.carshowroom.dto.LoginResponse;
import com.slingshot.carshowroom.exception.UnauthorizedException;
import com.slingshot.carshowroom.model.User;
import com.slingshot.carshowroom.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse authenticate(LoginRequest request) {
        // Find user by userId
        User user = userRepository.findByUserId(request.userId())
                .orElseThrow(() -&gt; new UnauthorizedException("Invalid credentials or role mismatch"));

        // Verify password
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials or role mismatch");
        }

        // Verify role matches
        if (!user.getRole().name().equalsIgnoreCase(request.role())) {
            throw new UnauthorizedException("Invalid credentials or role mismatch");
        }

        // Return login response
        return new LoginResponse(
                user.getUserId(),
                user.getName(),
                user.getRole().name()
        );
    }
}

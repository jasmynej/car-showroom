package com.slingshot.carshowroom.service;

import com.slingshot.carshowroom.dto.LoginRequest;
import com.slingshot.carshowroom.dto.LoginResponse;
import com.slingshot.carshowroom.exception.InvalidCredentialsException;
import com.slingshot.carshowroom.model.Role;
import com.slingshot.carshowroom.model.User;
import com.slingshot.carshowroom.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("$2a$10$hashedPassword");
        testUser.setRole(Role.CUSTOMER);

        loginRequest = new LoginRequest(1, "password123", "CUSTOMER");
    }

    @Test
    void authenticate_WithValidCredentialsAndRole_ReturnsLoginResponse() {
        // Arrange
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "$2a$10$hashedPassword")).thenReturn(true);

        // Act
        LoginResponse response = authService.authenticate(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getUserId());
        assertEquals("Test User", response.getName());
        assertEquals("CUSTOMER", response.getRole());
        verify(userRepository).findById(1);
        verify(passwordEncoder).matches("password123", "$2a$10$hashedPassword");
    }

    @Test
    void authenticate_WithInvalidPassword_ThrowsInvalidCredentialsException() {
        // Arrange
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpassword", "$2a$10$hashedPassword")).thenReturn(false);
        loginRequest.setPassword("wrongpassword");

        // Act &amp; Assert
        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -&gt; authService.authenticate(loginRequest)
        );
        assertEquals("Invalid credentials", exception.getMessage());
    }

    @Test
    void authenticate_WithNonExistentUser_ThrowsInvalidCredentialsException() {
        // Arrange
        when(userRepository.findById(999)).thenReturn(Optional.empty());
        loginRequest.setUserId(999);

        // Act &amp; Assert
        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -&gt; authService.authenticate(loginRequest)
        );
        assertEquals("Invalid credentials", exception.getMessage());
    }

    @Test
    void authenticate_WithRoleMismatch_ThrowsInvalidCredentialsException() {
        // Arrange
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "$2a$10$hashedPassword")).thenReturn(true);
        loginRequest.setRole("MANAGER"); // Wrong role

        // Act &amp; Assert
        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -&gt; authService.authenticate(loginRequest)
        );
        assertEquals("Invalid credentials or role mismatch", exception.getMessage());
    }

    @Test
    void authenticate_WithStaffRole_ReturnsCorrectResponse() {
        // Arrange
        testUser.setRole(Role.STAFF);
        loginRequest.setRole("STAFF");
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "$2a$10$hashedPassword")).thenReturn(true);

        // Act
        LoginResponse response = authService.authenticate(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("STAFF", response.getRole());
    }

    @Test
    void authenticate_WithManagerRole_ReturnsCorrectResponse() {
        // Arrange
        testUser.setRole(Role.MANAGER);
        loginRequest.setRole("MANAGER");
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "$2a$10$hashedPassword")).thenReturn(true);

        // Act
        LoginResponse response = authService.authenticate(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("MANAGER", response.getRole());
    }
}

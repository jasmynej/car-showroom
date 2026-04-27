package com.slingshot.carshowroom.service;

import com.slingshot.carshowroom.dto.LoginRequest;
import com.slingshot.carshowroom.dto.LoginResponse;
import com.slingshot.carshowroom.exception.UnauthorizedException;
import com.slingshot.carshowroom.model.Role;
import com.slingshot.carshowroom.model.User;
import com.slingshot.carshowroom.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    private BCryptPasswordEncoder passwordEncoder;
    private User testUser;
    private LoginRequest loginRequest;
    private String hashedPassword;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder(10);
        ReflectionTestUtils.setField(authService, "passwordEncoder", passwordEncoder);
        
        hashedPassword = passwordEncoder.encode("password123");
        
        testUser = new User();
        testUser.setId(1);
        testUser.setUserId("test.user");
        testUser.setName("Test User");
        testUser.setPassword(hashedPassword);
        testUser.setRole(Role.CUSTOMER);
        testUser.setEmail("test@example.com");

        loginRequest = new LoginRequest("test.user", "password123", "CUSTOMER");
    }

    @Test
    void authenticate_shouldReturnUserDataOnSuccess() {
        // Arrange
        when(userRepository.findByUserId("test.user")).thenReturn(Optional.of(testUser));

        // Act
        LoginResponse response = authService.authenticate(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("test.user", response.userId());
        assertEquals("Test User", response.name());
        assertEquals("CUSTOMER", response.role());
        verify(userRepository).findByUserId("test.user");
    }

    @Test
    void authenticate_shouldThrowExceptionOnInvalidUserId() {
        // Arrange
        when(userRepository.findByUserId("invalid.user")).thenReturn(Optional.empty());

        LoginRequest invalidRequest = new LoginRequest("invalid.user", "password123", "CUSTOMER");

        // Act &amp; Assert
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -&gt; {
            authService.authenticate(invalidRequest);
        });

        assertEquals("Invalid credentials or role mismatch", exception.getMessage());
        verify(userRepository).findByUserId("invalid.user");
    }

    @Test
    void authenticate_shouldThrowExceptionOnInvalidPassword() {
        // Arrange
        when(userRepository.findByUserId("test.user")).thenReturn(Optional.of(testUser));

        LoginRequest invalidRequest = new LoginRequest("test.user", "wrongpassword", "CUSTOMER");

        // Act &amp; Assert
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -&gt; {
            authService.authenticate(invalidRequest);
        });

        assertEquals("Invalid credentials or role mismatch", exception.getMessage());
        verify(userRepository).findByUserId("test.user");
    }

    @Test
    void authenticate_shouldThrowExceptionOnRoleMismatch() {
        // Arrange
        when(userRepository.findByUserId("test.user")).thenReturn(Optional.of(testUser));

        LoginRequest staffRequest = new LoginRequest("test.user", "password123", "STAFF");

        // Act &amp; Assert
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -&gt; {
            authService.authenticate(staffRequest);
        });

        assertEquals("Invalid credentials or role mismatch", exception.getMessage());
        verify(userRepository).findByUserId("test.user");
    }

    @Test
    void authenticate_shouldHandleCaseInsensitiveRole() {
        // Arrange
        when(userRepository.findByUserId("test.user")).thenReturn(Optional.of(testUser));

        LoginRequest lowerCaseRequest = new LoginRequest("test.user", "password123", "customer");

        // Act
        LoginResponse response = authService.authenticate(lowerCaseRequest);

        // Assert
        assertNotNull(response);
        assertEquals("CUSTOMER", response.role());
    }
}

package com.slingshot.carshowroom.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.slingshot.carshowroom.dto.CreateUserRequest;
import com.slingshot.carshowroom.dto.LoginRequest;
import com.slingshot.carshowroom.model.Role;
import com.slingshot.carshowroom.model.User;
import com.slingshot.carshowroom.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void signup_shouldCreateUserAndReturn201() throws Exception {
        // Arrange
        CreateUserRequest request = new CreateUserRequest(
                "new.user",
                "New User",
                "password123",
                "CUSTOMER"
        );

        // Act &amp; Assert
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value("new.user"))
                .andExpect(jsonPath("$.name").value("New User"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));
    }

    @Test
    void signup_shouldReturn409WhenUserIdExists() throws Exception {
        // Arrange - Create existing user
        User existingUser = new User();
        existingUser.setUserId("existing.user");
        existingUser.setName("Existing User");
        existingUser.setPassword(passwordEncoder.encode("password123"));
        existingUser.setRole(Role.CUSTOMER);
        existingUser.setEmail("existing.user@carshowroom.com");
        userRepository.save(existingUser);

        CreateUserRequest request = new CreateUserRequest(
                "existing.user",
                "Another User",
                "password456",
                "CUSTOMER"
        );

        // Act &amp; Assert
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("User ID already exists"));
    }

    @Test
    void login_shouldReturn200OnValidCredentials() throws Exception {
        // Arrange - Create user
        User user = new User();
        user.setUserId("test.user");
        user.setName("Test User");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setRole(Role.CUSTOMER);
        user.setEmail("test.user@carshowroom.com");
        userRepository.save(user);

        LoginRequest request = new LoginRequest("test.user", "password123", "CUSTOMER");

        // Act &amp; Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("test.user"))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));
    }

    @Test
    void login_shouldReturn401OnInvalidPassword() throws Exception {
        // Arrange - Create user
        User user = new User();
        user.setUserId("test.user");
        user.setName("Test User");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setRole(Role.CUSTOMER);
        user.setEmail("test.user@carshowroom.com");
        userRepository.save(user);

        LoginRequest request = new LoginRequest("test.user", "wrongpassword", "CUSTOMER");

        // Act &amp; Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid credentials or role mismatch"));
    }

    @Test
    void login_shouldReturn401OnRoleMismatch() throws Exception {
        // Arrange - Create user with CUSTOMER role
        User user = new User();
        user.setUserId("test.user");
        user.setName("Test User");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setRole(Role.CUSTOMER);
        user.setEmail("test.user@carshowroom.com");
        userRepository.save(user);

        // Try to login as STAFF
        LoginRequest request = new LoginRequest("test.user", "password123", "STAFF");

        // Act &amp; Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid credentials or role mismatch"));
    }

    @Test
    void login_shouldReturn401OnInvalidUserId() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest("nonexistent.user", "password123", "CUSTOMER");

        // Act &amp; Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid credentials or role mismatch"));
    }

    @Test
    void signup_shouldDefaultToCustomerRoleWhenNotSpecified() throws Exception {
        // Arrange
        CreateUserRequest request = new CreateUserRequest(
                "default.user",
                "Default User",
                "password123",
                null  // No role specified
        );

        // Act &amp; Assert
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("CUSTOMER"));
    }

    @Test
    void signup_shouldReturn400OnValidationError() throws Exception {
        // Arrange - Password too short
        CreateUserRequest request = new CreateUserRequest(
                "new.user",
                "New User",
                "short",  // Less than 8 characters
                "CUSTOMER"
        );

        // Act &amp; Assert
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}

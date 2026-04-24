package com.slingshot.carshowroom.service;

import com.slingshot.carshowroom.dto.PasswordUpdateRequest;
import com.slingshot.carshowroom.dto.UserRegistrationRequest;
import com.slingshot.carshowroom.dto.UserResponse;
import com.slingshot.carshowroom.exception.UserAlreadyExistsException;
import com.slingshot.carshowroom.exception.UserNotFoundException;
import com.slingshot.carshowroom.model.Role;
import com.slingshot.carshowroom.model.User;
import com.slingshot.carshowroom.repository.CarRepository;
import com.slingshot.carshowroom.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceAuthTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CarRepository carRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserRegistrationRequest registrationRequest;

    @BeforeEach
    void setUp() {
        registrationRequest = new UserRegistrationRequest();
        registrationRequest.setName("John Doe");
        registrationRequest.setEmail("john@example.com");
        registrationRequest.setPassword("password123");
        registrationRequest.setContactInfo("123-456-7890");
    }

    @Test
    void registerUser_WithValidData_CreatesUserWithHashedPassword() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$hashedPassword");
        
        User savedUser = new User();
        savedUser.setUserId(1);
        savedUser.setName("John Doe");
        savedUser.setEmail("john@example.com");
        savedUser.setPassword("$2a$10$hashedPassword");
        savedUser.setRole(Role.CUSTOMER);
        savedUser.setContactInfo("123-456-7890");
        
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        UserResponse response = userService.registerUser(registrationRequest);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.userId());
        assertEquals("John Doe", response.name());
        assertEquals("john@example.com", response.email());
        
        ArgumentCaptor&lt;User&gt; userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        assertEquals("$2a$10$hashedPassword", capturedUser.getPassword());
        verify(passwordEncoder).encode("password123");
    }

    @Test
    void registerUser_WithDefaultRole_SetsCustomerRole() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashedPassword");
        
        User savedUser = new User();
        savedUser.setUserId(1);
        savedUser.setName("John Doe");
        savedUser.setEmail("john@example.com");
        savedUser.setPassword("$2a$10$hashedPassword");
        savedUser.setRole(Role.CUSTOMER);
        
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        UserResponse response = userService.registerUser(registrationRequest);

        // Assert
        ArgumentCaptor&lt;User&gt; userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals(Role.CUSTOMER, userCaptor.getValue().getRole());
    }

    @Test
    void registerUser_WithStaffRole_SetsStaffRole() {
        // Arrange
        registrationRequest.setRole("STAFF");
        registrationRequest.setDesignation("Sales Associate");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashedPassword");
        
        User savedUser = new User();
        savedUser.setUserId(1);
        savedUser.setName("John Doe");
        savedUser.setEmail("john@example.com");
        savedUser.setPassword("$2a$10$hashedPassword");
        savedUser.setRole(Role.STAFF);
        savedUser.setDesignation("Sales Associate");
        
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        UserResponse response = userService.registerUser(registrationRequest);

        // Assert
        ArgumentCaptor&lt;User&gt; userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals(Role.STAFF, userCaptor.getValue().getRole());
        assertEquals("Sales Associate", userCaptor.getValue().getDesignation());
    }

    @Test
    void registerUser_WithManagerRole_SetsManagerRole() {
        // Arrange
        registrationRequest.setRole("MANAGER");
        registrationRequest.setDepartment("Sales");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashedPassword");
        
        User savedUser = new User();
        savedUser.setUserId(1);
        savedUser.setName("John Doe");
        savedUser.setEmail("john@example.com");
        savedUser.setPassword("$2a$10$hashedPassword");
        savedUser.setRole(Role.MANAGER);
        savedUser.setDepartment("Sales");
        
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        UserResponse response = userService.registerUser(registrationRequest);

        // Assert
        ArgumentCaptor&lt;User&gt; userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals(Role.MANAGER, userCaptor.getValue().getRole());
        assertEquals("Sales", userCaptor.getValue().getDepartment());
    }

    @Test
    void registerUser_WithExistingEmail_ThrowsUserAlreadyExistsException() {
        // Arrange
        User existingUser = new User();
        existingUser.setEmail("john@example.com");
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(existingUser));

        // Act &amp; Assert
        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -&gt; userService.registerUser(registrationRequest)
        );
        assertEquals("Email already registered", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updatePassword_WithValidUser_HashesAndSavesNewPassword() {
        // Arrange
        User existingUser = new User();
        existingUser.setUserId(1);
        existingUser.setPassword("$2a$10$oldHashedPassword");
        
        when(userRepository.findById(1)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("newPassword123")).thenReturn("$2a$10$newHashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(existingUser);
        
        PasswordUpdateRequest request = new PasswordUpdateRequest("newPassword123");

        // Act
        userService.updatePassword(1, request);

        // Assert
        ArgumentCaptor&lt;User&gt; userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals("$2a$10$newHashedPassword", userCaptor.getValue().getPassword());
        verify(passwordEncoder).encode("newPassword123");
    }

    @Test
    void updatePassword_WithNonExistentUser_ThrowsUserNotFoundException() {
        // Arrange
        when(userRepository.findById(999)).thenReturn(Optional.empty());
        PasswordUpdateRequest request = new PasswordUpdateRequest("newPassword123");

        // Act &amp; Assert
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -&gt; userService.updatePassword(999, request)
        );
        assertEquals("User not found", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }
}

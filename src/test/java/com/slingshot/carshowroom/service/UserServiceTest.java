package com.slingshot.carshowroom.service;

import com.slingshot.carshowroom.dto.CreateUserRequest;
import com.slingshot.carshowroom.dto.UpdatePasswordRequest;
import com.slingshot.carshowroom.dto.UserResponse;
import com.slingshot.carshowroom.exception.ConflictException;
import com.slingshot.carshowroom.exception.ResourceNotFoundException;
import com.slingshot.carshowroom.model.Role;
import com.slingshot.carshowroom.model.User;
import com.slingshot.carshowroom.repository.CarRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private UserService userService;

    private BCryptPasswordEncoder passwordEncoder;
    private User testUser;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder(10);
        ReflectionTestUtils.setField(userService, "passwordEncoder", passwordEncoder);
        
        testUser = new User();
        testUser.setId(1);
        testUser.setUserId("test.user");
        testUser.setName("Test User");
        testUser.setPassword(passwordEncoder.encode("password123"));
        testUser.setRole(Role.CUSTOMER);
        testUser.setEmail("test@example.com");
    }

    @Test
    void createUserFromAuth_shouldHashPassword() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest(
                "john.doe",
                "John Doe",
                "password123",
                null
        );

        when(userRepository.findByUserId("john.doe")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(i -&gt; {
            User user = i.getArgument(0);
            user.setId(1);
            return user;
        });

        // Act
        UserResponse response = userService.createUserFromAuth(request);

        // Assert
        assertNotNull(response);
        assertEquals("john.doe", response.userId());
        assertEquals("John Doe", response.name());
        verify(userRepository).save(argThat(user -&gt;
                user.getPassword() != null &amp;&amp; user.getPassword().startsWith("$2a$")
        ));
    }

    @Test
    void createUserFromAuth_shouldDefaultToCustomerRole() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest(
                "jane.doe",
                "Jane Doe",
                "password123",
                null  // No role specified
        );

        when(userRepository.findByUserId("jane.doe")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(i -&gt; {
            User user = i.getArgument(0);
            user.setId(2);
            return user;
        });

        // Act
        UserResponse response = userService.createUserFromAuth(request);

        // Assert
        assertEquals(Role.CUSTOMER, response.role());
        verify(userRepository).save(argThat(user -&gt;
                user.getRole() == Role.CUSTOMER
        ));
    }

    @Test
    void createUserFromAuth_shouldThrowExceptionIfUserExists() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest(
                "existing.user",
                "Existing User",
                "password123",
                "CUSTOMER"
        );

        when(userRepository.findByUserId("existing.user")).thenReturn(Optional.of(testUser));

        // Act &amp; Assert
        ConflictException exception = assertThrows(ConflictException.class, () -&gt; {
            userService.createUserFromAuth(request);
        });

        assertEquals("User ID already exists", exception.getMessage());
        verify(userRepository).findByUserId("existing.user");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUserFromAuth_shouldSetRoleFromRequest() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest(
                "staff.user",
                "Staff User",
                "password123",
                "STAFF"
        );

        when(userRepository.findByUserId("staff.user")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(i -&gt; {
            User user = i.getArgument(0);
            user.setId(3);
            return user;
        });

        // Act
        UserResponse response = userService.createUserFromAuth(request);

        // Assert
        assertEquals(Role.STAFF, response.role());
        verify(userRepository).save(argThat(user -&gt;
                user.getRole() == Role.STAFF
        ));
    }

    @Test
    void updatePassword_shouldHashNewPassword() {
        // Arrange
        UpdatePasswordRequest request = new UpdatePasswordRequest("newPassword456");

        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        userService.updatePassword(1, request);

        // Assert
        verify(userRepository).save(argThat(user -&gt;
                user.getPassword() != null &amp;&amp; user.getPassword().startsWith("$2a$")
        ));
    }

    @Test
    void updatePassword_shouldThrowExceptionIfUserNotFound() {
        // Arrange
        UpdatePasswordRequest request = new UpdatePasswordRequest("newPassword456");
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        // Act &amp; Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -&gt; {
            userService.updatePassword(999, request);
        });

        assertTrue(exception.getMessage().contains("User not found"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUserFromAuth_shouldGenerateEmailFromUserId() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest(
                "new.user",
                "New User",
                "password123",
                "CUSTOMER"
        );

        when(userRepository.findByUserId("new.user")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(i -&gt; {
            User user = i.getArgument(0);
            user.setId(4);
            return user;
        });

        // Act
        userService.createUserFromAuth(request);

        // Assert
        verify(userRepository).save(argThat(user -&gt;
                user.getEmail().equals("new.user@carshowroom.com")
        ));
    }
}

package com.slingshot.carshowroom.service;

import com.slingshot.carshowroom.dto.LoginRequest;
import com.slingshot.carshowroom.dto.PasswordUpdateRequest;
import com.slingshot.carshowroom.dto.UserRegistrationRequest;
import com.slingshot.carshowroom.dto.UserResponse;
import com.slingshot.carshowroom.exception.AuthenticationException;
import com.slingshot.carshowroom.exception.ConflictException;
import com.slingshot.carshowroom.exception.ResourceNotFoundException;
import com.slingshot.carshowroom.model.Role;
import com.slingshot.carshowroom.model.User;
import com.slingshot.carshowroom.repository.CarRepository;
import com.slingshot.carshowroom.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private CarRepository carRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private UserService userService;

    private User testUser;
    private UserRegistrationRequest registrationRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        carRepository = mock(CarRepository.class);
        passwordEncoder = new BCryptPasswordEncoder();
        userService = new UserService(userRepository, carRepository, passwordEncoder);

        testUser = new User();
        testUser.setUserId(1);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash(passwordEncoder.encode("password123"));
        testUser.setRole(Role.CUSTOMER);
        testUser.setContactInfo("123-456-7890");

        registrationRequest = new UserRegistrationRequest(
            "Test User",
            "test@example.com",
            "password123",
            Role.CUSTOMER,
            "123-456-7890",
            null,
            null
        );

        loginRequest = new LoginRequest(
            "test@example.com",
            "password123",
            Role.CUSTOMER
        );
    }

    @Test
    void testRegisterUser_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(carRepository.findByOwnerId(anyInt())).thenReturn(Collections.emptyList());

        UserResponse response = userService.registerUser(registrationRequest);

        assertNotNull(response);
        assertEquals("Test User", response.name());
        assertEquals("test@example.com", response.email());
        assertEquals(Role.CUSTOMER, response.role());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUser_DefaultRole() {
        UserRegistrationRequest requestWithoutRole = new UserRegistrationRequest(
            "Test User",
            "test@example.com",
            "password123",
            null,
            "123-456-7890",
            null,
            null
        );

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -&gt; {
            User user = invocation.getArgument(0);
            assertEquals(Role.CUSTOMER, user.getRole());
            user.setUserId(1);
            return user;
        });
        when(carRepository.findByOwnerId(anyInt())).thenReturn(Collections.emptyList());

        UserResponse response = userService.registerUser(requestWithoutRole);

        assertNotNull(response);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUser_DuplicateEmail() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        assertThrows(ConflictException.class, () -&gt; {
            userService.registerUser(registrationRequest);
        });

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testAuthenticate_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(carRepository.findByOwnerId(anyInt())).thenReturn(Collections.emptyList());

        UserResponse response = userService.authenticate(loginRequest);

        assertNotNull(response);
        assertEquals("Test User", response.name());
        assertEquals("test@example.com", response.email());
        assertEquals(Role.CUSTOMER, response.role());
    }

    @Test
    void testAuthenticate_WrongPassword() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        LoginRequest wrongPasswordRequest = new LoginRequest(
            "test@example.com",
            "wrongpassword",
            Role.CUSTOMER
        );

        assertThrows(AuthenticationException.class, () -&gt; {
            userService.authenticate(wrongPasswordRequest);
        });
    }

    @Test
    void testAuthenticate_WrongRole() {
        LoginRequest wrongRoleRequest = new LoginRequest(
            "test@example.com",
            "password123",
            Role.STAFF
        );

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        assertThrows(AuthenticationException.class, () -&gt; {
            userService.authenticate(wrongRoleRequest);
        });
    }

    @Test
    void testAuthenticate_UserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(AuthenticationException.class, () -&gt; {
            userService.authenticate(loginRequest);
        });
    }

    @Test
    void testChangePassword_Success() {
        PasswordUpdateRequest passwordRequest = new PasswordUpdateRequest("newPassword123");
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.changePassword(1, passwordRequest);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testChangePassword_UserNotFound() {
        PasswordUpdateRequest passwordRequest = new PasswordUpdateRequest("newPassword123");
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -&gt; {
            userService.changePassword(1, passwordRequest);
        });

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testPasswordIsHashed() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -&gt; {
            User user = invocation.getArgument(0);
            assertTrue(user.getPasswordHash().startsWith("$2a$"));
            assertNotEquals("password123", user.getPasswordHash());
            user.setUserId(1);
            return user;
        });
        when(carRepository.findByOwnerId(anyInt())).thenReturn(Collections.emptyList());

        userService.registerUser(registrationRequest);

        verify(userRepository, times(1)).save(any(User.class));
    }
}

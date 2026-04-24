package com.slingshot.carshowroom.service;

import com.slingshot.carshowroom.dto.CarResponse;
import com.slingshot.carshowroom.dto.LoginRequest;
import com.slingshot.carshowroom.dto.PasswordUpdateRequest;
import com.slingshot.carshowroom.dto.UserRegistrationRequest;
import com.slingshot.carshowroom.dto.UserRequest;
import com.slingshot.carshowroom.dto.UserResponse;
import com.slingshot.carshowroom.exception.AuthenticationException;
import com.slingshot.carshowroom.exception.ConflictException;
import com.slingshot.carshowroom.exception.ResourceNotFoundException;
import com.slingshot.carshowroom.model.Role;
import com.slingshot.carshowroom.model.User;
import com.slingshot.carshowroom.repository.CarRepository;
import com.slingshot.carshowroom.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, CarRepository carRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.carRepository = carRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse getUser(Integer id) {
        User user = findOrThrow(id);
        List&lt;CarResponse&gt; ownedCars = null;
        if (user.getRole() == Role.CUSTOMER) {
            ownedCars = carRepository.findByOwnerId(id).stream().map(CarResponse::from).toList();
        }
        return UserResponse.from(user, ownedCars);
    }

    public UserResponse createUser(UserRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new ConflictException("Email already registered: " + request.email());
        }
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setContactInfo(request.contactInfo());
        user.setRole(request.role());
        if (request.role() == Role.MANAGER) user.setDepartment(request.department());
        if (request.role() == Role.STAFF) user.setDesignation(request.designation());
        return UserResponse.from(userRepository.save(user), null);
    }

    public UserResponse registerUser(UserRegistrationRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new ConflictException("Email already registered: " + request.email());
        }
        
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setContactInfo(request.contactInfo());
        user.setRole(request.role() != null ? request.role() : Role.CUSTOMER);
        
        if (user.getRole() == Role.MANAGER) {
            user.setDepartment(request.department());
        }
        if (user.getRole() == Role.STAFF) {
            user.setDesignation(request.designation());
        }
        
        return UserResponse.from(userRepository.save(user), null);
    }

    public UserResponse authenticate(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
            .orElseThrow(() -&gt; new AuthenticationException("Invalid credentials"));
        
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new AuthenticationException("Invalid credentials");
        }
        
        if (user.getRole() != request.role()) {
            throw new AuthenticationException("Invalid credentials");
        }
        
        List&lt;CarResponse&gt; ownedCars = null;
        if (user.getRole() == Role.CUSTOMER) {
            ownedCars = carRepository.findByOwnerId(user.getUserId()).stream()
                .map(CarResponse::from).toList();
        }
        
        return UserResponse.from(user, ownedCars);
    }

    public void changePassword(Integer id, PasswordUpdateRequest request) {
        User user = findOrThrow(id);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        userRepository.save(user);
    }

    private User findOrThrow(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -&gt; new ResourceNotFoundException("User not found: " + id));
    }
}

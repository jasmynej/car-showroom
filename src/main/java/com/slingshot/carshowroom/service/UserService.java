package com.slingshot.carshowroom.service;

import com.slingshot.carshowroom.dto.CarResponse;
import com.slingshot.carshowroom.dto.CreateUserRequest;
import com.slingshot.carshowroom.dto.PasswordUpdateRequest;
import com.slingshot.carshowroom.dto.UpdatePasswordRequest;
import com.slingshot.carshowroom.dto.UserRequest;
import com.slingshot.carshowroom.dto.UserResponse;
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
        // Hash password using BCrypt
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setContactInfo(request.contactInfo());
        user.setRole(request.role());
        if (request.role() == Role.MANAGER) user.setDepartment(request.department());
        if (request.role() == Role.STAFF) user.setDesignation(request.designation());
        return UserResponse.from(userRepository.save(user), null);
    }

    public UserResponse createUserFromAuth(CreateUserRequest request) {
        // Check if userId already exists
        if (userRepository.findByUserId(request.userId()).isPresent()) {
            throw new ConflictException("User ID already exists");
        }

        // Create user entity
        User user = new User();
        user.setUserId(request.userId());
        user.setName(request.name());

        // Hash password using BCrypt
        String hashedPassword = passwordEncoder.encode(request.password());
        user.setPassword(hashedPassword);

        // Set role (default to CUSTOMER)
        Role role = (request.role() != null &amp;&amp; !request.role().isEmpty())
                ? Role.valueOf(request.role().toUpperCase())
                : Role.CUSTOMER;
        user.setRole(role);

        // Generate email from userId if not provided
        user.setEmail(request.userId() + "@carshowroom.com");

        // Save and return
        User savedUser = userRepository.save(user);
        return UserResponse.from(savedUser, null);
    }

    public void changePassword(Integer id, PasswordUpdateRequest request) {
        User user = findOrThrow(id);
        // Hash password using BCrypt
        user.setPassword(passwordEncoder.encode(request.password()));
        userRepository.save(user);
    }

    public void updatePassword(Integer id, UpdatePasswordRequest request) {
        User user = findOrThrow(id);
        // Hash password using BCrypt
        String hashedPassword = passwordEncoder.encode(request.newPassword());
        user.setPassword(hashedPassword);
        userRepository.save(user);
    }

    private User findOrThrow(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -&gt; new ResourceNotFoundException("User not found: " + id));
    }
}

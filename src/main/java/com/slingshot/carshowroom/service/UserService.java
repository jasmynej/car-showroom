package com.slingshot.carshowroom.service;

import com.slingshot.carshowroom.dto.CarResponse;
import com.slingshot.carshowroom.dto.PasswordUpdateRequest;
import com.slingshot.carshowroom.dto.UserRegistrationRequest;
import com.slingshot.carshowroom.dto.UserRequest;
import com.slingshot.carshowroom.dto.UserResponse;
import com.slingshot.carshowroom.exception.ConflictException;
import com.slingshot.carshowroom.exception.ResourceNotFoundException;
import com.slingshot.carshowroom.exception.UserAlreadyExistsException;
import com.slingshot.carshowroom.exception.UserNotFoundException;
import com.slingshot.carshowroom.model.Role;
import com.slingshot.carshowroom.model.User;
import com.slingshot.carshowroom.repository.CarRepository;
import com.slingshot.carshowroom.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, CarRepository carRepository, PasswordEncoder passwordEncoder) {
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
        user.setPassword(request.password());
        user.setContactInfo(request.contactInfo());
        user.setRole(request.role());
        if (request.role() == Role.MANAGER) user.setDepartment(request.department());
        if (request.role() == Role.STAFF) user.setDesignation(request.designation());
        return UserResponse.from(userRepository.save(user), null);
    }

    public UserResponse registerUser(UserRegistrationRequest request) {
        // 1. Check if user already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email already registered");
        }

        // 2. Create new user
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());

        // 3. Hash password
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // 4. Set role (default to CUSTOMER)
        Role role = request.getRole() != null &amp;&amp; !request.getRole().isEmpty()
                ? Role.valueOf(request.getRole())
                : Role.CUSTOMER;
        user.setRole(role);

        // 5. Set optional fields
        user.setContactInfo(request.getContactInfo());
        if (role == Role.MANAGER) {
            user.setDepartment(request.getDepartment());
        }
        if (role == Role.STAFF) {
            user.setDesignation(request.getDesignation());
        }

        // 6. Save and return
        User savedUser = userRepository.save(user);
        return UserResponse.from(savedUser, null);
    }

    public void changePassword(Integer id, PasswordUpdateRequest request) {
        User user = findOrThrow(id);
        user.setPassword(request.password());
        userRepository.save(user);
    }

    public void updatePassword(Integer userId, PasswordUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -&gt; new UserNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(request.password()));
        userRepository.save(user);
    }

    private User findOrThrow(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -&gt; new ResourceNotFoundException("User not found: " + id));
    }
}

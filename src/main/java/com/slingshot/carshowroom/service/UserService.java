package com.slingshot.carshowroom.service;

import com.slingshot.carshowroom.dto.CarResponse;
import com.slingshot.carshowroom.dto.PasswordUpdateRequest;
import com.slingshot.carshowroom.dto.UserRequest;
import com.slingshot.carshowroom.dto.UserResponse;
import com.slingshot.carshowroom.exception.ConflictException;
import com.slingshot.carshowroom.exception.ResourceNotFoundException;
import com.slingshot.carshowroom.model.Role;
import com.slingshot.carshowroom.model.User;
import com.slingshot.carshowroom.repository.CarRepository;
import com.slingshot.carshowroom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CarRepository carRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, CarRepository carRepository) {
        this.userRepository = userRepository;
        this.carRepository = carRepository;
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
        // Hash password before saving
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setContactInfo(request.contactInfo());
        user.setRole(request.role());
        if (request.role() == Role.MANAGER) user.setDepartment(request.department());
        if (request.role() == Role.STAFF) user.setDesignation(request.designation());
        return UserResponse.from(userRepository.save(user), null);
    }

    public void changePassword(Integer id, PasswordUpdateRequest request) {
        User user = findOrThrow(id);
        // Hash new password before saving
        user.setPassword(passwordEncoder.encode(request.password()));
        userRepository.save(user);
    }

    private User findOrThrow(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -&gt; new ResourceNotFoundException("User not found: " + id));
    }
}

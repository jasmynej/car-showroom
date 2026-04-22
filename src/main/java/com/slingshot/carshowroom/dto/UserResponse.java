package com.slingshot.carshowroom.dto;

import com.slingshot.carshowroom.model.Role;
import com.slingshot.carshowroom.model.User;

import java.util.List;

public record UserResponse(
        Integer userId,
        String name,
        String email,
        String contactInfo,
        Role role,
        String department,
        String designation,
        List<CarResponse> ownedCars
) {
    public static UserResponse from(User user, List<CarResponse> ownedCars) {
        return new UserResponse(
                user.getUserId(), user.getName(), user.getEmail(),
                user.getContactInfo(), user.getRole(),
                user.getDepartment(), user.getDesignation(),
                ownedCars
        );
    }
}

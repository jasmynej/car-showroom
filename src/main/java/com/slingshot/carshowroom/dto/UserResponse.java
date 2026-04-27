package com.slingshot.carshowroom.dto;

import com.slingshot.carshowroom.model.Role;
import com.slingshot.carshowroom.model.User;

import java.time.LocalDateTime;
import java.util.List;

public record UserResponse(
        Integer id,
        String userId,
        String name,
        String email,
        String contactInfo,
        Role role,
        String department,
        String designation,
        LocalDateTime createdAt,
        List&lt;CarResponse&gt; ownedCars
) {
    public static UserResponse from(User user, List&lt;CarResponse&gt; ownedCars) {
        return new UserResponse(
                user.getId(),
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getContactInfo(),
                user.getRole(),
                user.getDepartment(),
                user.getDesignation(),
                user.getCreatedAt(),
                ownedCars
        );
    }
}

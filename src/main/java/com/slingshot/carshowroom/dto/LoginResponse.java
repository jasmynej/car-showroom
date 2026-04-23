package com.slingshot.carshowroom.dto;

import com.slingshot.carshowroom.model.Role;

public class LoginResponse {
    
    private Integer userId;
    private String email;
    private String name;
    private Role role;

    // Constructors
    public LoginResponse() {}
    
    public LoginResponse(Integer userId, String email, String name, Role role) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.role = role;
    }

    // Getters and setters
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}

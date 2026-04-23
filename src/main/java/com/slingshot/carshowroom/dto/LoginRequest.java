package com.slingshot.carshowroom.dto;

import com.slingshot.carshowroom.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for login requests.
 */
public class LoginRequest {
    
    @NotBlank(message = "Email is required")
    private String email;
    
    @NotBlank(message = "Password is required")
    private String password;
    
    @NotNull(message = "Role is required")
    private Role role;
    
    // Constructors
    public LoginRequest() {}
    
    public LoginRequest(String email, String password, Role role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }
    
    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}

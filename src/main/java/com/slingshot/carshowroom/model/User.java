package com.slingshot.carshowroom.model;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * JPA Entity representing a User in the car showroom system.
 * Stores user authentication and role information with audit timestamps.
 * 
 * @author Car Showroom Team
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "users")
public class User {

    /**
     * Unique identifier for the user, automatically generated as UUID
     */
    @Id
    @UuidGenerator
    @Column(name = "user_id", updatable = false, nullable = false)
    private UUID userId;

    /**
     * User's email address - must be unique and non-null
     */
    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    /**
     * Hashed password for user authentication
     */
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    /**
     * User's role in the system (CUSTOMER, STAFF, or MANAGER)
     * Stored as STRING in the database
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 50)
    private UserRole role;

    /**
     * Timestamp when the user record was created (UTC)
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * Timestamp when the user record was last updated (UTC)
     */
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /**
     * Default constructor required by JPA
     */
    public User() {
    }

    /**
     * Constructor with all required fields
     * 
     * @param email the user's email address
     * @param passwordHash the hashed password
     * @param role the user's role
     */
    public User(String email, String passwordHash, UserRole role) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    /**
     * Lifecycle callback to set createdAt and updatedAt before persisting
     */
    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    /**
     * Lifecycle callback to update updatedAt timestamp before updating
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    // Getters and Setters

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId) && 
               Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, email);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

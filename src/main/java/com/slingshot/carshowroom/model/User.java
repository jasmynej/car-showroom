package com.slingshot.carshowroom.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * JPA Entity representing a User in the car showroom system.
 * 
 * <p>This entity maps to the 'users' table in the H2 in-memory database and includes:</p>
 * <ul>
 *   <li>UUID-based primary key with automatic generation</li>
 *   <li>Email-based unique identification</li>
 *   <li>Password hash storage (never store plain text passwords)</li>
 *   <li>Role-based access control via UserRole enum</li>
 *   <li>Automatic audit timestamps for creation and updates</li>
 * </ul>
 */
@Entity
@Table(name = "users", 
       uniqueConstraints = @UniqueConstraint(name = "uk_users_email", columnNames = "email"),
       indexes = @Index(name = "idx_users_email", columnList = "email"))
public class User {

    /**
     * Primary key - UUID generated automatically using Hibernate's @UuidGenerator.
     * Provides globally unique identifiers without database round-trips.
     */
    @Id
    @UuidGenerator
    @Column(name = "user_id", updatable = false, nullable = false)
    private UUID userId;

    /**
     * User's email address - must be unique and valid format.
     * Used as the primary identifier for authentication.
     */
    @Email(message = "Email must be a valid email address")
    @NotBlank(message = "Email is required")
    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    /**
     * Hashed password - NEVER store plain text passwords.
     * Should be hashed using BCrypt or similar strong hashing algorithm.
     */
    @NotBlank(message = "Password hash is required")
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    /**
     * User's role in the system (CUSTOMER, STAFF, MANAGER).
     * Stored as STRING in database for readability and flexibility.
     */
    @NotNull(message = "Role is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 50)
    private UserRole role;

    /**
     * Timestamp when the user record was created.
     * Automatically set on entity creation, stored in UTC.
     */
    @NotNull
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * Timestamp when the user record was last updated.
     * Automatically updated on any entity modification, stored in UTC.
     */
    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /**
     * Default constructor required by JPA.
     */
    public User() {
    }

    /**
     * Constructor for creating a new User with required fields.
     * Timestamps will be set automatically via @PrePersist.
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
     * JPA lifecycle callback - executed before persisting a new entity.
     * Sets the creation and update timestamps to current UTC time.
     */
    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    /**
     * JPA lifecycle callback - executed before updating an existing entity.
     * Updates the updatedAt timestamp to current UTC time.
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

    /**
     * Equals method based on business key (email) and primary key (userId).
     * Two users are equal if they have the same userId (when persisted) or same email.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        // If both have userId, compare by userId
        if (userId != null && user.userId != null) {
            return Objects.equals(userId, user.userId);
        }
        // Otherwise compare by email (business key)
        return Objects.equals(email, user.email);
    }

    /**
     * HashCode based on email (business key) for consistency with equals.
     */
    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    /**
     * String representation of User (excludes password hash for security).
     */
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

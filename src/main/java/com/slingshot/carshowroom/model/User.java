package com.slingshot.carshowroom.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

/**
 * JPA Entity representing a User in the car showroom system.
 * 
 * <p>This entity supports user credentials, roles, and profile information
 * with H2 in-memory database for development and testing.
 * 
 * <p>Features:
 * <ul>
 *   <li>UUID-based primary key for distributed system compatibility</li>
 *   <li>Email-based authentication with uniqueness constraint</li>
 *   <li>Role-based access control (CUSTOMER, STAFF, MANAGER)</li>
 *   <li>Automatic audit timestamps (created_at, updated_at)</li>
 *   <li>BCrypt password hash storage</li>
 * </ul>
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_users_email", columnList = "email")
})
public class User {

    /**
     * Unique identifier for the user.
     * Auto-generated UUID using Hibernate's UuidGenerator.
     */
    @Id
    @UuidGenerator
    @Column(name = "user_id", updatable = false, nullable = false)
    private UUID userId;

    /**
     * User's email address.
     * Used as the primary authentication identifier.
     * Must be unique across all users.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Column(name = "email", unique = true, nullable = false, length = 255)
    private String email;

    /**
     * Hashed password for user authentication.
     * Should store BCrypt hash (typically 60 characters).
     * Never store plain text passwords.
     */
    @NotBlank(message = "Password hash is required")
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    /**
     * User's full name.
     * Optional field for display purposes.
     */
    @Column(name = "name", length = 100)
    private String name;

    /**
     * User's contact information (typically phone number).
     * Optional field for communication purposes.
     */
    @Column(name = "contact_information", length = 50)
    private String contactInformation;

    /**
     * User's role in the system.
     * Determines access permissions and available features.
     */
    @NotNull(message = "Role is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private UserRole role;

    /**
     * Timestamp when the user record was created.
     * Automatically set on entity creation.
     * Stored in UTC timezone.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * Timestamp when the user record was last updated.
     * Automatically updated on entity modification.
     * Stored in UTC timezone.
     */
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /**
     * JPA lifecycle callback executed before persisting a new entity.
     * Initializes created_at and updated_at timestamps.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    /**
     * JPA lifecycle callback executed before updating an existing entity.
     * Updates the updated_at timestamp.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    // Constructors

    /**
     * Default no-args constructor required by JPA.
     */
    public User() {
    }

    /**
     * All-args constructor for creating User instances.
     * 
     * @param userId the unique identifier
     * @param email the user's email address
     * @param passwordHash the hashed password
     * @param name the user's full name
     * @param contactInformation the user's contact information
     * @param role the user's role
     * @param createdAt the creation timestamp
     * @param updatedAt the last update timestamp
     */
    public User(UUID userId, String email, String passwordHash, String name, 
                String contactInformation, UserRole role, Instant createdAt, Instant updatedAt) {
        this.userId = userId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.name = name;
        this.contactInformation = contactInformation;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactInformation() {
        return contactInformation;
    }

    public void setContactInformation(String contactInformation) {
        this.contactInformation = contactInformation;
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
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", contactInformation='" + contactInformation + '\'' +
                ", role=" + role +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

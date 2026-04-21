package com.slingshot.carshowroom.repository;

import com.slingshot.carshowroom.model.User;
import com.slingshot.carshowroom.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for User entity.
 * 
 * <p>Provides CRUD operations and custom query methods for User management.
 * 
 * <p>Key Features:
 * <ul>
 *   <li>Find users by email (authentication)</li>
 *   <li>Find users by UUID</li>
 *   <li>Check email existence (validation)</li>
 *   <li>Query users by role (authorization)</li>
 *   <li>Count users by role (reporting)</li>
 * </ul>
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Find a user by their email address.
     * 
     * <p>Primary method for user authentication and lookup.
     * Email is unique, so this returns at most one user.
     * 
     * @param email the email address to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findByEmail(String email);

    /**
     * Find a user by their unique user ID.
     * 
     * <p>Alternative to findById for explicit naming.
     * 
     * @param userId the UUID to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findByUserId(UUID userId);

    /**
     * Check if a user with the given email already exists.
     * 
     * <p>Useful for validation before creating new users
     * to prevent duplicate email violations.
     * 
     * @param email the email address to check
     * @return true if a user with this email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Find all users with a specific role.
     * 
     * <p>Useful for role-based queries and reporting.
     * For example, finding all STAFF members or CUSTOMERS.
     * 
     * @param role the UserRole to filter by
     * @return List of users with the specified role (may be empty)
     */
    List<User> findByRole(UserRole role);

    /**
     * Count the number of users with a specific role.
     * 
     * <p>Useful for analytics and reporting without loading full entities.
     * 
     * @param role the UserRole to count
     * @return the number of users with the specified role
     */
    long countByRole(UserRole role);
}

package com.slingshot.carshowroom.repository;

import com.slingshot.carshowroom.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository for User entity.
 * 
 * <p>Provides CRUD operations and custom query methods for User management.</p>
 * 
 * <p>Key features:</p>
 * <ul>
 *   <li>Inherits standard CRUD operations from JpaRepository</li>
 *   <li>Custom query method to find users by email</li>
 *   <li>Email existence check for validation</li>
 *   <li>Automatic query generation by Spring Data JPA</li>
 * </ul>
 * 
 * <p>Standard inherited methods include:</p>
 * <ul>
 *   <li>save(User) - Create or update a user</li>
 *   <li>findById(UUID) - Find user by userId</li>
 *   <li>findAll() - Retrieve all users</li>
 *   <li>deleteById(UUID) - Delete user by userId</li>
 *   <li>count() - Count total users</li>
 * </ul>
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Find a user by their email address.
     * 
     * <p>Email is a unique business identifier for users and is commonly used
     * for authentication and user lookup operations.</p>
     * 
     * @param email the email address to search for (case-sensitive)
     * @return Optional containing the User if found, empty Optional otherwise
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if a user exists with the given email address.
     * 
     * <p>Useful for validation during user registration to ensure email uniqueness
     * without retrieving the full user entity.</p>
     * 
     * @param email the email address to check
     * @return true if a user with this email exists, false otherwise
     */
    boolean existsByEmail(String email);
}

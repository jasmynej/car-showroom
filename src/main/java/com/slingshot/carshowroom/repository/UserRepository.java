package com.slingshot.carshowroom.repository;

import com.slingshot.carshowroom.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for User entity.
 * Provides CRUD operations and custom query methods for user management.
 * 
 * @author Car Showroom Team
 * @version 1.0
 * @since 1.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Find a user by their email address.
     * 
     * @param email the email address to search for
     * @return an Optional containing the user if found, empty otherwise
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if a user exists with the given email address.
     * Useful for validation before creating new users.
     * 
     * @param email the email address to check
     * @return true if a user with this email exists, false otherwise
     */
    boolean existsByEmail(String email);
}

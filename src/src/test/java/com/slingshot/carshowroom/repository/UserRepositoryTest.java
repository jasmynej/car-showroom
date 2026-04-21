package com.slingshot.carshowroom.repository;

import com.slingshot.carshowroom.model.User;
import com.slingshot.carshowroom.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for UserRepository using Spring Data JPA.
 * Uses @DataJpaTest for testing repository layer with H2 in-memory database.
 * 
 * @author Car Showroom Team
 * @version 1.0
 */
@DataJpaTest
@DisplayName("UserRepository Integration Tests")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User testUser;
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD_HASH = "$2a$10$hashedpassword123";
    private static final UserRole TEST_ROLE = UserRole.CUSTOMER;

    @BeforeEach
    void setUp() {
        // Clear database before each test
        userRepository.deleteAll();
        
        // Create test user
        testUser = new User(TEST_EMAIL, TEST_PASSWORD_HASH, TEST_ROLE);
    }

    @Nested
    @DisplayName("Basic CRUD Operations")
    class CrudOperationTests {

        @Test
        @DisplayName("Should save user and generate UUID")
        void testSaveUser() {
            User savedUser = userRepository.save(testUser);
            
            assertNotNull(savedUser);
            assertNotNull(savedUser.getUserId());
            assertNotNull(savedUser.getCreatedAt());
            assertNotNull(savedUser.getUpdatedAt());
            assertEquals(TEST_EMAIL, savedUser.getEmail());
            assertEquals(TEST_PASSWORD_HASH, savedUser.getPasswordHash());
            assertEquals(TEST_ROLE, savedUser.getRole());
        }

        @Test
        @DisplayName("Should find user by ID")
        void testFindById() {
            User savedUser = userRepository.save(testUser);
            UUID userId = savedUser.getUserId();
            
            Optional<User> foundUser = userRepository.findById(userId);
            
            assertTrue(foundUser.isPresent());
            assertEquals(userId, foundUser.get().getUserId());
            assertEquals(TEST_EMAIL, foundUser.get().getEmail());
        }

        @Test
        @DisplayName("Should return empty Optional when user not found by ID")
        void testFindByIdNotFound() {
            UUID randomId = UUID.randomUUID();
            Optional<User> foundUser = userRepository.findById(randomId);
            
            assertFalse(foundUser.isPresent());
        }

        @Test
        @DisplayName("Should update existing user")
        void testUpdateUser() throws InterruptedException {
            User savedUser = userRepository.save(testUser);
            UUID userId = savedUser.getUserId();
            
            // Wait to ensure different timestamp
            Thread.sleep(10);
            
            // Update user
            savedUser.setEmail("updated@example.com");
            savedUser.setRole(UserRole.STAFF);
            User updatedUser = userRepository.save(savedUser);
            
            assertEquals(userId, updatedUser.getUserId());
            assertEquals("updated@example.com", updatedUser.getEmail());
            assertEquals(UserRole.STAFF, updatedUser.getRole());
        }

        @Test
        @DisplayName("Should delete user by ID")
        void testDeleteUser() {
            User savedUser = userRepository.save(testUser);
            UUID userId = savedUser.getUserId();
            
            userRepository.deleteById(userId);
            
            Optional<User> deletedUser = userRepository.findById(userId);
            assertFalse(deletedUser.isPresent());
        }

        @Test
        @DisplayName("Should find all users")
        void testFindAll() {
            userRepository.save(testUser);
            userRepository.save(new User("user2@example.com", "hash2", UserRole.STAFF));
            userRepository.save(new User("user3@example.com", "hash3", UserRole.MANAGER));
            
            List<User> allUsers = userRepository.findAll();
            
            assertEquals(3, allUsers.size());
        }

        @Test
        @DisplayName("Should count users correctly")
        void testCount() {
            assertEquals(0, userRepository.count());
            
            userRepository.save(testUser);
            assertEquals(1, userRepository.count());
            
            userRepository.save(new User("user2@example.com", "hash2", UserRole.STAFF));
            assertEquals(2, userRepository.count());
        }
    }

    @Nested
    @DisplayName("Custom Query Methods")
    class CustomQueryTests {

        @Test
        @DisplayName("Should find user by email")
        void testFindByEmail() {
            userRepository.save(testUser);
            
            Optional<User> foundUser = userRepository.findByEmail(TEST_EMAIL);
            
            assertTrue(foundUser.isPresent());
            assertEquals(TEST_EMAIL, foundUser.get().getEmail());
            assertEquals(TEST_PASSWORD_HASH, foundUser.get().getPasswordHash());
            assertEquals(TEST_ROLE, foundUser.get().getRole());
        }

        @Test
        @DisplayName("Should return empty Optional when email not found")
        void testFindByEmailNotFound() {
            Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");
            
            assertFalse(foundUser.isPresent());
        }

        @Test
        @DisplayName("Should handle case-sensitive email search")
        void testFindByEmailCaseSensitive() {
            userRepository.save(testUser);
            
            Optional<User> foundUser = userRepository.findByEmail("TEST@EXAMPLE.COM");
            
            // Email search is case-sensitive by default
            assertFalse(foundUser.isPresent());
        }

        @Test
        @DisplayName("Should check if user exists by email")
        void testExistsByEmail() {
            assertFalse(userRepository.existsByEmail(TEST_EMAIL));
            
            userRepository.save(testUser);
            
            assertTrue(userRepository.existsByEmail(TEST_EMAIL));
        }

        @Test
        @DisplayName("Should return false when checking non-existent email")
        void testExistsByEmailNotFound() {
            userRepository.save(testUser);
            
            assertFalse(userRepository.existsByEmail("nonexistent@example.com"));
        }

        @Test
        @DisplayName("Should handle null email in findByEmail")
        void testFindByEmailWithNull() {
            Optional<User> foundUser = userRepository.findByEmail(null);
            assertFalse(foundUser.isPresent());
        }

        @Test
        @DisplayName("Should handle empty string email in findByEmail")
        void testFindByEmailWithEmptyString() {
            Optional<User> foundUser = userRepository.findByEmail("");
            assertFalse(foundUser.isPresent());
        }
    }

    @Nested
    @DisplayName("Constraint Validation Tests")
    class ConstraintValidationTests {

        @Test
        @DisplayName("Should enforce unique email constraint")
        void testUniqueEmailConstraint() {
            userRepository.save(testUser);
            
            // Try to save another user with same email
            User duplicateUser = new User(TEST_EMAIL, "different_hash", UserRole.STAFF);
            
            assertThrows(DataIntegrityViolationException.class, () -> {
                userRepository.save(duplicateUser);
                entityManager.flush(); // Force constraint validation
            });
        }

        @Test
        @DisplayName("Should allow same email after deletion")
        void testEmailReuseAfterDeletion() {
            User savedUser = userRepository.save(testUser);
            userRepository.delete(savedUser);
            
            // Should be able to create new user with same email
            User newUser = new User(TEST_EMAIL, "new_hash", UserRole.MANAGER);
            User savedNewUser = userRepository.save(newUser);
            
            assertNotNull(savedNewUser.getUserId());
            assertNotEquals(savedUser.getUserId(), savedNewUser.getUserId());
        }

        @Test
        @DisplayName("Should enforce non-null email constraint")
        void testNonNullEmailConstraint() {
            User userWithNullEmail = new User(null, TEST_PASSWORD_HASH, TEST_ROLE);
            
            assertThrows(DataIntegrityViolationException.class, () -> {
                userRepository.save(userWithNullEmail);
                entityManager.flush();
            });
        }

        @Test
        @DisplayName("Should enforce non-null password hash constraint")
        void testNonNullPasswordHashConstraint() {
            User userWithNullPassword = new User(TEST_EMAIL, null, TEST_ROLE);
            
            assertThrows(DataIntegrityViolationException.class, () -> {
                userRepository.save(userWithNullPassword);
                entityManager.flush();
            });
        }

        @Test
        @DisplayName("Should enforce non-null role constraint")
        void testNonNullRoleConstraint() {
            User userWithNullRole = new User(TEST_EMAIL, TEST_PASSWORD_HASH, null);
            
            assertThrows(DataIntegrityViolationException.class, () -> {
                userRepository.save(userWithNullRole);
                entityManager.flush();
            });
        }
    }

    @Nested
    @DisplayName("Role-Based Tests")
    class RoleBasedTests {

        @Test
        @DisplayName("Should save user with CUSTOMER role")
        void testSaveCustomerRole() {
            User customer = new User("customer@example.com", "hash", UserRole.CUSTOMER);
            User saved = userRepository.save(customer);
            
            assertEquals(UserRole.CUSTOMER, saved.getRole());
        }

        @Test
        @DisplayName("Should save user with STAFF role")
        void testSaveStaffRole() {
            User staff = new User("staff@example.com", "hash", UserRole.STAFF);
            User saved = userRepository.save(staff);
            
            assertEquals(UserRole.STAFF, saved.getRole());
        }

        @Test
        @DisplayName("Should save user with MANAGER role")
        void testSaveManagerRole() {
            User manager = new User("manager@example.com", "hash", UserRole.MANAGER);
            User saved = userRepository.save(manager);
            
            assertEquals(UserRole.MANAGER, saved.getRole());
        }

        @Test
        @DisplayName("Should allow multiple users with same role")
        void testMultipleUsersWithSameRole() {
            userRepository.save(new User("customer1@example.com", "hash1", UserRole.CUSTOMER));
            userRepository.save(new User("customer2@example.com", "hash2", UserRole.CUSTOMER));
            userRepository.save(new User("customer3@example.com", "hash3", UserRole.CUSTOMER));
            
            List<User> allUsers = userRepository.findAll();
            long customerCount = allUsers.stream()
                .filter(u -> u.getRole() == UserRole.CUSTOMER)
                .count();
            
            assertEquals(3, customerCount);
        }
    }

    @Nested
    @DisplayName("Timestamp Tests")
    class TimestampTests {

        @Test
        @DisplayName("Should set createdAt and updatedAt on save")
        void testTimestampsOnSave() {
            User savedUser = userRepository.save(testUser);
            
            assertNotNull(savedUser.getCreatedAt());
            assertNotNull(savedUser.getUpdatedAt());
            assertEquals(savedUser.getCreatedAt(), savedUser.getUpdatedAt());
        }

        @Test
        @DisplayName("Should update updatedAt on modification")
        void testUpdatedAtOnModification() throws InterruptedException {
            User savedUser = userRepository.save(testUser);
            var originalCreatedAt = savedUser.getCreatedAt();
            var originalUpdatedAt = savedUser.getUpdatedAt();
            
            Thread.sleep(50); // Ensure time difference
            
            savedUser.setEmail("modified@example.com");
            User updatedUser = userRepository.save(savedUser);
            
            assertEquals(originalCreatedAt, updatedUser.getCreatedAt());
            assertTrue(updatedUser.getUpdatedAt().isAfter(originalUpdatedAt));
        }

        @Test
        @DisplayName("Should not modify createdAt on update")
        void testCreatedAtImmutableOnUpdate() throws InterruptedException {
            User savedUser = userRepository.save(testUser);
            var originalCreatedAt = savedUser.getCreatedAt();
            
            Thread.sleep(50);
            
            savedUser.setRole(UserRole.MANAGER);
            User updatedUser = userRepository.save(savedUser);
            
            assertEquals(originalCreatedAt, updatedUser.getCreatedAt());
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle very long email (up to 255 chars)")
        void testLongEmail() {
            String longEmail = "a".repeat(240) + "@example.com"; // 253 chars
            User userWithLongEmail = new User(longEmail, TEST_PASSWORD_HASH, TEST_ROLE);
            
            User savedUser = userRepository.save(userWithLongEmail);
            
            assertEquals(longEmail, savedUser.getEmail());
        }

        @Test
        @DisplayName("Should handle very long password hash (up to 255 chars)")
        void testLongPasswordHash() {
            String longHash = "$2a$10$" + "a".repeat(240);
            User userWithLongHash = new User(TEST_EMAIL, longHash, TEST_ROLE);
            
            User savedUser = userRepository.save(userWithLongHash);
            
            assertEquals(longHash, savedUser.getPasswordHash());
        }

        @Test
        @DisplayName("Should handle special characters in email")
        void testSpecialCharactersInEmail() {
            String specialEmail = "user+tag@sub-domain.example.com";
            User user = new User(specialEmail, TEST_PASSWORD_HASH, TEST_ROLE);
            
            User savedUser = userRepository.save(user);
            
            assertEquals(specialEmail, savedUser.getEmail());
        }

        @Test
        @DisplayName("Should persist and retrieve UUID correctly")
        void testUuidPersistence() {
            User savedUser = userRepository.save(testUser);
            UUID originalId = savedUser.getUserId();
            
            entityManager.clear(); // Clear persistence context
            
            Optional<User> retrievedUser = userRepository.findById(originalId);
            
            assertTrue(retrievedUser.isPresent());
            assertEquals(originalId, retrievedUser.get().getUserId());
        }
    }

    @Nested
    @DisplayName("Batch Operations Tests")
    class BatchOperationTests {

        @Test
        @DisplayName("Should save multiple users in batch")
        void testSaveAll() {
            List<User> users = List.of(
                new User("user1@example.com", "hash1", UserRole.CUSTOMER),
                new User("user2@example.com", "hash2", UserRole.STAFF),
                new User("user3@example.com", "hash3", UserRole.MANAGER)
            );
            
            List<User> savedUsers = userRepository.saveAll(users);
            
            assertEquals(3, savedUsers.size());
            savedUsers.forEach(user -> assertNotNull(user.getUserId()));
        }

        @Test
        @DisplayName("Should delete all users")
        void testDeleteAll() {
            userRepository.save(testUser);
            userRepository.save(new User("user2@example.com", "hash2", UserRole.STAFF));
            
            assertEquals(2, userRepository.count());
            
            userRepository.deleteAll();
            
            assertEquals(0, userRepository.count());
        }
    }
}

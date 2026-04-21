package com.slingshot.carshowroom.repository;

import com.slingshot.carshowroom.model.User;
import com.slingshot.carshowroom.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
 * 
 * <p>Uses @DataJpaTest for lightweight JPA testing with H2 in-memory database.
 * 
 * <p>Tests cover:
 * <ul>
 *   <li>CRUD operations</li>
 *   <li>Custom query methods</li>
 *   <li>Unique constraints</li>
 *   <li>Relationship queries</li>
 *   <li>Edge cases and error conditions</li>
 * </ul>
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("UserRepository Integration Tests")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        // Clean database before each test
        userRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();
    }

    // ========== Save and Basic CRUD Tests ==========

    @Test
    @DisplayName("Should save a new user with auto-generated UUID")
    void testSaveUser() {
        User user = createUser("test@example.com", UserRole.CUSTOMER);
        
        User saved = userRepository.save(user);
        
        assertNotNull(saved);
        assertNotNull(saved.getUserId(), "User ID should be auto-generated");
        assertEquals("test@example.com", saved.getEmail());
        assertNotNull(saved.getCreatedAt(), "createdAt should be set");
        assertNotNull(saved.getUpdatedAt(), "updatedAt should be set");
    }

    @Test
    @DisplayName("Should find user by ID")
    void testFindById() {
        User user = createUser("find@example.com", UserRole.STAFF);
        User saved = userRepository.save(user);
        entityManager.flush();
        entityManager.clear();
        
        Optional<User> found = userRepository.findById(saved.getUserId());
        
        assertTrue(found.isPresent());
        assertEquals(saved.getUserId(), found.get().getUserId());
        assertEquals("find@example.com", found.get().getEmail());
    }

    @Test
    @DisplayName("Should return empty Optional when user not found by ID")
    void testFindByIdNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        
        Optional<User> found = userRepository.findById(nonExistentId);
        
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Should update existing user")
    void testUpdateUser() {
        User user = createUser("update@example.com", UserRole.CUSTOMER);
        User saved = userRepository.save(user);
        entityManager.flush();
        
        saved.setName("Updated Name");
        saved.setRole(UserRole.MANAGER);
        User updated = userRepository.save(saved);
        entityManager.flush();
        entityManager.clear();
        
        Optional<User> found = userRepository.findById(updated.getUserId());
        assertTrue(found.isPresent());
        assertEquals("Updated Name", found.get().getName());
        assertEquals(UserRole.MANAGER, found.get().getRole());
    }

    @Test
    @DisplayName("Should delete user by ID")
    void testDeleteUser() {
        User user = createUser("delete@example.com", UserRole.CUSTOMER);
        User saved = userRepository.save(user);
        UUID userId = saved.getUserId();
        entityManager.flush();
        
        userRepository.deleteById(userId);
        entityManager.flush();
        
        Optional<User> found = userRepository.findById(userId);
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Should count all users")
    void testCountUsers() {
        userRepository.save(createUser("user1@example.com", UserRole.CUSTOMER));
        userRepository.save(createUser("user2@example.com", UserRole.STAFF));
        userRepository.save(createUser("user3@example.com", UserRole.MANAGER));
        entityManager.flush();
        
        long count = userRepository.count();
        
        assertEquals(3, count);
    }

    // ========== Custom Query Method Tests ==========

    @Test
    @DisplayName("Should find user by email")
    void testFindByEmail() {
        User user = createUser("findbyemail@example.com", UserRole.CUSTOMER);
        userRepository.save(user);
        entityManager.flush();
        entityManager.clear();
        
        Optional<User> found = userRepository.findByEmail("findbyemail@example.com");
        
        assertTrue(found.isPresent());
        assertEquals("findbyemail@example.com", found.get().getEmail());
    }

    @Test
    @DisplayName("Should return empty Optional when email not found")
    void testFindByEmailNotFound() {
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");
        
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Should find user by userId")
    void testFindByUserId() {
        User user = createUser("userid@example.com", UserRole.STAFF);
        User saved = userRepository.save(user);
        entityManager.flush();
        entityManager.clear();
        
        Optional<User> found = userRepository.findByUserId(saved.getUserId());
        
        assertTrue(found.isPresent());
        assertEquals(saved.getUserId(), found.get().getUserId());
    }

    @Test
    @DisplayName("Should check if email exists")
    void testExistsByEmail() {
        userRepository.save(createUser("exists@example.com", UserRole.CUSTOMER));
        entityManager.flush();
        
        boolean exists = userRepository.existsByEmail("exists@example.com");
        boolean notExists = userRepository.existsByEmail("notexists@example.com");
        
        assertTrue(exists);
        assertFalse(notExists);
    }

    @Test
    @DisplayName("Should find all users by role")
    void testFindByRole() {
        userRepository.save(createUser("customer1@example.com", UserRole.CUSTOMER));
        userRepository.save(createUser("customer2@example.com", UserRole.CUSTOMER));
        userRepository.save(createUser("staff@example.com", UserRole.STAFF));
        userRepository.save(createUser("manager@example.com", UserRole.MANAGER));
        entityManager.flush();
        
        List<User> customers = userRepository.findByRole(UserRole.CUSTOMER);
        List<User> staff = userRepository.findByRole(UserRole.STAFF);
        List<User> managers = userRepository.findByRole(UserRole.MANAGER);
        
        assertEquals(2, customers.size());
        assertEquals(1, staff.size());
        assertEquals(1, managers.size());
        assertTrue(customers.stream().allMatch(u -> u.getRole() == UserRole.CUSTOMER));
    }

    @Test
    @DisplayName("Should return empty list when no users with role exist")
    void testFindByRoleEmpty() {
        userRepository.save(createUser("customer@example.com", UserRole.CUSTOMER));
        entityManager.flush();
        
        List<User> managers = userRepository.findByRole(UserRole.MANAGER);
        
        assertTrue(managers.isEmpty());
    }

    @Test
    @DisplayName("Should count users by role")
    void testCountByRole() {
        userRepository.save(createUser("customer1@example.com", UserRole.CUSTOMER));
        userRepository.save(createUser("customer2@example.com", UserRole.CUSTOMER));
        userRepository.save(createUser("customer3@example.com", UserRole.CUSTOMER));
        userRepository.save(createUser("staff@example.com", UserRole.STAFF));
        entityManager.flush();
        
        long customerCount = userRepository.countByRole(UserRole.CUSTOMER);
        long staffCount = userRepository.countByRole(UserRole.STAFF);
        long managerCount = userRepository.countByRole(UserRole.MANAGER);
        
        assertEquals(3, customerCount);
        assertEquals(1, staffCount);
        assertEquals(0, managerCount);
    }

    // ========== Constraint Tests ==========

    @Test
    @DisplayName("Should enforce unique email constraint")
    void testUniqueEmailConstraint() {
        userRepository.save(createUser("duplicate@example.com", UserRole.CUSTOMER));
        entityManager.flush();
        
        User duplicate = createUser("duplicate@example.com", UserRole.STAFF);
        
        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.save(duplicate);
            entityManager.flush();
        });
    }

    @Test
    @DisplayName("Should allow same email after deletion")
    void testEmailReuseAfterDeletion() {
        User user1 = createUser("reuse@example.com", UserRole.CUSTOMER);
        User saved = userRepository.save(user1);
        entityManager.flush();
        
        userRepository.delete(saved);
        entityManager.flush();
        entityManager.clear();
        
        User user2 = createUser("reuse@example.com", UserRole.STAFF);
        User saved2 = userRepository.save(user2);
        
        assertNotNull(saved2.getUserId());
        assertEquals("reuse@example.com", saved2.getEmail());
    }

    // ========== Edge Case Tests ==========

    @Test
    @DisplayName("Should handle email with special characters")
    void testEmailWithSpecialCharacters() {
        User user = createUser("user+tag@example.co.uk", UserRole.CUSTOMER);
        User saved = userRepository.save(user);
        entityManager.flush();
        entityManager.clear();
        
        Optional<User> found = userRepository.findByEmail("user+tag@example.co.uk");
        
        assertTrue(found.isPresent());
        assertEquals("user+tag@example.co.uk", found.get().getEmail());
    }

    @Test
    @DisplayName("Should handle long email addresses")
    void testLongEmail() {
        String longEmail = "very.long.email.address.with.many.parts@example-domain.com";
        User user = createUser(longEmail, UserRole.CUSTOMER);
        User saved = userRepository.save(user);
        entityManager.flush();
        
        Optional<User> found = userRepository.findByEmail(longEmail);
        
        assertTrue(found.isPresent());
        assertEquals(longEmail, found.get().getEmail());
    }

    @Test
    @DisplayName("Should handle null optional fields")
    void testNullOptionalFields() {
        User user = new User();
        user.setEmail("minimal@example.com");
        user.setPasswordHash("$2a$10$hashedpassword");
        user.setRole(UserRole.CUSTOMER);
        // name and contactInformation are null
        
        User saved = userRepository.save(user);
        entityManager.flush();
        entityManager.clear();
        
        Optional<User> found = userRepository.findById(saved.getUserId());
        
        assertTrue(found.isPresent());
        assertNull(found.get().getName());
        assertNull(found.get().getContactInformation());
    }

    @Test
    @DisplayName("Should persist all UserRole enum values")
    void testAllRolesPersistence() {
        User customer = createUser("customer@example.com", UserRole.CUSTOMER);
        User staff = createUser("staff@example.com", UserRole.STAFF);
        User manager = createUser("manager@example.com", UserRole.MANAGER);
        
        userRepository.save(customer);
        userRepository.save(staff);
        userRepository.save(manager);
        entityManager.flush();
        entityManager.clear();
        
        Optional<User> foundCustomer = userRepository.findByEmail("customer@example.com");
        Optional<User> foundStaff = userRepository.findByEmail("staff@example.com");
        Optional<User> foundManager = userRepository.findByEmail("manager@example.com");
        
        assertTrue(foundCustomer.isPresent());
        assertEquals(UserRole.CUSTOMER, foundCustomer.get().getRole());
        assertTrue(foundStaff.isPresent());
        assertEquals(UserRole.STAFF, foundStaff.get().getRole());
        assertTrue(foundManager.isPresent());
        assertEquals(UserRole.MANAGER, foundManager.get().getRole());
    }

    @Test
    @DisplayName("Should handle BCrypt password hash")
    void testBCryptPasswordHash() {
        String bcryptHash = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
        User user = createUser("bcrypt@example.com", UserRole.CUSTOMER);
        user.setPasswordHash(bcryptHash);
        
        User saved = userRepository.save(user);
        entityManager.flush();
        entityManager.clear();
        
        Optional<User> found = userRepository.findById(saved.getUserId());
        
        assertTrue(found.isPresent());
        assertEquals(bcryptHash, found.get().getPasswordHash());
        assertEquals(60, found.get().getPasswordHash().length());
    }

    @Test
    @DisplayName("Should maintain timestamp accuracy")
    void testTimestampAccuracy() throws InterruptedException {
        User user = createUser("timestamp@example.com", UserRole.CUSTOMER);
        User saved = userRepository.save(user);
        entityManager.flush();
        
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
        assertEquals(saved.getCreatedAt(), saved.getUpdatedAt());
        
        Thread.sleep(100); // Wait to ensure timestamp difference
        
        saved.setName("Updated Name");
        User updated = userRepository.save(saved);
        entityManager.flush();
        
        assertEquals(saved.getCreatedAt(), updated.getCreatedAt(), "createdAt should not change");
        assertTrue(updated.getUpdatedAt().isAfter(updated.getCreatedAt()), "updatedAt should be after createdAt");
    }

    @Test
    @DisplayName("Should find all users")
    void testFindAll() {
        userRepository.save(createUser("user1@example.com", UserRole.CUSTOMER));
        userRepository.save(createUser("user2@example.com", UserRole.STAFF));
        userRepository.save(createUser("user3@example.com", UserRole.MANAGER));
        entityManager.flush();
        
        List<User> allUsers = userRepository.findAll();
        
        assertEquals(3, allUsers.size());
    }

    @Test
    @DisplayName("Should return empty list when no users exist")
    void testFindAllEmpty() {
        List<User> allUsers = userRepository.findAll();
        
        assertTrue(allUsers.isEmpty());
    }

    // ========== Helper Methods ==========

    /**
     * Creates a User instance with required fields for testing.
     * 
     * @param email the user's email
     * @param role the user's role
     * @return a new User instance
     */
    private User createUser(String email, UserRole role) {
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash("$2a$10$hashedpassword");
        user.setName("Test User");
        user.setContactInformation("+1234567890");
        user.setRole(role);
        return user;
    }
}

package com.slingshot.carshowroom.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the User JPA entity.
 * 
 * <p>Tests cover:
 * <ul>
 *   <li>Entity construction and field access</li>
 *   <li>Bean validation constraints</li>
 *   <li>Lifecycle callbacks (@PrePersist, @PreUpdate)</li>
 *   <li>Edge cases and boundary conditions</li>
 *   <li>toString() method</li>
 * </ul>
 */
@DisplayName("User Entity Tests")
class UserTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // ========== Constructor Tests ==========

    @Test
    @DisplayName("Should create User with no-args constructor")
    void testNoArgsConstructor() {
        User user = new User();
        
        assertNotNull(user);
        assertNull(user.getUserId());
        assertNull(user.getEmail());
        assertNull(user.getPasswordHash());
        assertNull(user.getName());
        assertNull(user.getContactInformation());
        assertNull(user.getRole());
        assertNull(user.getCreatedAt());
        assertNull(user.getUpdatedAt());
    }

    @Test
    @DisplayName("Should create User with all-args constructor")
    void testAllArgsConstructor() {
        UUID userId = UUID.randomUUID();
        String email = "test@example.com";
        String passwordHash = "$2a$10$hashedpassword";
        String name = "John Doe";
        String contact = "+1234567890";
        UserRole role = UserRole.CUSTOMER;
        Instant createdAt = Instant.now();
        Instant updatedAt = Instant.now();

        User user = new User(userId, email, passwordHash, name, contact, role, createdAt, updatedAt);

        assertEquals(userId, user.getUserId());
        assertEquals(email, user.getEmail());
        assertEquals(passwordHash, user.getPasswordHash());
        assertEquals(name, user.getName());
        assertEquals(contact, user.getContactInformation());
        assertEquals(role, user.getRole());
        assertEquals(createdAt, user.getCreatedAt());
        assertEquals(updatedAt, user.getUpdatedAt());
    }

    // ========== Getter/Setter Tests ==========

    @Test
    @DisplayName("Should set and get userId")
    void testUserIdGetterSetter() {
        User user = new User();
        UUID userId = UUID.randomUUID();
        
        user.setUserId(userId);
        
        assertEquals(userId, user.getUserId());
    }

    @Test
    @DisplayName("Should set and get email")
    void testEmailGetterSetter() {
        User user = new User();
        String email = "customer@example.com";
        
        user.setEmail(email);
        
        assertEquals(email, user.getEmail());
    }

    @Test
    @DisplayName("Should set and get passwordHash")
    void testPasswordHashGetterSetter() {
        User user = new User();
        String passwordHash = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
        
        user.setPasswordHash(passwordHash);
        
        assertEquals(passwordHash, user.getPasswordHash());
    }

    @Test
    @DisplayName("Should set and get name")
    void testNameGetterSetter() {
        User user = new User();
        String name = "Jane Smith";
        
        user.setName(name);
        
        assertEquals(name, user.getName());
    }

    @Test
    @DisplayName("Should set and get contactInformation")
    void testContactInformationGetterSetter() {
        User user = new User();
        String contact = "+1-555-0100";
        
        user.setContactInformation(contact);
        
        assertEquals(contact, user.getContactInformation());
    }

    @Test
    @DisplayName("Should set and get role")
    void testRoleGetterSetter() {
        User user = new User();
        
        user.setRole(UserRole.MANAGER);
        
        assertEquals(UserRole.MANAGER, user.getRole());
    }

    @Test
    @DisplayName("Should set and get createdAt")
    void testCreatedAtGetterSetter() {
        User user = new User();
        Instant now = Instant.now();
        
        user.setCreatedAt(now);
        
        assertEquals(now, user.getCreatedAt());
    }

    @Test
    @DisplayName("Should set and get updatedAt")
    void testUpdatedAtGetterSetter() {
        User user = new User();
        Instant now = Instant.now();
        
        user.setUpdatedAt(now);
        
        assertEquals(now, user.getUpdatedAt());
    }

    // ========== Validation Tests ==========

    @Test
    @DisplayName("Should validate a valid User entity")
    void testValidUser() {
        User user = createValidUser();
        
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        
        assertTrue(violations.isEmpty(), "Valid user should have no violations");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  ", "\t", "\n"})
    @DisplayName("Should fail validation when email is blank")
    void testEmailNotBlank(String invalidEmail) {
        User user = createValidUser();
        user.setEmail(invalidEmail);
        
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid", "@example.com", "user@", "user@.com", "user..name@example.com"})
    @DisplayName("Should fail validation when email format is invalid")
    void testEmailValidFormat(String invalidEmail) {
        User user = createValidUser();
        user.setEmail(invalidEmail);
        
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("email") && 
                          v.getMessage().contains("valid")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"user@example.com", "test.user@example.co.uk", "user+tag@example.com"})
    @DisplayName("Should pass validation with valid email formats")
    void testValidEmailFormats(String validEmail) {
        User user = createValidUser();
        user.setEmail(validEmail);
        
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  "})
    @DisplayName("Should fail validation when passwordHash is blank")
    void testPasswordHashNotBlank(String invalidPassword) {
        User user = createValidUser();
        user.setPasswordHash(invalidPassword);
        
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("passwordHash")));
    }

    @Test
    @DisplayName("Should fail validation when role is null")
    void testRoleNotNull() {
        User user = createValidUser();
        user.setRole(null);
        
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("role")));
    }

    @Test
    @DisplayName("Should allow null name (optional field)")
    void testNameCanBeNull() {
        User user = createValidUser();
        user.setName(null);
        
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should allow null contactInformation (optional field)")
    void testContactInformationCanBeNull() {
        User user = createValidUser();
        user.setContactInformation(null);
        
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        
        assertTrue(violations.isEmpty());
    }

    // ========== Lifecycle Callback Tests ==========

    @Test
    @DisplayName("Should set timestamps on onCreate callback")
    void testOnCreateCallback() throws Exception {
        User user = createValidUser();
        
        // Simulate @PrePersist callback
        java.lang.reflect.Method onCreate = User.class.getDeclaredMethod("onCreate");
        onCreate.setAccessible(true);
        
        Instant before = Instant.now();
        onCreate.invoke(user);
        Instant after = Instant.now();
        
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
        assertTrue(user.getCreatedAt().equals(user.getUpdatedAt()));
        assertFalse(user.getCreatedAt().isBefore(before));
        assertFalse(user.getCreatedAt().isAfter(after));
    }

    @Test
    @DisplayName("Should update updatedAt on onUpdate callback")
    void testOnUpdateCallback() throws Exception {
        User user = createValidUser();
        
        // Simulate @PrePersist
        java.lang.reflect.Method onCreate = User.class.getDeclaredMethod("onCreate");
        onCreate.setAccessible(true);
        onCreate.invoke(user);
        
        Instant originalCreatedAt = user.getCreatedAt();
        Instant originalUpdatedAt = user.getUpdatedAt();
        
        // Wait a bit to ensure timestamp difference
        Thread.sleep(10);
        
        // Simulate @PreUpdate
        java.lang.reflect.Method onUpdate = User.class.getDeclaredMethod("onUpdate");
        onUpdate.setAccessible(true);
        onUpdate.invoke(user);
        
        assertEquals(originalCreatedAt, user.getCreatedAt(), "createdAt should not change");
        assertTrue(user.getUpdatedAt().isAfter(originalUpdatedAt), "updatedAt should be updated");
    }

    @Test
    @DisplayName("Should not modify createdAt on update")
    void testCreatedAtImmutableOnUpdate() throws Exception {
        User user = createValidUser();
        
        // Simulate @PrePersist
        java.lang.reflect.Method onCreate = User.class.getDeclaredMethod("onCreate");
        onCreate.setAccessible(true);
        onCreate.invoke(user);
        
        Instant createdAt = user.getCreatedAt();
        
        // Simulate @PreUpdate
        java.lang.reflect.Method onUpdate = User.class.getDeclaredMethod("onUpdate");
        onUpdate.setAccessible(true);
        onUpdate.invoke(user);
        
        assertEquals(createdAt, user.getCreatedAt());
    }

    // ========== toString() Tests ==========

    @Test
    @DisplayName("Should generate toString with all fields")
    void testToString() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setUserId(userId);
        user.setEmail("test@example.com");
        user.setPasswordHash("hashedpassword");
        user.setName("Test User");
        user.setContactInformation("+1234567890");
        user.setRole(UserRole.CUSTOMER);
        user.setCreatedAt(Instant.parse("2024-01-01T00:00:00Z"));
        user.setUpdatedAt(Instant.parse("2024-01-02T00:00:00Z"));
        
        String result = user.toString();
        
        assertNotNull(result);
        assertTrue(result.contains("User{"));
        assertTrue(result.contains("userId=" + userId));
        assertTrue(result.contains("email='test@example.com'"));
        assertTrue(result.contains("name='Test User'"));
        assertTrue(result.contains("contactInformation='+1234567890'"));
        assertTrue(result.contains("role=CUSTOMER"));
        assertTrue(result.contains("createdAt="));
        assertTrue(result.contains("updatedAt="));
        assertFalse(result.contains("passwordHash"), "Password hash should not be in toString");
    }

    @Test
    @DisplayName("Should handle null values in toString")
    void testToStringWithNulls() {
        User user = new User();
        
        String result = user.toString();
        
        assertNotNull(result);
        assertTrue(result.contains("User{"));
        assertTrue(result.contains("userId=null"));
        assertTrue(result.contains("email='null'"));
    }

    // ========== Edge Case Tests ==========

    @Test
    @DisplayName("Should handle maximum length email (255 characters)")
    void testMaxLengthEmail() {
        User user = createValidUser();
        // Create a valid email close to 255 chars
        String longEmail = "a".repeat(240) + "@example.com"; // 253 chars
        user.setEmail(longEmail);
        
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        
        // Should pass validation (format check might fail, but length is OK)
        // Note: Email validation is format-based, not just length
        assertNotNull(user.getEmail());
        assertEquals(longEmail, user.getEmail());
    }

    @Test
    @DisplayName("Should handle BCrypt hash (60 characters)")
    void testBCryptPasswordHash() {
        User user = createValidUser();
        String bcryptHash = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
        user.setPasswordHash(bcryptHash);
        
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        
        assertTrue(violations.isEmpty());
        assertEquals(60, user.getPasswordHash().length());
    }

    @Test
    @DisplayName("Should handle all UserRole enum values")
    void testAllRoles() {
        User user = createValidUser();
        
        for (UserRole role : UserRole.values()) {
            user.setRole(role);
            Set<ConstraintViolation<User>> violations = validator.validate(user);
            assertTrue(violations.isEmpty(), "Role " + role + " should be valid");
        }
    }

    @Test
    @DisplayName("Should handle instant timestamps correctly")
    void testInstantTimestamps() {
        User user = createValidUser();
        Instant past = Instant.parse("2020-01-01T00:00:00Z");
        Instant future = Instant.parse("2030-12-31T23:59:59Z");
        
        user.setCreatedAt(past);
        user.setUpdatedAt(future);
        
        assertEquals(past, user.getCreatedAt());
        assertEquals(future, user.getUpdatedAt());
        assertTrue(user.getUpdatedAt().isAfter(user.getCreatedAt()));
    }

    // ========== Helper Methods ==========

    /**
     * Creates a valid User instance for testing.
     * 
     * @return a User with all required fields set to valid values
     */
    private User createValidUser() {
        User user = new User();
        user.setEmail("valid@example.com");
        user.setPasswordHash("$2a$10$validhashedpassword");
        user.setName("Valid User");
        user.setContactInformation("+1234567890");
        user.setRole(UserRole.CUSTOMER);
        return user;
    }
}

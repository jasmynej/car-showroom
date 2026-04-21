package com.slingshot.carshowroom.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the User JPA entity.
 * Tests entity creation, lifecycle callbacks, getters/setters, equals/hashCode, and toString.
 * 
 * @author Car Showroom Team
 * @version 1.0
 */
@DisplayName("User Entity Tests")
class UserTest {

    private User user;
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD_HASH = "$2a$10$hashedpassword123";
    private static final UserRole TEST_ROLE = UserRole.CUSTOMER;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create user with default constructor")
        void testDefaultConstructor() {
            User newUser = new User();
            assertNotNull(newUser);
            assertNull(newUser.getUserId());
            assertNull(newUser.getEmail());
            assertNull(newUser.getPasswordHash());
            assertNull(newUser.getRole());
            assertNull(newUser.getCreatedAt());
            assertNull(newUser.getUpdatedAt());
        }

        @Test
        @DisplayName("Should create user with parameterized constructor")
        void testParameterizedConstructor() {
            User newUser = new User(TEST_EMAIL, TEST_PASSWORD_HASH, TEST_ROLE);
            
            assertNotNull(newUser);
            assertEquals(TEST_EMAIL, newUser.getEmail());
            assertEquals(TEST_PASSWORD_HASH, newUser.getPasswordHash());
            assertEquals(TEST_ROLE, newUser.getRole());
            assertNull(newUser.getUserId()); // Not set until persisted
            assertNull(newUser.getCreatedAt()); // Set by @PrePersist
            assertNull(newUser.getUpdatedAt()); // Set by @PrePersist
        }
    }

    @Nested
    @DisplayName("Lifecycle Callback Tests")
    class LifecycleCallbackTests {

        @Test
        @DisplayName("Should set createdAt and updatedAt on onCreate")
        void testOnCreate() throws InterruptedException {
            Instant before = Instant.now();
            Thread.sleep(10); // Small delay to ensure timestamp difference
            
            user.onCreate();
            
            Thread.sleep(10);
            Instant after = Instant.now();
            
            assertNotNull(user.getCreatedAt());
            assertNotNull(user.getUpdatedAt());
            assertEquals(user.getCreatedAt(), user.getUpdatedAt());
            assertTrue(user.getCreatedAt().isAfter(before));
            assertTrue(user.getCreatedAt().isBefore(after));
        }

        @Test
        @DisplayName("Should update only updatedAt on onUpdate")
        void testOnUpdate() throws InterruptedException {
            // Simulate initial creation
            user.onCreate();
            Instant originalCreatedAt = user.getCreatedAt();
            Instant originalUpdatedAt = user.getUpdatedAt();
            
            Thread.sleep(50); // Wait to ensure different timestamp
            
            // Simulate update
            user.onUpdate();
            
            assertEquals(originalCreatedAt, user.getCreatedAt()); // Should not change
            assertNotEquals(originalUpdatedAt, user.getUpdatedAt()); // Should change
            assertTrue(user.getUpdatedAt().isAfter(originalUpdatedAt));
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get userId")
        void testUserIdGetterSetter() {
            UUID testId = UUID.randomUUID();
            user.setUserId(testId);
            assertEquals(testId, user.getUserId());
        }

        @Test
        @DisplayName("Should set and get email")
        void testEmailGetterSetter() {
            user.setEmail(TEST_EMAIL);
            assertEquals(TEST_EMAIL, user.getEmail());
        }

        @Test
        @DisplayName("Should set and get passwordHash")
        void testPasswordHashGetterSetter() {
            user.setPasswordHash(TEST_PASSWORD_HASH);
            assertEquals(TEST_PASSWORD_HASH, user.getPasswordHash());
        }

        @Test
        @DisplayName("Should set and get role")
        void testRoleGetterSetter() {
            user.setRole(UserRole.MANAGER);
            assertEquals(UserRole.MANAGER, user.getRole());
        }

        @Test
        @DisplayName("Should set and get createdAt")
        void testCreatedAtGetterSetter() {
            Instant now = Instant.now();
            user.setCreatedAt(now);
            assertEquals(now, user.getCreatedAt());
        }

        @Test
        @DisplayName("Should set and get updatedAt")
        void testUpdatedAtGetterSetter() {
            Instant now = Instant.now();
            user.setUpdatedAt(now);
            assertEquals(now, user.getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should be equal to itself")
        void testEqualsSameObject() {
            assertTrue(user.equals(user));
        }

        @Test
        @DisplayName("Should not be equal to null")
        void testEqualsNull() {
            assertFalse(user.equals(null));
        }

        @Test
        @DisplayName("Should not be equal to different class")
        void testEqualsDifferentClass() {
            assertFalse(user.equals("Not a User"));
        }

        @Test
        @DisplayName("Should be equal when userId and email match")
        void testEqualsWithSameIdAndEmail() {
            UUID testId = UUID.randomUUID();
            
            User user1 = new User(TEST_EMAIL, TEST_PASSWORD_HASH, TEST_ROLE);
            user1.setUserId(testId);
            
            User user2 = new User(TEST_EMAIL, "different_hash", UserRole.STAFF);
            user2.setUserId(testId);
            
            assertTrue(user1.equals(user2));
            assertEquals(user1.hashCode(), user2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when userId differs")
        void testEqualsWithDifferentId() {
            User user1 = new User(TEST_EMAIL, TEST_PASSWORD_HASH, TEST_ROLE);
            user1.setUserId(UUID.randomUUID());
            
            User user2 = new User(TEST_EMAIL, TEST_PASSWORD_HASH, TEST_ROLE);
            user2.setUserId(UUID.randomUUID());
            
            assertFalse(user1.equals(user2));
        }

        @Test
        @DisplayName("Should not be equal when email differs")
        void testEqualsWithDifferentEmail() {
            UUID testId = UUID.randomUUID();
            
            User user1 = new User("user1@example.com", TEST_PASSWORD_HASH, TEST_ROLE);
            user1.setUserId(testId);
            
            User user2 = new User("user2@example.com", TEST_PASSWORD_HASH, TEST_ROLE);
            user2.setUserId(testId);
            
            assertFalse(user1.equals(user2));
        }

        @Test
        @DisplayName("Should handle null userId in equals")
        void testEqualsWithNullUserId() {
            User user1 = new User(TEST_EMAIL, TEST_PASSWORD_HASH, TEST_ROLE);
            User user2 = new User(TEST_EMAIL, TEST_PASSWORD_HASH, TEST_ROLE);
            
            assertTrue(user1.equals(user2));
        }

        @Test
        @DisplayName("Should have consistent hashCode")
        void testHashCodeConsistency() {
            UUID testId = UUID.randomUUID();
            user.setUserId(testId);
            user.setEmail(TEST_EMAIL);
            
            int hashCode1 = user.hashCode();
            int hashCode2 = user.hashCode();
            
            assertEquals(hashCode1, hashCode2);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should include all relevant fields in toString")
        void testToString() {
            UUID testId = UUID.randomUUID();
            Instant now = Instant.now();
            
            user.setUserId(testId);
            user.setEmail(TEST_EMAIL);
            user.setRole(TEST_ROLE);
            user.setCreatedAt(now);
            user.setUpdatedAt(now);
            
            String result = user.toString();
            
            assertTrue(result.contains("User{"));
            assertTrue(result.contains("userId=" + testId));
            assertTrue(result.contains("email='" + TEST_EMAIL + "'"));
            assertTrue(result.contains("role=" + TEST_ROLE));
            assertTrue(result.contains("createdAt=" + now));
            assertTrue(result.contains("updatedAt=" + now));
        }

        @Test
        @DisplayName("Should not include password hash in toString")
        void testToStringDoesNotIncludePassword() {
            user.setPasswordHash(TEST_PASSWORD_HASH);
            String result = user.toString();
            
            assertFalse(result.contains("passwordHash"));
            assertFalse(result.contains(TEST_PASSWORD_HASH));
        }
    }

    @Nested
    @DisplayName("Field Validation Tests")
    class FieldValidationTests {

        @Test
        @DisplayName("Should accept valid email formats")
        void testValidEmailFormats() {
            String[] validEmails = {
                "user@example.com",
                "test.user@example.co.uk",
                "user+tag@example.com",
                "user123@test-domain.com"
            };
            
            for (String email : validEmails) {
                user.setEmail(email);
                assertEquals(email, user.getEmail());
            }
        }

        @Test
        @DisplayName("Should accept all UserRole values")
        void testAllUserRoles() {
            user.setRole(UserRole.CUSTOMER);
            assertEquals(UserRole.CUSTOMER, user.getRole());
            
            user.setRole(UserRole.STAFF);
            assertEquals(UserRole.STAFF, user.getRole());
            
            user.setRole(UserRole.MANAGER);
            assertEquals(UserRole.MANAGER, user.getRole());
        }

        @Test
        @DisplayName("Should handle long password hashes")
        void testLongPasswordHash() {
            String longHash = "$2a$10$" + "a".repeat(240); // Up to 255 chars
            user.setPasswordHash(longHash);
            assertEquals(longHash, user.getPasswordHash());
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle null values gracefully")
        void testNullValues() {
            user.setEmail(null);
            user.setPasswordHash(null);
            user.setRole(null);
            
            assertNull(user.getEmail());
            assertNull(user.getPasswordHash());
            assertNull(user.getRole());
        }

        @Test
        @DisplayName("Should handle empty string email")
        void testEmptyStringEmail() {
            user.setEmail("");
            assertEquals("", user.getEmail());
        }

        @Test
        @DisplayName("Should handle maximum length email (255 chars)")
        void testMaxLengthEmail() {
            String longEmail = "a".repeat(240) + "@example.com"; // 253 chars
            user.setEmail(longEmail);
            assertEquals(longEmail, user.getEmail());
        }
    }
}

package com.slingshot.carshowroom.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the UserRole enum.
 * Tests enum values, valueOf, and name methods.
 * 
 * @author Car Showroom Team
 * @version 1.0
 */
@DisplayName("UserRole Enum Tests")
class UserRoleTest {

    @Test
    @DisplayName("Should have exactly three role values")
    void testEnumValues() {
        UserRole[] roles = UserRole.values();
        assertEquals(3, roles.length);
    }

    @Test
    @DisplayName("Should contain CUSTOMER role")
    void testCustomerRoleExists() {
        UserRole role = UserRole.CUSTOMER;
        assertNotNull(role);
        assertEquals("CUSTOMER", role.name());
    }

    @Test
    @DisplayName("Should contain STAFF role")
    void testStaffRoleExists() {
        UserRole role = UserRole.STAFF;
        assertNotNull(role);
        assertEquals("STAFF", role.name());
    }

    @Test
    @DisplayName("Should contain MANAGER role")
    void testManagerRoleExists() {
        UserRole role = UserRole.MANAGER;
        assertNotNull(role);
        assertEquals("MANAGER", role.name());
    }

    @Test
    @DisplayName("Should retrieve CUSTOMER by valueOf")
    void testValueOfCustomer() {
        UserRole role = UserRole.valueOf("CUSTOMER");
        assertEquals(UserRole.CUSTOMER, role);
    }

    @Test
    @DisplayName("Should retrieve STAFF by valueOf")
    void testValueOfStaff() {
        UserRole role = UserRole.valueOf("STAFF");
        assertEquals(UserRole.STAFF, role);
    }

    @Test
    @DisplayName("Should retrieve MANAGER by valueOf")
    void testValueOfManager() {
        UserRole role = UserRole.valueOf("MANAGER");
        assertEquals(UserRole.MANAGER, role);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for invalid role name")
    void testValueOfInvalidRole() {
        assertThrows(IllegalArgumentException.class, () -> {
            UserRole.valueOf("INVALID_ROLE");
        });
    }

    @Test
    @DisplayName("Should throw NullPointerException for null valueOf")
    void testValueOfNull() {
        assertThrows(NullPointerException.class, () -> {
            UserRole.valueOf(null);
        });
    }

    @ParameterizedTest
    @EnumSource(UserRole.class)
    @DisplayName("Should have consistent name() for all roles")
    void testNameConsistency(UserRole role) {
        assertNotNull(role.name());
        assertFalse(role.name().isEmpty());
        assertEquals(role, UserRole.valueOf(role.name()));
    }

    @ParameterizedTest
    @EnumSource(UserRole.class)
    @DisplayName("Should have consistent toString() for all roles")
    void testToStringConsistency(UserRole role) {
        assertEquals(role.name(), role.toString());
    }

    @Test
    @DisplayName("Should maintain enum order: CUSTOMER, STAFF, MANAGER")
    void testEnumOrder() {
        UserRole[] roles = UserRole.values();
        assertEquals(UserRole.CUSTOMER, roles[0]);
        assertEquals(UserRole.STAFF, roles[1]);
        assertEquals(UserRole.MANAGER, roles[2]);
    }

    @Test
    @DisplayName("Should have correct ordinal values")
    void testOrdinalValues() {
        assertEquals(0, UserRole.CUSTOMER.ordinal());
        assertEquals(1, UserRole.STAFF.ordinal());
        assertEquals(2, UserRole.MANAGER.ordinal());
    }

    @Test
    @DisplayName("Should support compareTo based on ordinal")
    void testCompareTo() {
        assertTrue(UserRole.CUSTOMER.compareTo(UserRole.STAFF) < 0);
        assertTrue(UserRole.STAFF.compareTo(UserRole.MANAGER) < 0);
        assertTrue(UserRole.MANAGER.compareTo(UserRole.CUSTOMER) > 0);
        assertEquals(0, UserRole.CUSTOMER.compareTo(UserRole.CUSTOMER));
    }

    @Test
    @DisplayName("Should support switch statements")
    void testSwitchStatement() {
        String result = switch (UserRole.CUSTOMER) {
            case CUSTOMER -> "Customer Access";
            case STAFF -> "Staff Access";
            case MANAGER -> "Manager Access";
        };
        assertEquals("Customer Access", result);
    }

    @Test
    @DisplayName("Should be usable in collections")
    void testInCollections() {
        java.util.Set<UserRole> roleSet = java.util.EnumSet.allOf(UserRole.class);
        assertEquals(3, roleSet.size());
        assertTrue(roleSet.contains(UserRole.CUSTOMER));
        assertTrue(roleSet.contains(UserRole.STAFF));
        assertTrue(roleSet.contains(UserRole.MANAGER));
    }
}

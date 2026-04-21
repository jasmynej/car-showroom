package com.slingshot.carshowroom.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the UserRole enum.
 * 
 * <p>Tests cover:
 * <ul>
 *   <li>Enum values existence</li>
 *   <li>Enum valueOf() method</li>
 *   <li>Enum values() method</li>
 *   <li>String representation</li>
 * </ul>
 */
@DisplayName("UserRole Enum Tests")
class UserRoleTest {

    @Test
    @DisplayName("Should have exactly three role values")
    void testEnumValuesCount() {
        UserRole[] roles = UserRole.values();
        
        assertEquals(3, roles.length, "UserRole should have exactly 3 values");
    }

    @Test
    @DisplayName("Should contain CUSTOMER role")
    void testCustomerRoleExists() {
        UserRole role = UserRole.valueOf("CUSTOMER");
        
        assertNotNull(role);
        assertEquals(UserRole.CUSTOMER, role);
    }

    @Test
    @DisplayName("Should contain STAFF role")
    void testStaffRoleExists() {
        UserRole role = UserRole.valueOf("STAFF");
        
        assertNotNull(role);
        assertEquals(UserRole.STAFF, role);
    }

    @Test
    @DisplayName("Should contain MANAGER role")
    void testManagerRoleExists() {
        UserRole role = UserRole.valueOf("MANAGER");
        
        assertNotNull(role);
        assertEquals(UserRole.MANAGER, role);
    }

    @ParameterizedTest
    @EnumSource(UserRole.class)
    @DisplayName("Should convert each role to string")
    void testEnumToString(UserRole role) {
        String roleName = role.toString();
        
        assertNotNull(roleName);
        assertFalse(roleName.isEmpty());
        assertTrue(roleName.matches("CUSTOMER|STAFF|MANAGER"));
    }

    @ParameterizedTest
    @EnumSource(UserRole.class)
    @DisplayName("Should convert each role name() to uppercase string")
    void testEnumName(UserRole role) {
        String name = role.name();
        
        assertNotNull(name);
        assertEquals(name, name.toUpperCase(), "Enum name should be uppercase");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for invalid role name")
    void testInvalidRoleName() {
        assertThrows(IllegalArgumentException.class, () -> {
            UserRole.valueOf("INVALID_ROLE");
        });
    }

    @Test
    @DisplayName("Should throw NullPointerException for null role name")
    void testNullRoleName() {
        assertThrows(NullPointerException.class, () -> {
            UserRole.valueOf(null);
        });
    }

    @Test
    @DisplayName("Should be case-sensitive in valueOf")
    void testCaseSensitiveValueOf() {
        assertThrows(IllegalArgumentException.class, () -> {
            UserRole.valueOf("customer"); // lowercase should fail
        });
    }

    @Test
    @DisplayName("Should maintain enum order")
    void testEnumOrder() {
        UserRole[] roles = UserRole.values();
        
        assertEquals(UserRole.CUSTOMER, roles[0]);
        assertEquals(UserRole.STAFF, roles[1]);
        assertEquals(UserRole.MANAGER, roles[2]);
    }

    @Test
    @DisplayName("Should support ordinal values")
    void testEnumOrdinals() {
        assertEquals(0, UserRole.CUSTOMER.ordinal());
        assertEquals(1, UserRole.STAFF.ordinal());
        assertEquals(2, UserRole.MANAGER.ordinal());
    }

    @Test
    @DisplayName("Should support equality comparison")
    void testEnumEquality() {
        UserRole role1 = UserRole.CUSTOMER;
        UserRole role2 = UserRole.valueOf("CUSTOMER");
        
        assertEquals(role1, role2);
        assertSame(role1, role2, "Enum instances should be the same");
    }

    @Test
    @DisplayName("Should support inequality comparison")
    void testEnumInequality() {
        UserRole customer = UserRole.CUSTOMER;
        UserRole staff = UserRole.STAFF;
        
        assertNotEquals(customer, staff);
    }

    @Test
    @DisplayName("Should work in switch statements")
    void testEnumInSwitch() {
        String description = getRoleDescription(UserRole.CUSTOMER);
        
        assertEquals("Customer role", description);
    }

    @ParameterizedTest
    @EnumSource(UserRole.class)
    @DisplayName("Should handle all roles in switch statement")
    void testAllRolesInSwitch(UserRole role) {
        String description = getRoleDescription(role);
        
        assertNotNull(description);
        assertFalse(description.isEmpty());
    }

    // Helper method to test switch compatibility
    private String getRoleDescription(UserRole role) {
        return switch (role) {
            case CUSTOMER -> "Customer role";
            case STAFF -> "Staff role";
            case MANAGER -> "Manager role";
        };
    }
}

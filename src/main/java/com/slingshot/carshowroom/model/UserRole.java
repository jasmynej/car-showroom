package com.slingshot.carshowroom.model;

/**
 * Enum representing the different user roles in the car showroom system.
 * 
 * @author Car Showroom Team
 * @version 1.0
 * @since 1.0
 */
public enum UserRole {
    /**
     * Customer role - for users who browse and purchase vehicles
     */
    CUSTOMER,
    
    /**
     * Staff role - for showroom employees who assist customers
     */
    STAFF,
    
    /**
     * Manager role - for users with administrative privileges
     */
    MANAGER
}

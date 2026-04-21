package com.slingshot.carshowroom.model;

/**
 * Enumeration representing the different user roles in the car showroom system.
 * 
 * <p>Roles define the access level and permissions for users:</p>
 * <ul>
 *   <li>CUSTOMER - End users who can browse and purchase vehicles</li>
 *   <li>STAFF - Employees who can manage inventory and customer interactions</li>
 *   <li>MANAGER - Administrative users with full system access</li>
 * </ul>
 */
public enum UserRole {
    /**
     * Customer role - standard user with basic access permissions
     */
    CUSTOMER,
    
    /**
     * Staff role - employee with elevated permissions for daily operations
     */
    STAFF,
    
    /**
     * Manager role - administrative user with full system access
     */
    MANAGER
}

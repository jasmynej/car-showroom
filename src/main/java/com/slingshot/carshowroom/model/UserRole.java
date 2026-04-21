package com.slingshot.carshowroom.model;

/**
 * Enum representing the different user roles in the car showroom system.
 * 
 * <p>Role Hierarchy:
 * <ul>
 *   <li>CUSTOMER - Can view cars, schedule test drives, make purchases</li>
 *   <li>STAFF - Can manage inventory, schedule services</li>
 *   <li>MANAGER - Can generate reports, full system access</li>
 * </ul>
 */
public enum UserRole {
    /**
     * Customer role - basic access for browsing and purchasing
     */
    CUSTOMER,
    
    /**
     * Staff role - operational access for inventory and service management
     */
    STAFF,
    
    /**
     * Manager role - administrative access with full system privileges
     */
    MANAGER
}

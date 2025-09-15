package com.enterprise.airbnb.model;

/**
 * Enumeration for user roles in the Airbnb Clone platform
 */
public enum UserRole {
    GUEST("Guest"),
    HOST("Host"),
    SUPER_HOST("Super Host"),
    ADMIN("Administrator");
    
    private final String displayName;
    
    UserRole(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public boolean isHost() {
        return this == HOST || this == SUPER_HOST;
    }
    
    public boolean isGuest() {
        return this == GUEST;
    }
    
    public boolean isAdmin() {
        return this == ADMIN;
    }
}


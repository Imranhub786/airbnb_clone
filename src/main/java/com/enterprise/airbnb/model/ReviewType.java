package com.enterprise.airbnb.model;

/**
 * Enumeration for review types
 */
public enum ReviewType {
    PROPERTY("Property"),
    GUEST("Guest"),
    GUEST_TO_HOST("Guest to Host"),
    GUEST_TO_PROPERTY("Guest to Property"),
    HOST_TO_GUEST("Host to Guest");
    
    private final String displayName;
    
    ReviewType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public boolean isGuestToHost() {
        return this == GUEST_TO_HOST;
    }
    
    public boolean isGuestToProperty() {
        return this == GUEST_TO_PROPERTY;
    }
    
    public boolean isHostToGuest() {
        return this == HOST_TO_GUEST;
    }
}


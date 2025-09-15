package com.enterprise.airbnb.model;

/**
 * Enumeration for property availability status
 */
public enum AvailabilityStatus {
    AVAILABLE("Available"),
    BOOKED("Booked"),
    BLOCKED("Blocked"),
    MAINTENANCE("Maintenance"),
    UNAVAILABLE("Unavailable");
    
    private final String displayName;
    
    AvailabilityStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public boolean isAvailable() {
        return this == AVAILABLE;
    }
    
    public boolean isBooked() {
        return this == BOOKED;
    }
    
    public boolean isBlocked() {
        return this == BLOCKED || this == MAINTENANCE || this == UNAVAILABLE;
    }
}





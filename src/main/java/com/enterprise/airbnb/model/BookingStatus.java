package com.enterprise.airbnb.model;

/**
 * Enumeration for booking status
 */
public enum BookingStatus {
    PENDING("Pending"),
    CONFIRMED("Confirmed"),
    CHECKED_IN("Checked In"),
    CHECKED_OUT("Checked Out"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled"),
    NO_SHOW("No Show"),
    REFUNDED("Refunded");
    
    private final String displayName;
    
    BookingStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public boolean isActive() {
        return this == CONFIRMED || this == CHECKED_IN;
    }
    
    public boolean isCompleted() {
        return this == COMPLETED;
    }
    
    public boolean isCancelled() {
        return this == CANCELLED || this == NO_SHOW;
    }
    
    public boolean canBeCancelled() {
        return this == PENDING || this == CONFIRMED;
    }
    
    public boolean isRefundable() {
        return this == CANCELLED || this == NO_SHOW || this == REFUNDED;
    }
}



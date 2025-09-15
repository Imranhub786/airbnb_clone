package com.enterprise.airbnb.model;

/**
 * Enumeration for property status
 */
public enum PropertyStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    PENDING_APPROVAL("Pending Approval"),
    SUSPENDED("Suspended"),
    DRAFT("Draft"),
    MAINTENANCE("Under Maintenance"),
    DELETED("Deleted");
    
    private final String displayName;
    
    PropertyStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public boolean isActive() {
        return this == ACTIVE;
    }
    
    public boolean isBookable() {
        return this == ACTIVE;
    }
    
    public boolean isVisible() {
        return this == ACTIVE || this == MAINTENANCE;
    }
    
    public boolean isPending() {
        return this == PENDING_APPROVAL || this == DRAFT;
    }
}



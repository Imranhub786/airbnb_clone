package com.enterprise.airbnb.model;

/**
 * Enumeration for review status
 */
public enum ReviewStatus {
    PENDING("Pending"),
    APPROVED("Approved"),
    PUBLISHED("Published"),
    REJECTED("Rejected"),
    REPORTED("Reported"),
    HIDDEN("Hidden"),
    DELETED("Deleted");
    
    private final String displayName;
    
    ReviewStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public boolean isPublished() {
        return this == PUBLISHED;
    }
    
    public boolean isPending() {
        return this == PENDING;
    }
    
    public boolean isRejected() {
        return this == REJECTED;
    }
    
    public boolean isVisible() {
        return this == PUBLISHED;
    }
    
    public boolean isHidden() {
        return this == HIDDEN || this == DELETED;
    }
}


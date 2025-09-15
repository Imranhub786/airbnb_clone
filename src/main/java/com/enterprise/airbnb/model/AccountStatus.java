package com.enterprise.airbnb.model;

/**
 * Enumeration for user account status
 */
public enum AccountStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    SUSPENDED("Suspended"),
    PENDING_VERIFICATION("Pending Verification"),
    BANNED("Banned");
    
    private final String displayName;
    
    AccountStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public boolean isActive() {
        return this == ACTIVE;
    }
    
    public boolean canLogin() {
        return this == ACTIVE || this == PENDING_VERIFICATION;
    }
    
    public boolean isRestricted() {
        return this == SUSPENDED || this == BANNED;
    }
}



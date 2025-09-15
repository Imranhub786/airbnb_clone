package com.enterprise.airbnb.model;

/**
 * Enumeration for payment status
 */
public enum PaymentStatus {
    PENDING("Pending"),
    PROCESSING("Processing"),
    COMPLETED("Completed"),
    FAILED("Failed"),
    CANCELLED("Cancelled"),
    REFUNDED("Refunded"),
    PARTIALLY_REFUNDED("Partially Refunded"),
    DISPUTED("Disputed"),
    CHARGEBACK("Chargeback");
    
    private final String displayName;
    
    PaymentStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public boolean isSuccessful() {
        return this == COMPLETED;
    }
    
    public boolean isPending() {
        return this == PENDING || this == PROCESSING;
    }
    
    public boolean isFailed() {
        return this == FAILED || this == CANCELLED;
    }
    
    public boolean isRefunded() {
        return this == REFUNDED || this == PARTIALLY_REFUNDED;
    }
    
    public boolean isDisputed() {
        return this == DISPUTED || this == CHARGEBACK;
    }
}



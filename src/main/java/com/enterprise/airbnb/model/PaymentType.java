package com.enterprise.airbnb.model;

/**
 * Enumeration for payment types
 */
public enum PaymentType {
    BOOKING("Booking"),
    BOOKING_PAYMENT("Booking Payment"),
    SECURITY_DEPOSIT("Security Deposit"),
    CLEANING_FEE("Cleaning Fee"),
    SERVICE_FEE("Service Fee"),
    TAXES("Taxes"),
    REFUND("Refund"),
    PARTIAL_REFUND("Partial Refund"),
    HOST_PAYOUT("Host Payout"),
    PLATFORM_FEE("Platform Fee"),
    PENALTY("Penalty"),
    ADJUSTMENT("Adjustment");
    
    private final String displayName;
    
    PaymentType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public boolean isBookingRelated() {
        return this == BOOKING_PAYMENT || this == SECURITY_DEPOSIT || 
               this == CLEANING_FEE || this == SERVICE_FEE || this == TAXES;
    }
    
    public boolean isRefund() {
        return this == REFUND || this == PARTIAL_REFUND;
    }
    
    public boolean isHostPayout() {
        return this == HOST_PAYOUT;
    }
    
    public boolean isPlatformFee() {
        return this == PLATFORM_FEE;
    }
    
    public boolean isPenalty() {
        return this == PENALTY;
    }
}


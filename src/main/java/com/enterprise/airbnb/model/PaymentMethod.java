package com.enterprise.airbnb.model;

/**
 * Enumeration for payment methods
 */
public enum PaymentMethod {
    PAYPAL("PayPal"),
    PAYPAL_CREDIT("PayPal Credit"),
    PAYPAL_DEBIT("PayPal Debit"),
    CREDIT_CARD("Credit Card"),
    DEBIT_CARD("Debit Card"),
    BANK_TRANSFER("Bank Transfer"),
    WALLET("Digital Wallet"),
    CASH("Cash"),
    CHECK("Check"),
    OTHER("Other");
    
    private final String displayName;
    
    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public boolean isPayPal() {
        return this == PAYPAL || this == PAYPAL_CREDIT || this == PAYPAL_DEBIT;
    }
    
    public boolean isCard() {
        return this == CREDIT_CARD || this == DEBIT_CARD;
    }
    
    public boolean isDigital() {
        return this == PAYPAL || this == PAYPAL_CREDIT || this == PAYPAL_DEBIT || 
               this == CREDIT_CARD || this == DEBIT_CARD || this == WALLET;
    }
    
    public boolean isTraditional() {
        return this == BANK_TRANSFER || this == CASH || this == CHECK;
    }
}



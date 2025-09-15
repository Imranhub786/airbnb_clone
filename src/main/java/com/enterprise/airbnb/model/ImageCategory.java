package com.enterprise.airbnb.model;

/**
 * Enumeration for image categories
 */
public enum ImageCategory {
    PROPERTY_MAIN("Property Main Image"),
    PROPERTY_INTERIOR("Property Interior"),
    PROPERTY_EXTERIOR("Property Exterior"),
    PROPERTY_AMENITIES("Property Amenities"),
    PROPERTY_NEIGHBORHOOD("Property Neighborhood"),
    PROPERTY("Property Image"),
    THUMBNAIL("Thumbnail Image"),
    USER_PROFILE("User Profile"),
    USER_VERIFICATION("User Verification"),
    BOOKING_DOCUMENT("Booking Document"),
    REVIEW_IMAGE("Review Image"),
    SUPPORT_ATTACHMENT("Support Attachment"),
    OTHER("Other");
    
    private final String displayName;
    
    ImageCategory(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public boolean isPropertyImage() {
        return this == PROPERTY_MAIN || this == PROPERTY_INTERIOR || 
               this == PROPERTY_EXTERIOR || this == PROPERTY_AMENITIES || 
               this == PROPERTY_NEIGHBORHOOD;
    }
    
    public boolean isUserImage() {
        return this == USER_PROFILE || this == USER_VERIFICATION;
    }
    
    public boolean isBookingRelated() {
        return this == BOOKING_DOCUMENT || this == REVIEW_IMAGE;
    }
    
    public boolean isSupportRelated() {
        return this == SUPPORT_ATTACHMENT;
    }
}



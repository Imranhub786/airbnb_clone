package com.enterprise.airbnb.model;

/**
 * Enumeration for different types of properties
 */
public enum PropertyType {
    APARTMENT("Apartment"),
    HOUSE("House"),
    CONDO("Condominium"),
    TOWNHOUSE("Townhouse"),
    VILLA("Villa"),
    STUDIO("Studio"),
    LOFT("Loft"),
    PENTHOUSE("Penthouse"),
    CABIN("Cabin"),
    COTTAGE("Cottage"),
    CASTLE("Castle"),
    TREEHOUSE("Treehouse"),
    BOAT("Boat"),
    RV("RV/Camper"),
    TENT("Tent"),
    YURT("Yurt"),
    BARN("Barn"),
    FARM("Farm"),
    WINERY("Winery"),
    BEACH_HOUSE("Beach House"),
    MOUNTAIN_HOUSE("Mountain House"),
    LAKE_HOUSE("Lake House"),
    SKI_CHALET("Ski Chalet"),
    BED_AND_BREAKFAST("Bed & Breakfast"),
    HOTEL("Hotel"),
    HOSTEL("Hostel"),
    GUESTHOUSE("Guesthouse"),
    GUEST_SUITE("Guest Suite"),
    PRIVATE_ROOM("Private Room"),
    SHARED_ROOM("Shared Room"),
    OTHER("Other");
    
    private final String displayName;
    
    PropertyType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public boolean isAccommodation() {
        return this == HOTEL || this == HOSTEL || this == BED_AND_BREAKFAST;
    }
    
    public boolean isUnique() {
        return this == TREEHOUSE || this == BOAT || this == CASTLE || this == YURT;
    }
    
    public boolean isOutdoor() {
        return this == TENT || this == RV || this == CABIN;
    }
}



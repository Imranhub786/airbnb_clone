package com.enterprise.airbnb.model;

/**
 * Enumeration for property amenities
 */
public enum Amenity {
    // Basic Amenities
    WIFI("WiFi"),
    KITCHEN("Kitchen"),
    WASHER("Washer"),
    DRYER("Dryer"),
    AIR_CONDITIONING("Air Conditioning"),
    HEATING("Heating"),
    DEDICATED_WORKSPACE("Dedicated Workspace"),
    TV("TV"),
    HAIR_DRYER("Hair Dryer"),
    IRON("Iron"),
    HANGERS("Hangers"),
    SHAMPOO("Shampoo"),
    BODY_SOAP("Body Soap"),
    HOT_WATER("Hot Water"),
    
    // Bedroom & Laundry
    BED_LINENS("Bed Linens"),
    EXTRA_PILLOWS_AND_BLANKETS("Extra Pillows and Blankets"),
    ROOM_DARKENING_SHADES("Room-darkening Shades"),
    CLOTHES_DRYER("Clothes Dryer"),
    DRYER_IN_UNIT("Dryer - In Unit"),
    WASHER_IN_UNIT("Washer - In Unit"),
    
    // Entertainment
    WIFI_FAST("WiFi - Fast"),
    LAPTOP_FRIENDLY_WORKSPACE("Laptop Friendly Workspace"),
    TV_STANDARD("TV - Standard"),
    TV_HD("TV - HD"),
    TV_4K("TV - 4K"),
    SOUND_SYSTEM("Sound System"),
    GAME_CONSOLE("Game Console"),
    BOOKS_AND_READING_MATERIAL("Books and Reading Material"),
    
    // Family Features
    CHILDREN_BOOKS_AND_TOYS("Children's Books and Toys"),
    CHILDREN_DINNERWARE("Children's Dinnerware"),
    HIGH_CHAIR("High Chair"),
    BABY_BATH("Baby Bath"),
    BABY_MONITOR("Baby Monitor"),
    CRIB("Crib"),
    PACK_N_PLAY_TRAVEL_CRIB("Pack 'n Play/Travel Crib"),
    BABY_GATE("Baby Gate"),
    OUTLET_COVERS("Outlet Covers"),
    WINDOW_GUARDS("Window Guards"),
    
    // Heating & Cooling
    CENTRAL_AIR_CONDITIONING("Central Air Conditioning"),
    WINDOW_AIR_CONDITIONING("Window Air Conditioning"),
    SPACE_HEATER("Space Heater"),
    FIREPLACE("Fireplace"),
    INDOOR_FIREPLACE("Indoor Fireplace"),
    OUTDOOR_FIREPLACE("Outdoor Fireplace"),
    
    // Internet & Office
    WIFI_DEDICATED_WORKSPACE("WiFi - Dedicated Workspace"),
    WIFI_LAPTOP_FRIENDLY("WiFi - Laptop Friendly"),
    PRINTER("Printer"),
    DESK("Desk"),
    CHAIR("Chair"),
    MONITOR("Monitor"),
    
    // Kitchen & Dining
    KITCHEN_FULL("Kitchen - Full"),
    KITCHEN_BASIC("Kitchen - Basic"),
    REFRIGERATOR("Refrigerator"),
    MICROWAVE("Microwave"),
    DISHWASHER("Dishwasher"),
    DISHES_AND_SILVERWARE("Dishes and Silverware"),
    COOKING_BASICS("Cooking Basics"),
    OVEN("Oven"),
    STOVE("Stove"),
    COFFEE_MAKER("Coffee Maker"),
    TOASTER("Toaster"),
    WINE_GLASSES("Wine Glasses"),
    DINING_TABLE("Dining Table"),
    
    // Location Features
    PRIVATE_ENTRANCE("Private Entrance"),
    PRIVATE_LIVING_ROOM("Private Living Room"),
    LOCK_ON_BEDROOM_DOOR("Lock on Bedroom Door"),
    LOCKBOX("Lockbox"),
    SMART_LOCK("Smart Lock"),
    KEYPAD("Keypad"),
    
    // Outdoor
    BALCONY("Balcony"),
    PATIO_OR_BALCONY("Patio or Balcony"),
    BACKYARD("Backyard"),
    BBQ_GRILL("BBQ Grill"),
    OUTDOOR_DINING_AREA("Outdoor Dining Area"),
    OUTDOOR_FURNITURE("Outdoor Furniture"),
    FIRE_PIT("Fire Pit"),
    POOL("Pool"),
    HOT_TUB("Hot Tub"),
    SAUNA("Sauna"),
    GARDEN_VIEW("Garden View"),
    POOL_VIEW("Pool View"),
    OCEAN_VIEW("Ocean View"),
    WATERFRONT("Waterfront"),
    BEACH_ACCESS("Beach Access"),
    LAKE_ACCESS("Lake Access"),
    HIKING_TRAILS("Hiking Trails"),
    SKI_IN_SKI_OUT("Ski-in/Ski-out"),
    
    // Parking & Facilities
    PARKING("Parking"),
    FREE_PARKING_ON_PREMISES("Free Parking on Premises"),
    FREE_STREET_PARKING("Free Street Parking"),
    PAID_PARKING_OFF_PREMISES("Paid Parking Off Premises"),
    PAID_PARKING_ON_PREMISES("Paid Parking On Premises"),
    ELEVATOR("Elevator"),
    GYM("Gym"),
    FITNESS_CENTER("Fitness Center"),
    
    // Safety Features
    SMOKE_ALARM("Smoke Alarm"),
    CARBON_MONOXIDE_ALARM("Carbon Monoxide Alarm"),
    FIRE_EXTINGUISHER("Fire Extinguisher"),
    FIRST_AID_KIT("First Aid Kit"),
    SECURITY_CAMERAS_ON_PROPERTY("Security Cameras on Property"),
    WEAPONS_ON_PROPERTY("Weapons on Property"),
    DANGEROUS_ANIMALS_ON_PROPERTY("Dangerous Animals on Property"),
    
    // Accessibility
    WHEELCHAIR_ACCESSIBLE("Wheelchair Accessible"),
    STEP_FREE_ACCESS("Step-free Access"),
    WIDE_ENTRANCE("Wide Entrance"),
    WIDE_HALLWAY_CLEARANCES("Wide Hallway Clearances"),
    ACCESSIBLE_BATHROOM("Accessible Bathroom"),
    ACCESSIBLE_BEDROOM("Accessible Bedroom"),
    ACCESSIBLE_PARKING("Accessible Parking"),
    ACCESSIBLE_PATH_TO_ENTRANCE("Accessible Path to Entrance"),
    
    // Pet Amenities
    PETS_ALLOWED("Pets Allowed"),
    PET_BOWL("Pet Bowl"),
    PET_BED("Pet Bed"),
    PET_FEEDER("Pet Feeder");
    
    private final String displayName;
    
    Amenity(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public boolean isBasic() {
        return this == WIFI || this == KITCHEN || this == WASHER || this == DRYER || 
               this == AIR_CONDITIONING || this == HEATING || this == TV;
    }
    
    public boolean isFamilyFriendly() {
        return this == CHILDREN_BOOKS_AND_TOYS || this == CHILDREN_DINNERWARE || 
               this == HIGH_CHAIR || this == BABY_BATH || this == BABY_MONITOR || 
               this == CRIB || this == PACK_N_PLAY_TRAVEL_CRIB || this == BABY_GATE;
    }
    
    public boolean isOutdoor() {
        return this == BALCONY || this == PATIO_OR_BALCONY || this == BACKYARD || 
               this == BBQ_GRILL || this == OUTDOOR_DINING_AREA || this == OUTDOOR_FURNITURE || 
               this == FIRE_PIT || this == POOL || this == HOT_TUB || this == SAUNA;
    }
    
    public boolean isAccessibility() {
        return this == WHEELCHAIR_ACCESSIBLE || this == STEP_FREE_ACCESS || 
               this == WIDE_ENTRANCE || this == WIDE_HALLWAY_CLEARANCES || 
               this == ACCESSIBLE_BATHROOM || this == ACCESSIBLE_BEDROOM;
    }
    
    public boolean isSafety() {
        return this == SMOKE_ALARM || this == CARBON_MONOXIDE_ALARM || 
               this == FIRE_EXTINGUISHER || this == FIRST_AID_KIT;
    }
}


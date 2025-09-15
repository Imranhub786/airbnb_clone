package com.enterprise.airbnb.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Embeddable Address entity for property locations
 */
@Embeddable
public class Address {
    
    @NotBlank(message = "Street address is required")
    @Size(max = 200)
    @Column(name = "street_address")
    private String streetAddress;
    
    @NotBlank(message = "City is required")
    @Size(max = 100)
    @Column(name = "city")
    private String city;
    
    @NotBlank(message = "State/Province is required")
    @Size(max = 100)
    @Column(name = "state_province")
    private String stateProvince;
    
    @NotBlank(message = "Postal code is required")
    @Size(max = 20)
    @Column(name = "postal_code")
    private String postalCode;
    
    @NotBlank(message = "Country is required")
    @Size(max = 100)
    @Column(name = "country")
    private String country;
    
    @Column(name = "latitude")
    private Double latitude;
    
    @Column(name = "longitude")
    private Double longitude;
    
    @Size(max = 200)
    @Column(name = "landmark")
    private String landmark;
    
    @Size(max = 100)
    @Column(name = "neighborhood")
    private String neighborhood;
    
    // Constructors
    public Address() {}
    
    public Address(String streetAddress, String city, String stateProvince, 
                  String postalCode, String country) {
        this.streetAddress = streetAddress;
        this.city = city;
        this.stateProvince = stateProvince;
        this.postalCode = postalCode;
        this.country = country;
    }
    
    // Getters and Setters
    public String getStreetAddress() { return streetAddress; }
    public void setStreetAddress(String streetAddress) { this.streetAddress = streetAddress; }
    
    // Missing method for compilation
    public void setStreet(String street) { this.streetAddress = street; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getStateProvince() { return stateProvince; }
    public void setStateProvince(String stateProvince) { this.stateProvince = stateProvince; }
    
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    
    public String getLandmark() { return landmark; }
    public void setLandmark(String landmark) { this.landmark = landmark; }
    
    public String getNeighborhood() { return neighborhood; }
    public void setNeighborhood(String neighborhood) { this.neighborhood = neighborhood; }
    
    // Utility methods
    public String getFullAddress() {
        return String.format("%s, %s, %s %s, %s", 
                streetAddress, city, stateProvince, postalCode, country);
    }
    
    public String getCityState() {
        return String.format("%s, %s", city, stateProvince);
    }
    
    public boolean hasCoordinates() {
        return latitude != null && longitude != null;
    }
}


package com.enterprise.airbnb.graphql.dto;

import com.enterprise.airbnb.model.PropertyType;
import com.enterprise.airbnb.model.Amenity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * GraphQL Input DTO for property search
 */
public class PropertySearchInput {
    
    private String city;
    private String stateProvince;
    private String country;
    private PropertyType propertyType;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer minGuests;
    private Integer minBedrooms;
    private Integer minBathrooms;
    private Boolean instantBook;
    private Boolean isPetFriendly;
    private Boolean isSmokingAllowed;
    private Boolean isEntirePlace;
    private BigDecimal minRating;
    private List<Amenity> amenities;
    private Double latitude;
    private Double longitude;
    private Double radius;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;

    // Constructors
    public PropertySearchInput() {}

    // Getters and Setters
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getStateProvince() { return stateProvince; }
    public void setStateProvince(String stateProvince) { this.stateProvince = stateProvince; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public PropertyType getPropertyType() { return propertyType; }
    public void setPropertyType(PropertyType propertyType) { this.propertyType = propertyType; }

    public BigDecimal getMinPrice() { return minPrice; }
    public void setMinPrice(BigDecimal minPrice) { this.minPrice = minPrice; }

    public BigDecimal getMaxPrice() { return maxPrice; }
    public void setMaxPrice(BigDecimal maxPrice) { this.maxPrice = maxPrice; }

    public Integer getMinGuests() { return minGuests; }
    public void setMinGuests(Integer minGuests) { this.minGuests = minGuests; }

    public Integer getMinBedrooms() { return minBedrooms; }
    public void setMinBedrooms(Integer minBedrooms) { this.minBedrooms = minBedrooms; }

    public Integer getMinBathrooms() { return minBathrooms; }
    public void setMinBathrooms(Integer minBathrooms) { this.minBathrooms = minBathrooms; }

    public Boolean getInstantBook() { return instantBook; }
    public void setInstantBook(Boolean instantBook) { this.instantBook = instantBook; }

    public Boolean getIsPetFriendly() { return isPetFriendly; }
    public void setIsPetFriendly(Boolean isPetFriendly) { this.isPetFriendly = isPetFriendly; }

    public Boolean getIsSmokingAllowed() { return isSmokingAllowed; }
    public void setIsSmokingAllowed(Boolean isSmokingAllowed) { this.isSmokingAllowed = isSmokingAllowed; }

    public Boolean getIsEntirePlace() { return isEntirePlace; }
    public void setIsEntirePlace(Boolean isEntirePlace) { this.isEntirePlace = isEntirePlace; }

    public BigDecimal getMinRating() { return minRating; }
    public void setMinRating(BigDecimal minRating) { this.minRating = minRating; }

    public List<Amenity> getAmenities() { return amenities; }
    public void setAmenities(List<Amenity> amenities) { this.amenities = amenities; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Double getRadius() { return radius; }
    public void setRadius(Double radius) { this.radius = radius; }

    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }

    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }

    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }

    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }

    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }

    public String getSortDirection() { return sortDirection; }
    public void setSortDirection(String sortDirection) { this.sortDirection = sortDirection; }
}




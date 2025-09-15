package com.enterprise.airbnb.graphql.dto;

import com.enterprise.airbnb.model.PropertyType;
import com.enterprise.airbnb.model.Amenity;
import com.enterprise.airbnb.model.Address;

import java.math.BigDecimal;
import java.util.List;

/**
 * GraphQL Input DTO for Property
 */
public class PropertyInput {
    
    private String title;
    private String description;
    private PropertyType propertyType;
    private Long hostId;
    private BigDecimal pricePerNight;
    private String currency;
    private Integer maxGuests;
    private Integer bedrooms;
    private Integer bathrooms;
    private Integer beds;
    private Address address;
    private List<Amenity> amenities;
    private List<String> images;
    private String mainImageUrl;
    private BigDecimal cleaningFee;
    private BigDecimal serviceFee;
    private BigDecimal securityDeposit;
    private Integer minimumNights;
    private Integer maximumNights;
    private String checkInTime;
    private String checkOutTime;
    private Boolean instantBook;
    private Boolean isPetFriendly;
    private Boolean isSmokingAllowed;
    private Boolean isEntirePlace;
    private String cancellationPolicy;
    private String houseRules;

    // Constructors
    public PropertyInput() {}

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public PropertyType getPropertyType() { return propertyType; }
    public void setPropertyType(PropertyType propertyType) { this.propertyType = propertyType; }

    public Long getHostId() { return hostId; }
    public void setHostId(Long hostId) { this.hostId = hostId; }

    public BigDecimal getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(BigDecimal pricePerNight) { this.pricePerNight = pricePerNight; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Integer getMaxGuests() { return maxGuests; }
    public void setMaxGuests(Integer maxGuests) { this.maxGuests = maxGuests; }

    public Integer getBedrooms() { return bedrooms; }
    public void setBedrooms(Integer bedrooms) { this.bedrooms = bedrooms; }

    public Integer getBathrooms() { return bathrooms; }
    public void setBathrooms(Integer bathrooms) { this.bathrooms = bathrooms; }

    public Integer getBeds() { return beds; }
    public void setBeds(Integer beds) { this.beds = beds; }

    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }

    public List<Amenity> getAmenities() { return amenities; }
    public void setAmenities(List<Amenity> amenities) { this.amenities = amenities; }

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }

    public String getMainImageUrl() { return mainImageUrl; }
    public void setMainImageUrl(String mainImageUrl) { this.mainImageUrl = mainImageUrl; }

    public BigDecimal getCleaningFee() { return cleaningFee; }
    public void setCleaningFee(BigDecimal cleaningFee) { this.cleaningFee = cleaningFee; }

    public BigDecimal getServiceFee() { return serviceFee; }
    public void setServiceFee(BigDecimal serviceFee) { this.serviceFee = serviceFee; }

    public BigDecimal getSecurityDeposit() { return securityDeposit; }
    public void setSecurityDeposit(BigDecimal securityDeposit) { this.securityDeposit = securityDeposit; }

    public Integer getMinimumNights() { return minimumNights; }
    public void setMinimumNights(Integer minimumNights) { this.minimumNights = minimumNights; }

    public Integer getMaximumNights() { return maximumNights; }
    public void setMaximumNights(Integer maximumNights) { this.maximumNights = maximumNights; }

    public String getCheckInTime() { return checkInTime; }
    public void setCheckInTime(String checkInTime) { this.checkInTime = checkInTime; }

    public String getCheckOutTime() { return checkOutTime; }
    public void setCheckOutTime(String checkOutTime) { this.checkOutTime = checkOutTime; }

    public Boolean getInstantBook() { return instantBook; }
    public void setInstantBook(Boolean instantBook) { this.instantBook = instantBook; }

    public Boolean getIsPetFriendly() { return isPetFriendly; }
    public void setIsPetFriendly(Boolean isPetFriendly) { this.isPetFriendly = isPetFriendly; }

    public Boolean getIsSmokingAllowed() { return isSmokingAllowed; }
    public void setIsSmokingAllowed(Boolean isSmokingAllowed) { this.isSmokingAllowed = isSmokingAllowed; }

    public Boolean getIsEntirePlace() { return isEntirePlace; }
    public void setIsEntirePlace(Boolean isEntirePlace) { this.isEntirePlace = isEntirePlace; }

    public String getCancellationPolicy() { return cancellationPolicy; }
    public void setCancellationPolicy(String cancellationPolicy) { this.cancellationPolicy = cancellationPolicy; }

    public String getHouseRules() { return houseRules; }
    public void setHouseRules(String houseRules) { this.houseRules = houseRules; }
}




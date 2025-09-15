package com.enterprise.airbnb.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Property entity representing vacation rental listings
 */
@Entity
@Table(name = "properties")
@Audited
@EntityListeners(AuditingEntityListener.class)
public class Property {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Title is required")
    @Size(max = 200)
    @Column(nullable = false)
    private String title;
    
    @NotBlank(message = "Description is required")
    @Size(max = 2000)
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;
    
    @NotNull(message = "Property type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "property_type", nullable = false)
    private PropertyType propertyType;
    
    @NotNull(message = "Host is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = false)
    private User host;
    
    @NotNull(message = "Price per night is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Price must have at most 10 integer digits and 2 decimal places")
    @Column(name = "price_per_night", nullable = false, precision = 12, scale = 2)
    private BigDecimal pricePerNight;
    
    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3)
    @Column(nullable = false, length = 3)
    private String currency = "USD";
    
    @NotNull(message = "Maximum guests is required")
    @Min(value = 1, message = "Maximum guests must be at least 1")
    @Max(value = 50, message = "Maximum guests cannot exceed 50")
    @Column(name = "max_guests", nullable = false)
    private Integer maxGuests;
    
    @NotNull(message = "Bedrooms count is required")
    @Min(value = 0, message = "Bedrooms count cannot be negative")
    @Column(nullable = false)
    private Integer bedrooms;
    
    @NotNull(message = "Bathrooms count is required")
    @Min(value = 0, message = "Bathrooms count cannot be negative")
    @Column(nullable = false)
    private Integer bathrooms;
    
    @Min(value = 0, message = "Beds count cannot be negative")
    @Column
    private Integer beds;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "property_status", nullable = false)
    private PropertyStatus status = PropertyStatus.ACTIVE;
    
    @Embedded
    private Address address;
    
    @ElementCollection(targetClass = Amenity.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "property_amenities", joinColumns = @JoinColumn(name = "property_id"))
    @Column(name = "amenity")
    private List<Amenity> amenities = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "property_images", joinColumns = @JoinColumn(name = "property_id"))
    @Column(name = "image_url")
    private List<String> images = new ArrayList<>();
    
    @Column(name = "main_image_url")
    private String mainImageUrl;
    
    @DecimalMin(value = "0.0", message = "Rating cannot be negative")
    @DecimalMax(value = "5.0", message = "Rating cannot exceed 5.0")
    @Column(precision = 3, scale = 2)
    private BigDecimal rating;
    
    @Column(name = "total_reviews")
    private Integer totalReviews = 0;
    
    @Column(name = "cleaning_fee", precision = 12, scale = 2)
    private BigDecimal cleaningFee;
    
    @Column(name = "service_fee", precision = 12, scale = 2)
    private BigDecimal serviceFee;
    
    @Column(name = "security_deposit", precision = 12, scale = 2)
    private BigDecimal securityDeposit;
    
    @Column(name = "minimum_nights")
    private Integer minimumNights = 1;
    
    @Column(name = "maximum_nights")
    private Integer maximumNights = 30;
    
    @Column(name = "check_in_time")
    private String checkInTime = "15:00";
    
    @Column(name = "check_out_time")
    private String checkOutTime = "11:00";
    
    @Column(name = "instant_book")
    private Boolean instantBook = false;
    
    @Column(name = "is_pet_friendly")
    private Boolean isPetFriendly = false;
    
    @Column(name = "is_smoking_allowed")
    private Boolean isSmokingAllowed = false;
    
    @Column(name = "is_entire_place")
    private Boolean isEntirePlace = true;
    
    @Column(name = "cancellation_policy", length = 50)
    private String cancellationPolicy = "MODERATE";
    
    @Column(name = "house_rules", columnDefinition = "TEXT")
    private String houseRules;
    
    @Column(name = "last_updated_availability")
    private LocalDateTime lastUpdatedAvailability;
    
    // Additional fields
    @Column(name = "latitude")
    private Double latitude;
    
    @Column(name = "longitude")
    private Double longitude;
    
    @Column(name = "square_footage")
    private Integer squareFootage;
    
    @Column(name = "minimum_stay")
    private Integer minimumStay = 1;
    
    @Column(name = "maximum_stay")
    private Integer maximumStay = 30;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Relationships
    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings = new ArrayList<>();
    
    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();
    
    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PropertyAvailability> availability = new ArrayList<>();
    
    // Constructors
    public Property() {}
    
    public Property(String title, String description, PropertyType propertyType, User host, 
                   BigDecimal pricePerNight, Integer maxGuests, Integer bedrooms, Integer bathrooms) {
        this.title = title;
        this.description = description;
        this.propertyType = propertyType;
        this.host = host;
        this.pricePerNight = pricePerNight;
        this.maxGuests = maxGuests;
        this.bedrooms = bedrooms;
        this.bathrooms = bathrooms;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public PropertyType getPropertyType() { return propertyType; }
    public void setPropertyType(PropertyType propertyType) { this.propertyType = propertyType; }
    
    public User getHost() { return host; }
    public void setHost(User host) { this.host = host; }
    
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
    
    public PropertyStatus getStatus() { return status; }
    public void setStatus(PropertyStatus status) { this.status = status; }
    
    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }
    
    public List<Amenity> getAmenities() { return amenities; }
    public void setAmenities(List<Amenity> amenities) { this.amenities = amenities; }
    
    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }
    
    public String getMainImageUrl() { return mainImageUrl; }
    public void setMainImageUrl(String mainImageUrl) { this.mainImageUrl = mainImageUrl; }
    
    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }
    
    public Integer getTotalReviews() { return totalReviews; }
    public void setTotalReviews(Integer totalReviews) { this.totalReviews = totalReviews; }
    
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
    
    public LocalDateTime getLastUpdatedAvailability() { return lastUpdatedAvailability; }
    public void setLastUpdatedAvailability(LocalDateTime lastUpdatedAvailability) { this.lastUpdatedAvailability = lastUpdatedAvailability; }
    
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    
    public Integer getSquareFootage() { return squareFootage; }
    public void setSquareFootage(Integer squareFootage) { this.squareFootage = squareFootage; }
    
    public Integer getMinimumStay() { return minimumStay; }
    public void setMinimumStay(Integer minimumStay) { this.minimumStay = minimumStay; }
    
    public Integer getMaximumStay() { return maximumStay; }
    public void setMaximumStay(Integer maximumStay) { this.maximumStay = maximumStay; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public List<Booking> getBookings() { return bookings; }
    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }
    
    public List<Review> getReviews() { return reviews; }
    public void setReviews(List<Review> reviews) { this.reviews = reviews; }
    
    public List<PropertyAvailability> getAvailability() { return availability; }
    public void setAvailability(List<PropertyAvailability> availability) { this.availability = availability; }
    
    // Utility methods
    public boolean isAvailable() {
        return status == PropertyStatus.ACTIVE;
    }
    
    public void addAmenity(Amenity amenity) {
        amenities.add(amenity);
    }
    
    public void removeAmenity(Amenity amenity) {
        amenities.remove(amenity);
    }
    
    public void addImage(String imageUrl) {
        images.add(imageUrl);
    }
    
    public void removeImage(String imageUrl) {
        images.remove(imageUrl);
    }
    
    
    public BigDecimal getTotalPrice(int nights) {
        BigDecimal basePrice = pricePerNight.multiply(BigDecimal.valueOf(nights));
        BigDecimal total = basePrice;
        
        if (cleaningFee != null) {
            total = total.add(cleaningFee);
        }
        
        if (serviceFee != null) {
            total = total.add(serviceFee);
        }
        
        return total;
    }
}


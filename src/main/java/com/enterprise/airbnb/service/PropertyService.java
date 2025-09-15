package com.enterprise.airbnb.service;

import com.enterprise.airbnb.model.Property;
import com.enterprise.airbnb.model.PropertyType;
import com.enterprise.airbnb.model.PropertyStatus;
import com.enterprise.airbnb.model.User;
import com.enterprise.airbnb.model.Amenity;
import com.enterprise.airbnb.repository.PropertyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for Property entity operations
 */
@Service
@Transactional
public class PropertyService {

    private static final Logger logger = LoggerFactory.getLogger(PropertyService.class);

    @Autowired
    private PropertyRepository propertyRepository;

    /**
     * Create a new property
     */
    @CacheEvict(value = {"properties", "propertySearch"}, allEntries = true)
    public Property createProperty(Property property) {
        logger.info("Creating new property: {}", property.getTitle());
        
        // Check if host already has a property with the same title
        if (propertyRepository.existsByHostAndTitle(property.getHost(), property.getTitle())) {
            throw new IllegalArgumentException("Property with this title already exists for the host");
        }

        // Set default values
        if (property.getStatus() == null) {
            property.setStatus(PropertyStatus.ACTIVE);
        }
        if (property.getCurrency() == null) {
            property.setCurrency("USD");
        }
        if (property.getMinimumNights() == null) {
            property.setMinimumNights(1);
        }
        if (property.getMaximumNights() == null) {
            property.setMaximumNights(30);
        }
        if (property.getCheckInTime() == null) {
            property.setCheckInTime("15:00");
        }
        if (property.getCheckOutTime() == null) {
            property.setCheckOutTime("11:00");
        }
        if (property.getInstantBook() == null) {
            property.setInstantBook(false);
        }
        if (property.getIsPetFriendly() == null) {
            property.setIsPetFriendly(false);
        }
        if (property.getIsSmokingAllowed() == null) {
            property.setIsSmokingAllowed(false);
        }
        if (property.getIsEntirePlace() == null) {
            property.setIsEntirePlace(true);
        }
        if (property.getCancellationPolicy() == null) {
            property.setCancellationPolicy("MODERATE");
        }
        if (property.getTotalReviews() == null) {
            property.setTotalReviews(0);
        }

        Property savedProperty = propertyRepository.save(property);
        logger.info("Property created successfully with ID: {}", savedProperty.getId());
        return savedProperty;
    }

    /**
     * Get property by ID
     */
    @Cacheable(value = "properties", key = "#id")
    public Optional<Property> getPropertyById(Long id) {
        logger.debug("Fetching property by ID: {}", id);
        return propertyRepository.findById(id);
    }

    /**
     * Update property
     */
    @CachePut(value = "properties", key = "#property.id")
    @CacheEvict(value = {"properties", "propertySearch"}, allEntries = true)
    public Property updateProperty(Property property) {
        logger.info("Updating property with ID: {}", property.getId());
        
        Property existingProperty = propertyRepository.findById(property.getId())
                .orElseThrow(() -> new IllegalArgumentException("Property not found with ID: " + property.getId()));

        // Check if title is being changed and if it already exists for the host
        if (!existingProperty.getTitle().equals(property.getTitle()) && 
            propertyRepository.existsByHostAndTitle(property.getHost(), property.getTitle())) {
            throw new IllegalArgumentException("Property with this title already exists for the host");
        }

        Property updatedProperty = propertyRepository.save(property);
        logger.info("Property updated successfully with ID: {}", updatedProperty.getId());
        return updatedProperty;
    }

    /**
     * Delete property
     */
    @CacheEvict(value = {"properties", "propertySearch"}, allEntries = true)
    public void deleteProperty(Long id) {
        logger.info("Deleting property with ID: {}", id);
        
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Property not found with ID: " + id));

        propertyRepository.delete(property);
        logger.info("Property deleted successfully with ID: {}", id);
    }

    /**
     * Update property status
     */
    @CacheEvict(value = {"properties", "propertySearch"}, key = "#id")
    public void updatePropertyStatus(Long id, PropertyStatus status) {
        logger.info("Updating property status for ID: {} to {}", id, status);
        
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Property not found with ID: " + id));

        property.setStatus(status);
        propertyRepository.save(property);
        
        logger.info("Property status updated successfully for ID: {}", id);
    }

    /**
     * Get properties by host
     */
    @Cacheable(value = "properties", key = "#host.id")
    public List<Property> getPropertiesByHost(User host) {
        logger.debug("Fetching properties by host: {}", host.getId());
        return propertyRepository.findByHost(host);
    }

    /**
     * Get properties by host with pagination
     */
    @Cacheable(value = "properties", key = "#host.id + '_page'")
    public Page<Property> getPropertiesByHost(User host, Pageable pageable) {
        logger.debug("Fetching properties by host with pagination: {}", host.getId());
        return propertyRepository.findByHost(host, pageable);
    }

    /**
     * Get properties by host ID
     */
    @Cacheable(value = "properties", key = "#hostId")
    public List<Property> getPropertiesByHostId(Long hostId) {
        logger.debug("Fetching properties by host ID: {}", hostId);
        return propertyRepository.findByHostId(hostId);
    }

    /**
     * Get properties by host ID with pagination
     */
    @Cacheable(value = "properties", key = "#hostId + '_page'")
    public Page<Property> getPropertiesByHostId(Long hostId, Pageable pageable) {
        logger.debug("Fetching properties by host ID with pagination: {}", hostId);
        return propertyRepository.findByHostId(hostId, pageable);
    }

    /**
     * Get properties by status
     */
    @Cacheable(value = "properties", key = "#status")
    public List<Property> getPropertiesByStatus(PropertyStatus status) {
        logger.debug("Fetching properties by status: {}", status);
        return propertyRepository.findByStatus(status);
    }

    /**
     * Get properties by status with pagination
     */
    @Cacheable(value = "properties", key = "#status + '_page'")
    public Page<Property> getPropertiesByStatus(PropertyStatus status, Pageable pageable) {
        logger.debug("Fetching properties by status with pagination: {}", status);
        return propertyRepository.findByStatus(status, pageable);
    }

    /**
     * Get properties by type
     */
    @Cacheable(value = "properties", key = "#propertyType")
    public List<Property> getPropertiesByType(PropertyType propertyType) {
        logger.debug("Fetching properties by type: {}", propertyType);
        return propertyRepository.findByPropertyType(propertyType);
    }

    /**
     * Get properties by type with pagination
     */
    @Cacheable(value = "properties", key = "#propertyType + '_page'")
    public Page<Property> getPropertiesByType(PropertyType propertyType, Pageable pageable) {
        logger.debug("Fetching properties by type with pagination: {}", propertyType);
        return propertyRepository.findByPropertyType(propertyType, pageable);
    }

    /**
     * Get properties by host and status
     */
    @Cacheable(value = "properties", key = "#host.id + '_' + #status")
    public List<Property> getPropertiesByHostAndStatus(User host, PropertyStatus status) {
        logger.debug("Fetching properties by host and status: {} - {}", host.getId(), status);
        return propertyRepository.findByHostAndStatus(host, status);
    }

    /**
     * Get properties by host and status with pagination
     */
    @Cacheable(value = "properties", key = "#host.id + '_' + #status + '_page'")
    public Page<Property> getPropertiesByHostAndStatus(User host, PropertyStatus status, Pageable pageable) {
        logger.debug("Fetching properties by host and status with pagination: {} - {}", host.getId(), status);
        return propertyRepository.findByHostAndStatus(host, status, pageable);
    }

    /**
     * Get properties by type and status
     */
    @Cacheable(value = "properties", key = "#propertyType + '_' + #status")
    public List<Property> getPropertiesByTypeAndStatus(PropertyType propertyType, PropertyStatus status) {
        logger.debug("Fetching properties by type and status: {} - {}", propertyType, status);
        return propertyRepository.findByPropertyTypeAndStatus(propertyType, status);
    }

    /**
     * Get properties by type and status with pagination
     */
    @Cacheable(value = "properties", key = "#propertyType + '_' + #status + '_page'")
    public Page<Property> getPropertiesByTypeAndStatus(PropertyType propertyType, PropertyStatus status, Pageable pageable) {
        logger.debug("Fetching properties by type and status with pagination: {} - {}", propertyType, status);
        return propertyRepository.findByPropertyTypeAndStatus(propertyType, status, pageable);
    }

    /**
     * Get properties by price range
     */
    @Cacheable(value = "propertySearch", key = "#minPrice + '_' + #maxPrice")
    public List<Property> getPropertiesByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        logger.debug("Fetching properties by price range: {} - {}", minPrice, maxPrice);
        return propertyRepository.findByPricePerNightBetween(minPrice, maxPrice);
    }

    /**
     * Get properties by price range with pagination
     */
    @Cacheable(value = "propertySearch", key = "#minPrice + '_' + #maxPrice + '_page'")
    public Page<Property> getPropertiesByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        logger.debug("Fetching properties by price range with pagination: {} - {}", minPrice, maxPrice);
        return propertyRepository.findByPricePerNightBetween(minPrice, maxPrice, pageable);
    }

    /**
     * Get properties by maximum guests
     */
    @Cacheable(value = "propertySearch", key = "#minGuests")
    public List<Property> getPropertiesByMaxGuests(Integer minGuests) {
        logger.debug("Fetching properties by max guests: {}", minGuests);
        return propertyRepository.findByMaxGuestsGreaterThanEqual(minGuests);
    }

    /**
     * Get properties by maximum guests with pagination
     */
    @Cacheable(value = "propertySearch", key = "#minGuests + '_page'")
    public Page<Property> getPropertiesByMaxGuests(Integer minGuests, Pageable pageable) {
        logger.debug("Fetching properties by max guests with pagination: {}", minGuests);
        return propertyRepository.findByMaxGuestsGreaterThanEqual(minGuests, pageable);
    }

    /**
     * Get properties by bedrooms
     */
    @Cacheable(value = "propertySearch", key = "#bedrooms")
    public List<Property> getPropertiesByBedrooms(Integer bedrooms) {
        logger.debug("Fetching properties by bedrooms: {}", bedrooms);
        return propertyRepository.findByBedrooms(bedrooms);
    }

    /**
     * Get properties by bedrooms with pagination
     */
    @Cacheable(value = "propertySearch", key = "#bedrooms + '_page'")
    public Page<Property> getPropertiesByBedrooms(Integer bedrooms, Pageable pageable) {
        logger.debug("Fetching properties by bedrooms with pagination: {}", bedrooms);
        return propertyRepository.findByBedrooms(bedrooms, pageable);
    }

    /**
     * Get properties by bathrooms
     */
    @Cacheable(value = "propertySearch", key = "#bathrooms")
    public List<Property> getPropertiesByBathrooms(Integer bathrooms) {
        logger.debug("Fetching properties by bathrooms: {}", bathrooms);
        return propertyRepository.findByBathrooms(bathrooms);
    }

    /**
     * Get properties by bathrooms with pagination
     */
    @Cacheable(value = "propertySearch", key = "#bathrooms + '_page'")
    public Page<Property> getPropertiesByBathrooms(Integer bathrooms, Pageable pageable) {
        logger.debug("Fetching properties by bathrooms with pagination: {}", bathrooms);
        return propertyRepository.findByBathrooms(bathrooms, pageable);
    }

    /**
     * Get properties by city
     */
    @Cacheable(value = "propertySearch", key = "#city")
    public List<Property> getPropertiesByCity(String city) {
        logger.debug("Fetching properties by city: {}", city);
        return propertyRepository.findByCity(city);
    }

    /**
     * Get properties by city with pagination
     */
    @Cacheable(value = "propertySearch", key = "#city + '_page'")
    public Page<Property> getPropertiesByCity(String city, Pageable pageable) {
        logger.debug("Fetching properties by city with pagination: {}", city);
        return propertyRepository.findByCity(city, pageable);
    }

    /**
     * Get properties by country
     */
    @Cacheable(value = "propertySearch", key = "#country")
    public List<Property> getPropertiesByCountry(String country) {
        logger.debug("Fetching properties by country: {}", country);
        return propertyRepository.findByCountry(country);
    }

    /**
     * Get properties by country with pagination
     */
    @Cacheable(value = "propertySearch", key = "#country + '_page'")
    public Page<Property> getPropertiesByCountry(String country, Pageable pageable) {
        logger.debug("Fetching properties by country with pagination: {}", country);
        return propertyRepository.findByCountry(country, pageable);
    }

    /**
     * Get properties by amenity
     */
    @Cacheable(value = "propertySearch", key = "#amenity")
    public List<Property> getPropertiesByAmenity(Amenity amenity) {
        logger.debug("Fetching properties by amenity: {}", amenity);
        return propertyRepository.findByAmenity(amenity.name());
    }

    /**
     * Get properties by amenity with pagination
     */
    @Cacheable(value = "propertySearch", key = "#amenity + '_page'")
    public Page<Property> getPropertiesByAmenity(Amenity amenity, Pageable pageable) {
        logger.debug("Fetching properties by amenity with pagination: {}", amenity);
        return propertyRepository.findByAmenity(amenity.name(), pageable);
    }

    /**
     * Get properties by multiple amenities
     */
    @Cacheable(value = "propertySearch", key = "#amenities.toString()")
    public List<Property> getPropertiesByAmenities(List<Amenity> amenities) {
        logger.debug("Fetching properties by amenities: {}", amenities);
        List<String> amenityNames = amenities.stream().map(Enum::name).toList();
        return propertyRepository.findByAmenities(amenityNames, (long) amenities.size());
    }

    /**
     * Get properties by multiple amenities with pagination
     */
    @Cacheable(value = "propertySearch", key = "#amenities.toString() + '_page'")
    public Page<Property> getPropertiesByAmenities(List<Amenity> amenities, Pageable pageable) {
        logger.debug("Fetching properties by amenities with pagination: {}", amenities);
        List<String> amenityNames = amenities.stream().map(Enum::name).toList();
        return propertyRepository.findByAmenities(amenityNames, (long) amenities.size(), pageable);
    }

    /**
     * Get properties within geographic radius
     */
    @Cacheable(value = "propertySearch", key = "#latitude + '_' + #longitude + '_' + #radius")
    public List<Property> getPropertiesByLocation(Double latitude, Double longitude, Double radius) {
        logger.debug("Fetching properties by location: lat={}, lng={}, radius={}", latitude, longitude, radius);
        return propertyRepository.findByLocationWithinRadius(latitude, longitude, radius);
    }

    /**
     * Get properties within geographic radius with pagination
     */
    @Cacheable(value = "propertySearch", key = "#latitude + '_' + #longitude + '_' + #radius + '_page'")
    public Page<Property> getPropertiesByLocation(Double latitude, Double longitude, Double radius, Pageable pageable) {
        logger.debug("Fetching properties by location with pagination: lat={}, lng={}, radius={}", latitude, longitude, radius);
        return propertyRepository.findByLocationWithinRadius(latitude, longitude, radius, pageable);
    }

    /**
     * Search properties by title
     */
    @Cacheable(value = "propertySearch", key = "#title")
    public List<Property> searchPropertiesByTitle(String title) {
        logger.debug("Searching properties by title: {}", title);
        return propertyRepository.findByTitleContainingIgnoreCase(title);
    }

    /**
     * Search properties by title with pagination
     */
    @Cacheable(value = "propertySearch", key = "#title + '_page'")
    public Page<Property> searchPropertiesByTitle(String title, Pageable pageable) {
        logger.debug("Searching properties by title with pagination: {}", title);
        return propertyRepository.findByTitleContainingIgnoreCase(title, pageable);
    }

    /**
     * Search properties by description
     */
    @Cacheable(value = "propertySearch", key = "#description")
    public List<Property> searchPropertiesByDescription(String description) {
        logger.debug("Searching properties by description: {}", description);
        return propertyRepository.findByDescriptionContainingIgnoreCase(description);
    }

    /**
     * Search properties by description with pagination
     */
    @Cacheable(value = "propertySearch", key = "#description + '_page'")
    public Page<Property> searchPropertiesByDescription(String description, Pageable pageable) {
        logger.debug("Searching properties by description with pagination: {}", description);
        return propertyRepository.findByDescriptionContainingIgnoreCase(description, pageable);
    }

    /**
     * Advanced property search
     */
    @Cacheable(value = "propertySearch", key = "#city + '_' + #stateProvince + '_' + #country + '_' + #propertyType + '_' + #minPrice + '_' + #maxPrice + '_' + #minGuests + '_' + #minBedrooms + '_' + #minBathrooms + '_' + #instantBook + '_' + #isPetFriendly + '_' + #isSmokingAllowed + '_' + #isEntirePlace + '_' + #minRating")
    public Page<Property> searchProperties(String city, String stateProvince, String country, 
                                         PropertyType propertyType, BigDecimal minPrice, BigDecimal maxPrice,
                                         Integer minGuests, Integer minBedrooms, Integer minBathrooms,
                                         Boolean instantBook, Boolean isPetFriendly, Boolean isSmokingAllowed,
                                         Boolean isEntirePlace, BigDecimal minRating, Pageable pageable) {
        logger.debug("Advanced property search with multiple criteria");
        return propertyRepository.searchProperties(city, stateProvince, country, propertyType, minPrice, maxPrice,
                minGuests, minBedrooms, minBathrooms, instantBook, isPetFriendly, isSmokingAllowed,
                isEntirePlace, minRating, pageable);
    }

    /**
     * Get active properties
     */
    @Cacheable(value = "properties", key = "'active'")
    public List<Property> getActiveProperties() {
        logger.debug("Fetching active properties");
        return propertyRepository.findActiveProperties();
    }

    /**
     * Get active properties with pagination
     */
    @Cacheable(value = "properties", key = "'active_page'")
    public Page<Property> getActiveProperties(Pageable pageable) {
        logger.debug("Fetching active properties with pagination");
        return propertyRepository.findActiveProperties(pageable);
    }

    /**
     * Get properties with bookings
     */
    @Cacheable(value = "properties", key = "'with_bookings'")
    public List<Property> getPropertiesWithBookings() {
        logger.debug("Fetching properties with bookings");
        return propertyRepository.findPropertiesWithBookings();
    }

    /**
     * Get properties without bookings
     */
    @Cacheable(value = "properties", key = "'without_bookings'")
    public List<Property> getPropertiesWithoutBookings() {
        logger.debug("Fetching properties without bookings");
        return propertyRepository.findPropertiesWithoutBookings();
    }

    /**
     * Get properties with reviews
     */
    @Cacheable(value = "properties", key = "'with_reviews'")
    public List<Property> getPropertiesWithReviews() {
        logger.debug("Fetching properties with reviews");
        return propertyRepository.findPropertiesWithReviews();
    }

    /**
     * Get properties without reviews
     */
    @Cacheable(value = "properties", key = "'without_reviews'")
    public List<Property> getPropertiesWithoutReviews() {
        logger.debug("Fetching properties without reviews");
        return propertyRepository.findPropertiesWithoutReviews();
    }

    /**
     * Update property availability
     */
    @CacheEvict(value = {"properties", "propertySearch"}, key = "#id")
    public void updatePropertyAvailability(Long id) {
        logger.info("Updating property availability for ID: {}", id);
        
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Property not found with ID: " + id));

        property.setLastUpdatedAvailability(LocalDateTime.now());
        propertyRepository.save(property);
        
        logger.info("Property availability updated successfully for ID: {}", id);
    }

    /**
     * Add amenity to property
     */
    @CacheEvict(value = {"properties", "propertySearch"}, key = "#id")
    public void addAmenityToProperty(Long id, Amenity amenity) {
        logger.info("Adding amenity {} to property ID: {}", amenity, id);
        
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Property not found with ID: " + id));

        if (!property.getAmenities().contains(amenity)) {
            property.addAmenity(amenity);
            propertyRepository.save(property);
            logger.info("Amenity added successfully to property ID: {}", id);
        } else {
            logger.warn("Amenity {} already exists for property ID: {}", amenity, id);
        }
    }

    /**
     * Remove amenity from property
     */
    @CacheEvict(value = {"properties", "propertySearch"}, key = "#id")
    public void removeAmenityFromProperty(Long id, Amenity amenity) {
        logger.info("Removing amenity {} from property ID: {}", amenity, id);
        
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Property not found with ID: " + id));

        if (property.getAmenities().contains(amenity)) {
            property.removeAmenity(amenity);
            propertyRepository.save(property);
            logger.info("Amenity removed successfully from property ID: {}", id);
        } else {
            logger.warn("Amenity {} does not exist for property ID: {}", amenity, id);
        }
    }

    /**
     * Add image to property
     */
    @CacheEvict(value = {"properties", "propertySearch"}, key = "#id")
    public void addImageToProperty(Long id, String imageUrl) {
        logger.info("Adding image to property ID: {}", id);
        
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Property not found with ID: " + id));

        property.addImage(imageUrl);
        
        // Set as main image if it's the first image
        if (property.getMainImageUrl() == null) {
            property.setMainImageUrl(imageUrl);
        }
        
        propertyRepository.save(property);
        logger.info("Image added successfully to property ID: {}", id);
    }

    /**
     * Remove image from property
     */
    @CacheEvict(value = {"properties", "propertySearch"}, key = "#id")
    public void removeImageFromProperty(Long id, String imageUrl) {
        logger.info("Removing image from property ID: {}", id);
        
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Property not found with ID: " + id));

        property.removeImage(imageUrl);
        
        // Update main image if it was removed
        if (imageUrl.equals(property.getMainImageUrl())) {
            if (!property.getImages().isEmpty()) {
                property.setMainImageUrl(property.getImages().get(0));
            } else {
                property.setMainImageUrl(null);
            }
        }
        
        propertyRepository.save(property);
        logger.info("Image removed successfully from property ID: {}", id);
    }

    /**
     * Set main image for property
     */
    @CacheEvict(value = {"properties", "propertySearch"}, key = "#id")
    public void setMainImageForProperty(Long id, String imageUrl) {
        logger.info("Setting main image for property ID: {}", id);
        
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Property not found with ID: " + id));

        if (property.getImages().contains(imageUrl)) {
            property.setMainImageUrl(imageUrl);
            propertyRepository.save(property);
            logger.info("Main image set successfully for property ID: {}", id);
        } else {
            throw new IllegalArgumentException("Image not found in property images: " + imageUrl);
        }
    }

    /**
     * Count properties by host
     */
    @Cacheable(value = "stats", key = "'property_count_host_' + #host.id")
    public long countPropertiesByHost(User host) {
        logger.debug("Counting properties by host: {}", host.getId());
        return propertyRepository.countByHost(host);
    }

    /**
     * Count properties by status
     */
    @Cacheable(value = "stats", key = "'property_count_status_' + #status")
    public long countPropertiesByStatus(PropertyStatus status) {
        logger.debug("Counting properties by status: {}", status);
        return propertyRepository.countByStatus(status);
    }

    /**
     * Count properties by type
     */
    @Cacheable(value = "stats", key = "'property_count_type_' + #propertyType")
    public long countPropertiesByType(PropertyType propertyType) {
        logger.debug("Counting properties by type: {}", propertyType);
        return propertyRepository.countByPropertyType(propertyType);
    }

    /**
     * Count properties by host and status
     */
    @Cacheable(value = "stats", key = "'property_count_host_' + #host.id + '_status_' + #status")
    public long countPropertiesByHostAndStatus(User host, PropertyStatus status) {
        logger.debug("Counting properties by host and status: {} - {}", host.getId(), status);
        return propertyRepository.countByHostAndStatus(host, status);
    }

    /**
     * Count properties by type and status
     */
    @Cacheable(value = "stats", key = "'property_count_type_' + #propertyType + '_status_' + #status")
    public long countPropertiesByTypeAndStatus(PropertyType propertyType, PropertyStatus status) {
        logger.debug("Counting properties by type and status: {} - {}", propertyType, status);
        return propertyRepository.countByPropertyTypeAndStatus(propertyType, status);
    }

    /**
     * Count properties by city
     */
    @Cacheable(value = "stats", key = "'property_count_city_' + #city")
    public long countPropertiesByCity(String city) {
        logger.debug("Counting properties by city: {}", city);
        return propertyRepository.countByCity(city);
    }

    /**
     * Count properties by country
     */
    @Cacheable(value = "stats", key = "'property_count_country_' + #country")
    public long countPropertiesByCountry(String country) {
        logger.debug("Counting properties by country: {}", country);
        return propertyRepository.countByCountry(country);
    }

    /**
     * Get all properties
     */
    public List<Property> getAllProperties() {
        logger.debug("Fetching all properties");
        return propertyRepository.findAll();
    }

    /**
     * Get all properties with pagination
     */
    public Page<Property> getAllProperties(Pageable pageable) {
        logger.debug("Fetching all properties with pagination");
        return propertyRepository.findAll(pageable);
    }

    /**
     * Get properties by host ID with pagination
     */
    public List<Property> getPropertiesByHost(Long hostId, Pageable pageable) {
        logger.debug("Fetching properties by host ID: {} with pagination", hostId);
        return getPropertiesByHostId(hostId, pageable).getContent();
    }

    /**
     * Search properties with input DTO
     */
    public Page<Property> searchProperties(com.enterprise.airbnb.graphql.dto.PropertySearchInput input, Pageable pageable) {
        logger.debug("Searching properties with input: {}", input);
        
        // This is a simplified implementation
        // In a real application, you would build a more sophisticated search query
        return propertyRepository.searchProperties(
            input.getCity(),
            input.getStateProvince(), 
            input.getCountry(),
            input.getPropertyType(),
            input.getMinPrice(),
            input.getMaxPrice(),
            input.getMinGuests(),
            input.getMinBedrooms(),
            input.getMinBathrooms(),
            input.getInstantBook(),
            input.getIsPetFriendly(),
            input.getIsSmokingAllowed(),
            input.getIsEntirePlace(),
            input.getMinRating(),
            pageable
        );
    }
}



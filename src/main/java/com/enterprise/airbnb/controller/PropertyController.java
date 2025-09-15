package com.enterprise.airbnb.controller;

import com.enterprise.airbnb.model.Property;
import com.enterprise.airbnb.model.PropertyType;
import com.enterprise.airbnb.model.PropertyStatus;
import com.enterprise.airbnb.model.Amenity;
import com.enterprise.airbnb.service.PropertyService;
import com.enterprise.airbnb.util.JwtRequestFilter;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Property operations
 */
@RestController
@RequestMapping("/api/properties")
@CrossOrigin(origins = "*")
public class PropertyController {

    private static final Logger logger = LoggerFactory.getLogger(PropertyController.class);

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    /**
     * Get all properties with pagination
     */
    @GetMapping
    public ResponseEntity<Page<Property>> getAllProperties(Pageable pageable) {
        logger.info("Fetching all properties with pagination: {}", pageable);
        Page<Property> properties = propertyService.getAllProperties(pageable);
        return ResponseEntity.ok(properties);
    }

    /**
     * Get property by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Property> getPropertyById(@PathVariable Long id) {
        logger.info("Fetching property by ID: {}", id);
        Optional<Property> property = propertyService.getPropertyById(id);
        return property.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create new property
     */
    @PostMapping
    @PreAuthorize("hasRole('HOST') or hasRole('SUPER_HOST') or hasRole('ADMIN')")
    public ResponseEntity<Property> createProperty(@Valid @RequestBody Property property) {
        logger.info("Creating new property: {}", property.getTitle());
        try {
            Property createdProperty = propertyService.createProperty(property);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProperty);
        } catch (Exception e) {
            logger.error("Error creating property: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Update property
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('HOST') or hasRole('SUPER_HOST') or hasRole('ADMIN')")
    public ResponseEntity<Property> updateProperty(@PathVariable Long id, @Valid @RequestBody Property property) {
        logger.info("Updating property with ID: {}", id);
        try {
            property.setId(id);
            Property updatedProperty = propertyService.updateProperty(property);
            return ResponseEntity.ok(updatedProperty);
        } catch (Exception e) {
            logger.error("Error updating property: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Delete property
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('HOST') or hasRole('SUPER_HOST') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long id) {
        logger.info("Deleting property with ID: {}", id);
        try {
            propertyService.deleteProperty(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting property: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Update property status
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('HOST') or hasRole('SUPER_HOST') or hasRole('ADMIN')")
    public ResponseEntity<Void> updatePropertyStatus(@PathVariable Long id, @RequestParam PropertyStatus status) {
        logger.info("Updating property status for ID: {} to {}", id, status);
        try {
            propertyService.updatePropertyStatus(id, status);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error updating property status: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get properties by host
     */
    @GetMapping("/host/{hostId}")
    public ResponseEntity<Page<Property>> getPropertiesByHost(@PathVariable Long hostId, Pageable pageable) {
        logger.info("Fetching properties by host ID: {}", hostId);
        Page<Property> properties = propertyService.getPropertiesByHostId(hostId, pageable);
        return ResponseEntity.ok(properties);
    }

    /**
     * Get properties by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<Property>> getPropertiesByStatus(@PathVariable PropertyStatus status, Pageable pageable) {
        logger.info("Fetching properties by status: {}", status);
        Page<Property> properties = propertyService.getPropertiesByStatus(status, pageable);
        return ResponseEntity.ok(properties);
    }

    /**
     * Get properties by type
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<Page<Property>> getPropertiesByType(@PathVariable PropertyType type, Pageable pageable) {
        logger.info("Fetching properties by type: {}", type);
        Page<Property> properties = propertyService.getPropertiesByType(type, pageable);
        return ResponseEntity.ok(properties);
    }

    /**
     * Search properties
     */
    @GetMapping("/search")
    public ResponseEntity<Page<Property>> searchProperties(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String stateProvince,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) PropertyType propertyType,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer minGuests,
            @RequestParam(required = false) Integer minBedrooms,
            @RequestParam(required = false) Integer minBathrooms,
            @RequestParam(required = false) Boolean instantBook,
            @RequestParam(required = false) Boolean isPetFriendly,
            @RequestParam(required = false) Boolean isSmokingAllowed,
            @RequestParam(required = false) Boolean isEntirePlace,
            @RequestParam(required = false) BigDecimal minRating,
            Pageable pageable) {
        
        logger.info("Searching properties with criteria");
        Page<Property> properties = propertyService.searchProperties(
                city, stateProvince, country, propertyType, minPrice, maxPrice,
                minGuests, minBedrooms, minBathrooms, instantBook, isPetFriendly,
                isSmokingAllowed, isEntirePlace, minRating, pageable);
        return ResponseEntity.ok(properties);
    }

    /**
     * Get properties by city
     */
    @GetMapping("/city/{city}")
    public ResponseEntity<Page<Property>> getPropertiesByCity(@PathVariable String city, Pageable pageable) {
        logger.info("Fetching properties by city: {}", city);
        Page<Property> properties = propertyService.getPropertiesByCity(city, pageable);
        return ResponseEntity.ok(properties);
    }

    /**
     * Get properties by country
     */
    @GetMapping("/country/{country}")
    public ResponseEntity<Page<Property>> getPropertiesByCountry(@PathVariable String country, Pageable pageable) {
        logger.info("Fetching properties by country: {}", country);
        Page<Property> properties = propertyService.getPropertiesByCountry(country, pageable);
        return ResponseEntity.ok(properties);
    }

    /**
     * Get properties by amenity
     */
    @GetMapping("/amenity/{amenity}")
    public ResponseEntity<Page<Property>> getPropertiesByAmenity(@PathVariable Amenity amenity, Pageable pageable) {
        logger.info("Fetching properties by amenity: {}", amenity);
        Page<Property> properties = propertyService.getPropertiesByAmenity(amenity, pageable);
        return ResponseEntity.ok(properties);
    }

    /**
     * Get properties by location (latitude, longitude, radius)
     */
    @GetMapping("/location")
    public ResponseEntity<Page<Property>> getPropertiesByLocation(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam Double radius,
            Pageable pageable) {
        
        logger.info("Fetching properties by location: lat={}, lng={}, radius={}", latitude, longitude, radius);
        Page<Property> properties = propertyService.getPropertiesByLocation(latitude, longitude, radius, pageable);
        return ResponseEntity.ok(properties);
    }

    /**
     * Search properties by title
     */
    @GetMapping("/search/title")
    public ResponseEntity<Page<Property>> searchPropertiesByTitle(@RequestParam String title, Pageable pageable) {
        logger.info("Searching properties by title: {}", title);
        Page<Property> properties = propertyService.searchPropertiesByTitle(title, pageable);
        return ResponseEntity.ok(properties);
    }

    /**
     * Search properties by description
     */
    @GetMapping("/search/description")
    public ResponseEntity<Page<Property>> searchPropertiesByDescription(@RequestParam String description, Pageable pageable) {
        logger.info("Searching properties by description: {}", description);
        Page<Property> properties = propertyService.searchPropertiesByDescription(description, pageable);
        return ResponseEntity.ok(properties);
    }

    /**
     * Add amenity to property
     */
    @PostMapping("/{id}/amenities")
    @PreAuthorize("hasRole('HOST') or hasRole('SUPER_HOST') or hasRole('ADMIN')")
    public ResponseEntity<Void> addAmenityToProperty(@PathVariable Long id, @RequestParam Amenity amenity) {
        logger.info("Adding amenity {} to property ID: {}", amenity, id);
        try {
            propertyService.addAmenityToProperty(id, amenity);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error adding amenity to property: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Remove amenity from property
     */
    @DeleteMapping("/{id}/amenities/{amenity}")
    @PreAuthorize("hasRole('HOST') or hasRole('SUPER_HOST') or hasRole('ADMIN')")
    public ResponseEntity<Void> removeAmenityFromProperty(@PathVariable Long id, @PathVariable Amenity amenity) {
        logger.info("Removing amenity {} from property ID: {}", amenity, id);
        try {
            propertyService.removeAmenityFromProperty(id, amenity);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error removing amenity from property: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Add image to property
     */
    @PostMapping("/{id}/images")
    @PreAuthorize("hasRole('HOST') or hasRole('SUPER_HOST') or hasRole('ADMIN')")
    public ResponseEntity<Void> addImageToProperty(@PathVariable Long id, @RequestParam String imageUrl) {
        logger.info("Adding image to property ID: {}", id);
        try {
            propertyService.addImageToProperty(id, imageUrl);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error adding image to property: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Remove image from property
     */
    @DeleteMapping("/{id}/images")
    @PreAuthorize("hasRole('HOST') or hasRole('SUPER_HOST') or hasRole('ADMIN')")
    public ResponseEntity<Void> removeImageFromProperty(@PathVariable Long id, @RequestParam String imageUrl) {
        logger.info("Removing image from property ID: {}", id);
        try {
            propertyService.removeImageFromProperty(id, imageUrl);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error removing image from property: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Set main image for property
     */
    @PatchMapping("/{id}/images/main")
    @PreAuthorize("hasRole('HOST') or hasRole('SUPER_HOST') or hasRole('ADMIN')")
    public ResponseEntity<Void> setMainImageForProperty(@PathVariable Long id, @RequestParam String imageUrl) {
        logger.info("Setting main image for property ID: {}", id);
        try {
            propertyService.setMainImageForProperty(id, imageUrl);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error setting main image for property: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get active properties
     */
    @GetMapping("/active")
    public ResponseEntity<Page<Property>> getActiveProperties(Pageable pageable) {
        logger.info("Fetching active properties");
        Page<Property> properties = propertyService.getActiveProperties(pageable);
        return ResponseEntity.ok(properties);
    }

    /**
     * Get properties with bookings
     */
    @GetMapping("/with-bookings")
    public ResponseEntity<List<Property>> getPropertiesWithBookings() {
        logger.info("Fetching properties with bookings");
        List<Property> properties = propertyService.getPropertiesWithBookings();
        return ResponseEntity.ok(properties);
    }

    /**
     * Get properties without bookings
     */
    @GetMapping("/without-bookings")
    public ResponseEntity<List<Property>> getPropertiesWithoutBookings() {
        logger.info("Fetching properties without bookings");
        List<Property> properties = propertyService.getPropertiesWithoutBookings();
        return ResponseEntity.ok(properties);
    }

    /**
     * Get properties with reviews
     */
    @GetMapping("/with-reviews")
    public ResponseEntity<List<Property>> getPropertiesWithReviews() {
        logger.info("Fetching properties with reviews");
        List<Property> properties = propertyService.getPropertiesWithReviews();
        return ResponseEntity.ok(properties);
    }

    /**
     * Get properties without reviews
     */
    @GetMapping("/without-reviews")
    public ResponseEntity<List<Property>> getPropertiesWithoutReviews() {
        logger.info("Fetching properties without reviews");
        List<Property> properties = propertyService.getPropertiesWithoutReviews();
        return ResponseEntity.ok(properties);
    }

    /**
     * Update property availability
     */
    @PatchMapping("/{id}/availability")
    @PreAuthorize("hasRole('HOST') or hasRole('SUPER_HOST') or hasRole('ADMIN')")
    public ResponseEntity<Void> updatePropertyAvailability(@PathVariable Long id) {
        logger.info("Updating property availability for ID: {}", id);
        try {
            propertyService.updatePropertyAvailability(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error updating property availability: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}

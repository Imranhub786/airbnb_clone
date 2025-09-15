package com.enterprise.airbnb.repository;

import com.enterprise.airbnb.model.Property;
import com.enterprise.airbnb.model.PropertyType;
import com.enterprise.airbnb.model.PropertyStatus;
import com.enterprise.airbnb.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Property entity operations
 */
@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    
    /**
     * Find properties by host
     */
    List<Property> findByHost(User host);
    
    /**
     * Find properties by host with pagination
     */
    Page<Property> findByHost(User host, Pageable pageable);
    
    /**
     * Find properties by host ID
     */
    List<Property> findByHostId(Long hostId);
    
    /**
     * Find properties by host ID with pagination
     */
    Page<Property> findByHostId(Long hostId, Pageable pageable);
    
    /**
     * Find properties by status
     */
    List<Property> findByStatus(PropertyStatus status);
    
    /**
     * Find properties by status with pagination
     */
    Page<Property> findByStatus(PropertyStatus status, Pageable pageable);
    
    /**
     * Find properties by type
     */
    List<Property> findByPropertyType(PropertyType propertyType);
    
    /**
     * Find properties by type with pagination
     */
    Page<Property> findByPropertyType(PropertyType propertyType, Pageable pageable);
    
    /**
     * Find properties by host and status
     */
    List<Property> findByHostAndStatus(User host, PropertyStatus status);
    
    /**
     * Find properties by host and status with pagination
     */
    Page<Property> findByHostAndStatus(User host, PropertyStatus status, Pageable pageable);
    
    /**
     * Find properties by type and status
     */
    List<Property> findByPropertyTypeAndStatus(PropertyType propertyType, PropertyStatus status);
    
    /**
     * Find properties by type and status with pagination
     */
    Page<Property> findByPropertyTypeAndStatus(PropertyType propertyType, PropertyStatus status, Pageable pageable);
    
    /**
     * Find properties by price range
     */
    List<Property> findByPricePerNightBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    /**
     * Find properties by price range with pagination
     */
    Page<Property> findByPricePerNightBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    
    /**
     * Find properties by maximum guests
     */
    List<Property> findByMaxGuestsGreaterThanEqual(Integer minGuests);
    
    /**
     * Find properties by maximum guests with pagination
     */
    Page<Property> findByMaxGuestsGreaterThanEqual(Integer minGuests, Pageable pageable);
    
    /**
     * Find properties by bedrooms count
     */
    List<Property> findByBedrooms(Integer bedrooms);
    
    /**
     * Find properties by bedrooms count with pagination
     */
    Page<Property> findByBedrooms(Integer bedrooms, Pageable pageable);
    
    /**
     * Find properties by bathrooms count
     */
    List<Property> findByBathrooms(Integer bathrooms);
    
    /**
     * Find properties by bathrooms count with pagination
     */
    Page<Property> findByBathrooms(Integer bathrooms, Pageable pageable);
    
    /**
     * Find properties by currency
     */
    List<Property> findByCurrency(String currency);
    
    /**
     * Find properties by currency with pagination
     */
    Page<Property> findByCurrency(String currency, Pageable pageable);
    
    /**
     * Find properties by instant book availability
     */
    List<Property> findByInstantBook(Boolean instantBook);
    
    /**
     * Find properties by instant book availability with pagination
     */
    Page<Property> findByInstantBook(Boolean instantBook, Pageable pageable);
    
    /**
     * Find properties by pet friendly status
     */
    List<Property> findByIsPetFriendly(Boolean isPetFriendly);
    
    /**
     * Find properties by pet friendly status with pagination
     */
    Page<Property> findByIsPetFriendly(Boolean isPetFriendly, Pageable pageable);
    
    /**
     * Find properties by smoking allowed status
     */
    List<Property> findByIsSmokingAllowed(Boolean isSmokingAllowed);
    
    /**
     * Find properties by smoking allowed status with pagination
     */
    Page<Property> findByIsSmokingAllowed(Boolean isSmokingAllowed, Pageable pageable);
    
    /**
     * Find properties by entire place status
     */
    List<Property> findByIsEntirePlace(Boolean isEntirePlace);
    
    /**
     * Find properties by entire place status with pagination
     */
    Page<Property> findByIsEntirePlace(Boolean isEntirePlace, Pageable pageable);
    
    /**
     * Find properties by rating range
     */
    List<Property> findByRatingBetween(BigDecimal minRating, BigDecimal maxRating);
    
    /**
     * Find properties by rating range with pagination
     */
    Page<Property> findByRatingBetween(BigDecimal minRating, BigDecimal maxRating, Pageable pageable);
    
    /**
     * Find properties by minimum nights
     */
    List<Property> findByMinimumNightsLessThanEqual(Integer maxMinimumNights);
    
    /**
     * Find properties by minimum nights with pagination
     */
    Page<Property> findByMinimumNightsLessThanEqual(Integer maxMinimumNights, Pageable pageable);
    
    /**
     * Find properties by maximum nights
     */
    List<Property> findByMaximumNightsGreaterThanEqual(Integer minMaximumNights);
    
    /**
     * Find properties by maximum nights with pagination
     */
    Page<Property> findByMaximumNightsGreaterThanEqual(Integer minMaximumNights, Pageable pageable);
    
    /**
     * Find properties by city
     */
    @Query("SELECT p FROM Property p WHERE p.address.city = :city")
    List<Property> findByCity(@Param("city") String city);
    
    /**
     * Find properties by city with pagination
     */
    @Query("SELECT p FROM Property p WHERE p.address.city = :city")
    Page<Property> findByCity(@Param("city") String city, Pageable pageable);
    
    /**
     * Find properties by state/province
     */
    @Query("SELECT p FROM Property p WHERE p.address.stateProvince = :stateProvince")
    List<Property> findByStateProvince(@Param("stateProvince") String stateProvince);
    
    /**
     * Find properties by state/province with pagination
     */
    @Query("SELECT p FROM Property p WHERE p.address.stateProvince = :stateProvince")
    Page<Property> findByStateProvince(@Param("stateProvince") String stateProvince, Pageable pageable);
    
    /**
     * Find properties by country
     */
    @Query("SELECT p FROM Property p WHERE p.address.country = :country")
    List<Property> findByCountry(@Param("country") String country);
    
    /**
     * Find properties by country with pagination
     */
    @Query("SELECT p FROM Property p WHERE p.address.country = :country")
    Page<Property> findByCountry(@Param("country") String country, Pageable pageable);
    
    /**
     * Find properties by postal code
     */
    @Query("SELECT p FROM Property p WHERE p.address.postalCode = :postalCode")
    List<Property> findByPostalCode(@Param("postalCode") String postalCode);
    
    /**
     * Find properties by postal code with pagination
     */
    @Query("SELECT p FROM Property p WHERE p.address.postalCode = :postalCode")
    Page<Property> findByPostalCode(@Param("postalCode") String postalCode, Pageable pageable);
    
    /**
     * Find properties by neighborhood
     */
    @Query("SELECT p FROM Property p WHERE p.address.neighborhood = :neighborhood")
    List<Property> findByNeighborhood(@Param("neighborhood") String neighborhood);
    
    /**
     * Find properties by neighborhood with pagination
     */
    @Query("SELECT p FROM Property p WHERE p.address.neighborhood = :neighborhood")
    Page<Property> findByNeighborhood(@Param("neighborhood") String neighborhood, Pageable pageable);
    
    /**
     * Find properties within a geographic radius
     */
    @Query("SELECT p FROM Property p WHERE " +
           "6371 * acos(cos(radians(:latitude)) * cos(radians(p.address.latitude)) * " +
           "cos(radians(p.address.longitude) - radians(:longitude)) + " +
           "sin(radians(:latitude)) * sin(radians(p.address.latitude))) <= :radius")
    List<Property> findByLocationWithinRadius(@Param("latitude") Double latitude, 
                                            @Param("longitude") Double longitude, 
                                            @Param("radius") Double radius);
    
    /**
     * Find properties within a geographic radius with pagination
     */
    @Query("SELECT p FROM Property p WHERE " +
           "6371 * acos(cos(radians(:latitude)) * cos(radians(p.address.latitude)) * " +
           "cos(radians(p.address.longitude) - radians(:longitude)) + " +
           "sin(radians(:latitude)) * sin(radians(p.address.latitude))) <= :radius")
    Page<Property> findByLocationWithinRadius(@Param("latitude") Double latitude, 
                                            @Param("longitude") Double longitude, 
                                            @Param("radius") Double radius, 
                                            Pageable pageable);
    
    /**
     * Find properties by title containing (case insensitive)
     */
    List<Property> findByTitleContainingIgnoreCase(String title);
    
    /**
     * Find properties by title containing with pagination
     */
    Page<Property> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    
    /**
     * Find properties by description containing (case insensitive)
     */
    List<Property> findByDescriptionContainingIgnoreCase(String description);
    
    /**
     * Find properties by description containing with pagination
     */
    Page<Property> findByDescriptionContainingIgnoreCase(String description, Pageable pageable);
    
    /**
     * Find properties created after a specific date
     */
    List<Property> findByCreatedAtAfter(LocalDateTime date);
    
    /**
     * Find properties created between dates
     */
    List<Property> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find properties updated after a specific date
     */
    List<Property> findByUpdatedAtAfter(LocalDateTime date);
    
    /**
     * Find properties with availability updated after a specific date
     */
    List<Property> findByLastUpdatedAvailabilityAfter(LocalDateTime date);
    
    /**
     * Find active properties
     */
    @Query("SELECT p FROM Property p WHERE p.status = 'ACTIVE'")
    List<Property> findActiveProperties();
    
    /**
     * Find active properties with pagination
     */
    @Query("SELECT p FROM Property p WHERE p.status = 'ACTIVE'")
    Page<Property> findActiveProperties(Pageable pageable);
    
    /**
     * Find properties with bookings
     */
    @Query("SELECT DISTINCT p FROM Property p JOIN p.bookings b")
    List<Property> findPropertiesWithBookings();
    
    /**
     * Find properties without bookings
     */
    @Query("SELECT p FROM Property p WHERE p.bookings IS EMPTY")
    List<Property> findPropertiesWithoutBookings();
    
    /**
     * Find properties with reviews
     */
    @Query("SELECT DISTINCT p FROM Property p JOIN p.reviews r")
    List<Property> findPropertiesWithReviews();
    
    /**
     * Find properties without reviews
     */
    @Query("SELECT p FROM Property p WHERE p.reviews IS EMPTY")
    List<Property> findPropertiesWithoutReviews();
    
    /**
     * Find properties by amenity
     */
    @Query("SELECT p FROM Property p JOIN p.amenities a WHERE a = :amenity")
    List<Property> findByAmenity(@Param("amenity") String amenity);
    
    /**
     * Find properties by amenity with pagination
     */
    @Query("SELECT p FROM Property p JOIN p.amenities a WHERE a = :amenity")
    Page<Property> findByAmenity(@Param("amenity") String amenity, Pageable pageable);
    
    /**
     * Find properties by multiple amenities
     */
    @Query("SELECT p FROM Property p JOIN p.amenities a WHERE a IN :amenities GROUP BY p HAVING COUNT(DISTINCT a) = :amenityCount")
    List<Property> findByAmenities(@Param("amenities") List<String> amenities, @Param("amenityCount") Long amenityCount);
    
    /**
     * Find properties by multiple amenities with pagination
     */
    @Query("SELECT p FROM Property p JOIN p.amenities a WHERE a IN :amenities GROUP BY p HAVING COUNT(DISTINCT a) = :amenityCount")
    Page<Property> findByAmenities(@Param("amenities") List<String> amenities, @Param("amenityCount") Long amenityCount, Pageable pageable);
    
    /**
     * Search properties by multiple criteria
     */
    @Query("SELECT p FROM Property p WHERE " +
           "(:city IS NULL OR p.address.city = :city) AND " +
           "(:stateProvince IS NULL OR p.address.stateProvince = :stateProvince) AND " +
           "(:country IS NULL OR p.address.country = :country) AND " +
           "(:propertyType IS NULL OR p.propertyType = :propertyType) AND " +
           "(:minPrice IS NULL OR p.pricePerNight >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.pricePerNight <= :maxPrice) AND " +
           "(:minGuests IS NULL OR p.maxGuests >= :minGuests) AND " +
           "(:minBedrooms IS NULL OR p.bedrooms >= :minBedrooms) AND " +
           "(:minBathrooms IS NULL OR p.bathrooms >= :minBathrooms) AND " +
           "(:instantBook IS NULL OR p.instantBook = :instantBook) AND " +
           "(:isPetFriendly IS NULL OR p.isPetFriendly = :isPetFriendly) AND " +
           "(:isSmokingAllowed IS NULL OR p.isSmokingAllowed = :isSmokingAllowed) AND " +
           "(:isEntirePlace IS NULL OR p.isEntirePlace = :isEntirePlace) AND " +
           "(:minRating IS NULL OR p.rating >= :minRating) AND " +
           "p.status = 'ACTIVE'")
    Page<Property> searchProperties(@Param("city") String city,
                                  @Param("stateProvince") String stateProvince,
                                  @Param("country") String country,
                                  @Param("propertyType") PropertyType propertyType,
                                  @Param("minPrice") BigDecimal minPrice,
                                  @Param("maxPrice") BigDecimal maxPrice,
                                  @Param("minGuests") Integer minGuests,
                                  @Param("minBedrooms") Integer minBedrooms,
                                  @Param("minBathrooms") Integer minBathrooms,
                                  @Param("instantBook") Boolean instantBook,
                                  @Param("isPetFriendly") Boolean isPetFriendly,
                                  @Param("isSmokingAllowed") Boolean isSmokingAllowed,
                                  @Param("isEntirePlace") Boolean isEntirePlace,
                                  @Param("minRating") BigDecimal minRating,
                                  Pageable pageable);
    
    /**
     * Count properties by host
     */
    long countByHost(User host);
    
    /**
     * Count properties by status
     */
    long countByStatus(PropertyStatus status);
    
    /**
     * Count properties by type
     */
    long countByPropertyType(PropertyType propertyType);
    
    /**
     * Count properties by host and status
     */
    long countByHostAndStatus(User host, PropertyStatus status);
    
    /**
     * Count properties by type and status
     */
    long countByPropertyTypeAndStatus(PropertyType propertyType, PropertyStatus status);
    
    /**
     * Count properties created after a specific date
     */
    long countByCreatedAtAfter(LocalDateTime date);
    
    /**
     * Count properties by city
     */
    @Query("SELECT COUNT(p) FROM Property p WHERE p.address.city = :city")
    long countByCity(@Param("city") String city);
    
    /**
     * Count properties by country
     */
    @Query("SELECT COUNT(p) FROM Property p WHERE p.address.country = :country")
    long countByCountry(@Param("country") String country);
    
    /**
     * Check if property exists by host and title
     */
    boolean existsByHostAndTitle(User host, String title);
}



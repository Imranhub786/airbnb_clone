package com.enterprise.airbnb.repository;

import com.enterprise.airbnb.model.PropertyAvailability;
import com.enterprise.airbnb.model.AvailabilityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for PropertyAvailability operations
 */
@Repository
public interface PropertyAvailabilityRepository extends JpaRepository<PropertyAvailability, Long> {

    // Find availability by property
    List<PropertyAvailability> findByPropertyId(Long propertyId);

    // Find availability by property and date range
    @Query("SELECT pa FROM PropertyAvailability pa WHERE pa.property.id = :propertyId AND pa.date BETWEEN :startDate AND :endDate")
    List<PropertyAvailability> findByPropertyIdAndDateRange(@Param("propertyId") Long propertyId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Find availability by status
    Page<PropertyAvailability> findByStatus(AvailabilityStatus status, Pageable pageable);

    // Find availability by date
    List<PropertyAvailability> findByDate(LocalDate date);

    // Find availability by date range
    @Query("SELECT pa FROM PropertyAvailability pa WHERE pa.date BETWEEN :startDate AND :endDate")
    Page<PropertyAvailability> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, Pageable pageable);

    // Check if property is available on specific dates
    @Query("SELECT COUNT(pa) = 0 FROM PropertyAvailability pa WHERE pa.property.id = :propertyId AND pa.date BETWEEN :startDate AND :endDate AND pa.status = 'UNAVAILABLE'")
    boolean isPropertyAvailable(@Param("propertyId") Long propertyId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Get available properties for date range
    @Query("SELECT DISTINCT pa.property FROM PropertyAvailability pa WHERE pa.date BETWEEN :startDate AND :endDate AND pa.status = 'AVAILABLE'")
    List<Object> getAvailableProperties(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Get occupancy rate for property
    @Query("SELECT COUNT(pa) * 100.0 / (SELECT COUNT(pa2) FROM PropertyAvailability pa2 WHERE pa2.property.id = :propertyId AND pa2.date BETWEEN :startDate AND :endDate) FROM PropertyAvailability pa WHERE pa.property.id = :propertyId AND pa.date BETWEEN :startDate AND :endDate AND pa.status = 'UNAVAILABLE'")
    Double getOccupancyRate(@Param("propertyId") Long propertyId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Get availability statistics
    @Query("SELECT pa.status, COUNT(pa) FROM PropertyAvailability pa GROUP BY pa.status")
    List<Object> getAvailabilityStatistics();

    // Find properties needing availability update
    @Query("SELECT DISTINCT pa.property FROM PropertyAvailability pa WHERE pa.date < :today")
    List<Object> findPropertiesNeedingAvailabilityUpdate(@Param("today") LocalDate today);

    // Clean up old availability records
    @Query("DELETE FROM PropertyAvailability pa WHERE pa.date < :cutoffDate")
    void cleanupOldAvailability(@Param("cutoffDate") LocalDate cutoffDate);
}





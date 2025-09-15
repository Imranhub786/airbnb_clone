package com.enterprise.airbnb.service;

import com.enterprise.airbnb.model.PropertyAvailability;
import com.enterprise.airbnb.model.AvailabilityStatus;
import com.enterprise.airbnb.repository.PropertyAvailabilityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service for PropertyAvailability operations
 */
@Service
@Transactional
public class PropertyAvailabilityService {

    private static final Logger logger = LoggerFactory.getLogger(PropertyAvailabilityService.class);

    @Autowired
    private PropertyAvailabilityRepository propertyAvailabilityRepository;

    /**
     * Get availability by property ID
     */
    @Cacheable(value = "availability", key = "#propertyId")
    public List<PropertyAvailability> getAvailabilityByProperty(Long propertyId) {
        logger.debug("Fetching availability for property ID: {}", propertyId);
        return propertyAvailabilityRepository.findByPropertyId(propertyId);
    }

    /**
     * Get availability by property ID and date range
     */
    @Cacheable(value = "availability", key = "#propertyId + '_' + #startDate + '_' + #endDate")
    public List<PropertyAvailability> getAvailabilityByPropertyAndDateRange(Long propertyId, LocalDate startDate, LocalDate endDate) {
        logger.debug("Fetching availability for property ID: {} from {} to {}", propertyId, startDate, endDate);
        return propertyAvailabilityRepository.findByPropertyIdAndDateRange(propertyId, startDate, endDate);
    }

    /**
     * Get availability by status
     */
    @Cacheable(value = "availability", key = "#status + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<PropertyAvailability> getAvailabilityByStatus(AvailabilityStatus status, Pageable pageable) {
        logger.debug("Fetching availability by status: {}", status);
        return propertyAvailabilityRepository.findByStatus(status, pageable);
    }

    /**
     * Get availability by date
     */
    @Cacheable(value = "availability", key = "#date")
    public List<PropertyAvailability> getAvailabilityByDate(LocalDate date) {
        logger.debug("Fetching availability for date: {}", date);
        return propertyAvailabilityRepository.findByDate(date);
    }

    /**
     * Check if property is available
     */
    @Cacheable(value = "availability", key = "'check_' + #propertyId + '_' + #startDate + '_' + #endDate")
    public boolean isPropertyAvailable(Long propertyId, LocalDate startDate, LocalDate endDate) {
        logger.debug("Checking availability for property ID: {} from {} to {}", propertyId, startDate, endDate);
        return propertyAvailabilityRepository.isPropertyAvailable(propertyId, startDate, endDate);
    }

    /**
     * Get available properties for date range
     */
    @Cacheable(value = "availability", key = "'available_' + #startDate + '_' + #endDate")
    public List<Object> getAvailableProperties(LocalDate startDate, LocalDate endDate) {
        logger.debug("Fetching available properties from {} to {}", startDate, endDate);
        return propertyAvailabilityRepository.getAvailableProperties(startDate, endDate);
    }

    /**
     * Get occupancy rate for property
     */
    @Cacheable(value = "availability", key = "'occupancy_' + #propertyId + '_' + #startDate + '_' + #endDate")
    public Double getOccupancyRate(Long propertyId, LocalDate startDate, LocalDate endDate) {
        logger.debug("Calculating occupancy rate for property ID: {} from {} to {}", propertyId, startDate, endDate);
        return propertyAvailabilityRepository.getOccupancyRate(propertyId, startDate, endDate);
    }

    /**
     * Get availability statistics
     */
    @Cacheable(value = "availability", key = "'statistics'")
    public List<Object> getAvailabilityStatistics() {
        logger.debug("Fetching availability statistics");
        return propertyAvailabilityRepository.getAvailabilityStatistics();
    }

    /**
     * Create availability record
     */
    @CacheEvict(value = "availability", allEntries = true)
    public PropertyAvailability createAvailability(PropertyAvailability availability) {
        logger.info("Creating availability record for property ID: {} on date: {}", 
            availability.getProperty().getId(), availability.getDate());
        return propertyAvailabilityRepository.save(availability);
    }

    /**
     * Update availability record
     */
    @CacheEvict(value = "availability", allEntries = true)
    public PropertyAvailability updateAvailability(PropertyAvailability availability) {
        logger.info("Updating availability record for property ID: {} on date: {}", 
            availability.getProperty().getId(), availability.getDate());
        return propertyAvailabilityRepository.save(availability);
    }

    /**
     * Delete availability record
     */
    @CacheEvict(value = "availability", allEntries = true)
    public void deleteAvailability(Long id) {
        logger.info("Deleting availability record with ID: {}", id);
        propertyAvailabilityRepository.deleteById(id);
    }

    /**
     * Get availability by ID
     */
    @Cacheable(value = "availability", key = "#id")
    public Optional<PropertyAvailability> getAvailabilityById(Long id) {
        logger.debug("Fetching availability by ID: {}", id);
        return propertyAvailabilityRepository.findById(id);
    }

    /**
     * Clean up old availability records
     */
    @CacheEvict(value = "availability", allEntries = true)
    public void cleanupOldAvailability(LocalDate cutoffDate) {
        logger.info("Cleaning up availability records before: {}", cutoffDate);
        propertyAvailabilityRepository.cleanupOldAvailability(cutoffDate);
    }

    /**
     * Find properties needing availability update
     */
    @Cacheable(value = "availability", key = "'needsUpdate_' + #today")
    public List<Object> findPropertiesNeedingAvailabilityUpdate(LocalDate today) {
        logger.debug("Finding properties needing availability update for date: {}", today);
        return propertyAvailabilityRepository.findPropertiesNeedingAvailabilityUpdate(today);
    }
}




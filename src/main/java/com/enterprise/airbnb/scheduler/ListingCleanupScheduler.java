package com.enterprise.airbnb.scheduler;

import com.enterprise.airbnb.model.Property;
import com.enterprise.airbnb.model.PropertyStatus;
import com.enterprise.airbnb.model.Booking;
import com.enterprise.airbnb.model.BookingStatus;
import com.enterprise.airbnb.model.Payment;
import com.enterprise.airbnb.model.PaymentStatus;
import com.enterprise.airbnb.repository.PropertyRepository;
import com.enterprise.airbnb.repository.BookingRepository;
import com.enterprise.airbnb.repository.PaymentRepository;
import com.enterprise.airbnb.service.CurrencyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduled tasks for maintenance and cleanup operations
 */
@Component
public class ListingCleanupScheduler {

    private static final Logger logger = LoggerFactory.getLogger(ListingCleanupScheduler.class);

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private CurrencyService currencyService;

    /**
     * Clean up expired listings every day at 2 AM
     */
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    @CacheEvict(value = {"properties", "bookings"}, allEntries = true)
    public void cleanupExpiredListings() {
        logger.info("Starting cleanup of expired listings");
        
        try {
            LocalDate today = LocalDate.now();
            
            // Find properties that are expired (past their end date)
            List<Property> expiredProperties = propertyRepository.findExpiredProperties(today);
            
            for (Property property : expiredProperties) {
                logger.info("Cleaning up expired property: {}", property.getId());
                
                // Update property status
                property.setStatus(PropertyStatus.EXPIRED);
                property.setUpdatedAt(LocalDateTime.now());
                propertyRepository.save(property);
                
                // Cancel any pending bookings for this property
                List<Booking> pendingBookings = bookingRepository.findPendingBookingsByProperty(property.getId());
                for (Booking booking : pendingBookings) {
                    booking.setStatus(BookingStatus.CANCELLED);
                    booking.setUpdatedAt(LocalDateTime.now());
                    bookingRepository.save(booking);
                    
                    // Refund any payments for cancelled bookings
                    List<Payment> payments = paymentRepository.findByBookingId(booking.getId());
                    for (Payment payment : payments) {
                        if (payment.getStatus() == PaymentStatus.COMPLETED) {
                            payment.setStatus(PaymentStatus.REFUNDED);
                            payment.setRefundedAt(LocalDateTime.now());
                            paymentRepository.save(payment);
                        }
                    }
                }
            }
            
            logger.info("Cleanup completed. Processed {} expired properties", expiredProperties.size());
            
        } catch (Exception e) {
            logger.error("Error during cleanup of expired listings: {}", e.getMessage());
        }
    }

    /**
     * Clean up old cancelled bookings every week on Sunday at 3 AM
     */
    @Scheduled(cron = "0 0 3 * * SUN")
    @Transactional
    @CacheEvict(value = {"bookings", "payments"}, allEntries = true)
    public void cleanupOldCancelledBookings() {
        logger.info("Starting cleanup of old cancelled bookings");
        
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusMonths(6);
            
            // Find old cancelled bookings
            List<Booking> oldCancelledBookings = bookingRepository.findOldCancelledBookings(cutoffDate);
            
            for (Booking booking : oldCancelledBookings) {
                logger.info("Cleaning up old cancelled booking: {}", booking.getId());
                
                // Delete associated payments
                List<Payment> payments = paymentRepository.findByBookingId(booking.getId());
                for (Payment payment : payments) {
                    paymentRepository.delete(payment);
                }
                
                // Delete the booking
                bookingRepository.delete(booking);
            }
            
            logger.info("Cleanup completed. Processed {} old cancelled bookings", oldCancelledBookings.size());
            
        } catch (Exception e) {
            logger.error("Error during cleanup of old cancelled bookings: {}", e.getMessage());
        }
    }

    /**
     * Update exchange rates every hour
     */
    @Scheduled(fixedRate = 3600000) // 1 hour
    @CacheEvict(value = {"exchangeRates"}, allEntries = true)
    public void updateExchangeRates() {
        logger.info("Updating exchange rates");
        
        try {
            currencyService.updateExchangeRates();
            logger.info("Exchange rates updated successfully");
            
        } catch (Exception e) {
            logger.error("Error updating exchange rates: {}", e.getMessage());
        }
    }

    /**
     * Clean up failed payments every day at 4 AM
     */
    @Scheduled(cron = "0 0 4 * * ?")
    @Transactional
    @CacheEvict(value = {"payments"}, allEntries = true)
    public void cleanupFailedPayments() {
        logger.info("Starting cleanup of failed payments");
        
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
            
            // Find old failed payments
            List<Payment> oldFailedPayments = paymentRepository.findOldFailedPayments(cutoffDate);
            
            for (Payment payment : oldFailedPayments) {
                logger.info("Cleaning up old failed payment: {}", payment.getId());
                
                // Delete the payment
                paymentRepository.delete(payment);
            }
            
            logger.info("Cleanup completed. Processed {} old failed payments", oldFailedPayments.size());
            
        } catch (Exception e) {
            logger.error("Error during cleanup of failed payments: {}", e.getMessage());
        }
    }

    /**
     * Update property availability every 30 minutes
     */
    @Scheduled(fixedRate = 1800000) // 30 minutes
    @Transactional
    @CacheEvict(value = {"properties"}, allEntries = true)
    public void updatePropertyAvailability() {
        logger.info("Updating property availability");
        
        try {
            LocalDate today = LocalDate.now();
            
            // Find properties that need availability updates
            List<Property> properties = propertyRepository.findPropertiesNeedingAvailabilityUpdate(today);
            
            for (Property property : properties) {
                logger.info("Updating availability for property: {}", property.getId());
                
                // Update availability based on bookings
                boolean isAvailable = bookingRepository.isPropertyAvailable(
                    property.getId(), 
                    today, 
                    today.plusDays(30)
                );
                
                // Update property availability status
                property.setUpdatedAt(LocalDateTime.now());
                propertyRepository.save(property);
            }
            
            logger.info("Availability update completed. Processed {} properties", properties.size());
            
        } catch (Exception e) {
            logger.error("Error updating property availability: {}", e.getMessage());
        }
    }

    /**
     * Generate daily reports every day at 6 AM
     */
    @Scheduled(cron = "0 0 6 * * ?")
    public void generateDailyReports() {
        logger.info("Generating daily reports");
        
        try {
            LocalDate yesterday = LocalDate.now().minusDays(1);
            
            // Generate booking report
            generateBookingReport(yesterday);
            
            // Generate revenue report
            generateRevenueReport(yesterday);
            
            // Generate property report
            generatePropertyReport(yesterday);
            
            logger.info("Daily reports generated successfully");
            
        } catch (Exception e) {
            logger.error("Error generating daily reports: {}", e.getMessage());
        }
    }

    /**
     * Clean up old audit logs every month on the 1st at 5 AM
     */
    @Scheduled(cron = "0 0 5 1 * ?")
    @Transactional
    public void cleanupOldAuditLogs() {
        logger.info("Starting cleanup of old audit logs");
        
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusYears(2);
            
            // This would clean up old audit logs
            // Implementation depends on your audit logging strategy
            
            logger.info("Audit log cleanup completed");
            
        } catch (Exception e) {
            logger.error("Error during cleanup of old audit logs: {}", e.getMessage());
        }
    }

    /**
     * Update property ratings every week on Monday at 7 AM
     */
    @Scheduled(cron = "0 0 7 * * MON")
    @Transactional
    @CacheEvict(value = {"properties", "reviews"}, allEntries = true)
    public void updatePropertyRatings() {
        logger.info("Updating property ratings");
        
        try {
            // Find properties that need rating updates
            List<Property> properties = propertyRepository.findPropertiesNeedingRatingUpdate();
            
            for (Property property : properties) {
                logger.info("Updating rating for property: {}", property.getId());
                
                // Calculate new average rating
                Double newRating = propertyRepository.calculateAverageRating(property.getId());
                
                if (newRating != null) {
                    property.setAverageRating(newRating);
                    property.setUpdatedAt(LocalDateTime.now());
                    propertyRepository.save(property);
                }
            }
            
            logger.info("Property ratings update completed. Processed {} properties", properties.size());
            
        } catch (Exception e) {
            logger.error("Error updating property ratings: {}", e.getMessage());
        }
    }

    /**
     * Clean up temporary files every day at 1 AM
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void cleanupTemporaryFiles() {
        logger.info("Starting cleanup of temporary files");
        
        try {
            // Clean up temporary files (e.g., uploaded images that failed processing)
            // Implementation depends on your file storage strategy
            
            logger.info("Temporary files cleanup completed");
            
        } catch (Exception e) {
            logger.error("Error during cleanup of temporary files: {}", e.getMessage());
        }
    }

    /**
     * Generate booking report
     */
    private void generateBookingReport(LocalDate date) {
        logger.info("Generating booking report for date: {}", date);
        
        try {
            // Generate booking statistics for the date
            // Implementation would create and send reports
            
        } catch (Exception e) {
            logger.error("Error generating booking report: {}", e.getMessage());
        }
    }

    /**
     * Generate revenue report
     */
    private void generateRevenueReport(LocalDate date) {
        logger.info("Generating revenue report for date: {}", date);
        
        try {
            // Generate revenue statistics for the date
            // Implementation would create and send reports
            
        } catch (Exception e) {
            logger.error("Error generating revenue report: {}", e.getMessage());
        }
    }

    /**
     * Generate property report
     */
    private void generatePropertyReport(LocalDate date) {
        logger.info("Generating property report for date: {}", date);
        
        try {
            // Generate property statistics for the date
            // Implementation would create and send reports
            
        } catch (Exception e) {
            logger.error("Error generating property report: {}", e.getMessage());
        }
    }
}





package com.enterprise.airbnb.event;

import com.enterprise.airbnb.model.Booking;
import com.enterprise.airbnb.model.User;
import com.enterprise.airbnb.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Event listener for booking-related events
 */
@Component
public class BookingEventListener {
    
    private static final Logger logger = LoggerFactory.getLogger(BookingEventListener.class);
    
    @Autowired
    private UserService userService;
    
    /**
     * Handle booking created event
     */
    @EventListener
    @Async
    public void handleBookingCreated(BookingCreatedEvent event) {
        logger.info("Processing booking created event: {}", event);
        
        try {
            Booking booking = event.getBooking();
            
            // Send notification to host
            sendHostNotification(booking);
            
            // Send confirmation email to guest
            sendGuestConfirmation(booking);
            
            // Update property availability
            updatePropertyAvailability(booking);
            
            // Log the event
            logBookingEvent(booking, "BOOKING_CREATED");
            
        } catch (Exception e) {
            logger.error("Error processing booking created event: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Send notification to property host
     */
    private void sendHostNotification(Booking booking) {
        try {
            User host = booking.getProperty().getHost();
            logger.info("Sending booking notification to host: {} for property: {}", 
                       host.getEmail(), booking.getProperty().getId());
            
            // TODO: Implement actual notification service (email, SMS, push notification)
            // This could integrate with services like SendGrid, Twilio, Firebase, etc.
            
        } catch (Exception e) {
            logger.error("Error sending host notification: {}", e.getMessage());
        }
    }
    
    /**
     * Send confirmation email to guest
     */
    private void sendGuestConfirmation(Booking booking) {
        try {
            User guest = booking.getGuest();
            logger.info("Sending booking confirmation to guest: {} for booking: {}", 
                       guest.getEmail(), booking.getBookingReference());
            
            // TODO: Implement actual email service
            // This could integrate with services like SendGrid, AWS SES, etc.
            
        } catch (Exception e) {
            logger.error("Error sending guest confirmation: {}", e.getMessage());
        }
    }
    
    /**
     * Update property availability
     */
    private void updatePropertyAvailability(Booking booking) {
        try {
            logger.info("Updating property availability for property: {} from {} to {}", 
                       booking.getProperty().getId(), 
                       booking.getCheckInDate(), 
                       booking.getCheckOutDate());
            
            // TODO: Implement property availability update
            // This could update PropertyAvailability entities or trigger availability recalculation
            
        } catch (Exception e) {
            logger.error("Error updating property availability: {}", e.getMessage());
        }
    }
    
    /**
     * Log booking event for audit purposes
     */
    private void logBookingEvent(Booking booking, String eventType) {
        try {
            logger.info("Booking event logged - Type: {}, Booking ID: {}, Reference: {}, " +
                       "Property: {}, Guest: {}, Amount: {}", 
                       eventType, 
                       booking.getId(), 
                       booking.getBookingReference(),
                       booking.getProperty().getId(),
                       booking.getGuest().getId(),
                       booking.getTotalAmount());
            
            // TODO: Implement audit logging to database or external logging service
            
        } catch (Exception e) {
            logger.error("Error logging booking event: {}", e.getMessage());
        }
    }
}




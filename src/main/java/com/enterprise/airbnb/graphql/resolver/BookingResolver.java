package com.enterprise.airbnb.graphql.resolver;

import com.enterprise.airbnb.model.Booking;
import com.enterprise.airbnb.model.Property;
import com.enterprise.airbnb.model.User;
import com.enterprise.airbnb.model.Payment;
import com.enterprise.airbnb.model.Review;
import com.enterprise.airbnb.service.PropertyService;
import com.enterprise.airbnb.service.UserService;
import com.enterprise.airbnb.service.PaymentService;
import com.enterprise.airbnb.service.ReviewService;
import graphql.kickstart.tools.GraphQLResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * GraphQL Resolver for Booking entity
 */
@Component
public class BookingResolver implements GraphQLResolver<Booking> {

    private static final Logger logger = LoggerFactory.getLogger(BookingResolver.class);

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private UserService userService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ReviewService reviewService;

    /**
     * Resolve property for booking
     */
    public Property property(Booking booking) {
        logger.debug("Resolving property for booking ID: {}", booking.getId());
        return booking.getProperty();
    }

    /**
     * Resolve guest for booking
     */
    public User guest(Booking booking) {
        logger.debug("Resolving guest for booking ID: {}", booking.getId());
        return booking.getGuest();
    }

    /**
     * Resolve payments for booking
     */
    public List<Payment> payments(Booking booking) {
        logger.debug("Resolving payments for booking ID: {}", booking.getId());
        return paymentService.getPaymentsByBooking(booking.getId());
    }

    /**
     * Resolve reviews for booking
     */
    public List<Review> reviews(Booking booking) {
        logger.debug("Resolving reviews for booking ID: {}", booking.getId());
        return reviewService.getReviewsByBooking(booking.getId());
    }

    /**
     * Calculate number of nights for booking
     */
    public Integer numberOfNights(Booking booking) {
        logger.debug("Calculating number of nights for booking ID: {}", booking.getId());
        if (booking.getCheckInDate() == null || booking.getCheckOutDate() == null) {
            return null;
        }
        
        return (int) java.time.temporal.ChronoUnit.DAYS.between(
            booking.getCheckInDate(), 
            booking.getCheckOutDate()
        );
    }

    /**
     * Check if booking is instant book
     */
    public Boolean isInstantBook(Booking booking) {
        logger.debug("Checking if booking is instant book for booking ID: {}", booking.getId());
        return booking.getProperty() != null && 
               booking.getProperty().getInstantBook() != null && 
               booking.getProperty().getInstantBook();
    }

    /**
     * Get booking source
     */
    public String bookingSource(Booking booking) {
        logger.debug("Getting booking source for booking ID: {}", booking.getId());
        return "WEB"; // Default source, can be enhanced based on business logic
    }
}




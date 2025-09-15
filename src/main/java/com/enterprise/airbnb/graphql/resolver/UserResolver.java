package com.enterprise.airbnb.graphql.resolver;

import com.enterprise.airbnb.model.User;
import com.enterprise.airbnb.model.Property;
import com.enterprise.airbnb.model.Booking;
import com.enterprise.airbnb.model.Review;
import com.enterprise.airbnb.model.Payment;
import com.enterprise.airbnb.service.PropertyService;
import com.enterprise.airbnb.service.BookingService;
import com.enterprise.airbnb.service.ReviewService;
import com.enterprise.airbnb.service.PaymentService;
import graphql.kickstart.tools.GraphQLResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * GraphQL Resolver for User entity
 */
@Component
public class UserResolver implements GraphQLResolver<User> {

    private static final Logger logger = LoggerFactory.getLogger(UserResolver.class);

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private PaymentService paymentService;

    /**
     * Resolve properties for user (host)
     */
    public List<Property> properties(User user) {
        logger.debug("Resolving properties for user ID: {}", user.getId());
        return propertyService.getPropertiesByHost(user.getId());
    }

    /**
     * Resolve bookings for user (guest)
     */
    public List<Booking> bookings(User user) {
        logger.debug("Resolving bookings for user ID: {}", user.getId());
        return bookingService.getBookingsByUser(user.getId());
    }

    /**
     * Resolve reviews for user
     */
    public List<Review> reviews(User user) {
        logger.debug("Resolving reviews for user ID: {}", user.getId());
        return reviewService.getReviewsByUser(user.getId());
    }

    /**
     * Resolve payments for user
     */
    public List<Payment> payments(User user) {
        logger.debug("Resolving payments for user ID: {}", user.getId());
        return paymentService.getPaymentsByUser(user.getId());
    }

    /**
     * Calculate host rating for user
     */
    public Double hostRating(User user) {
        logger.debug("Calculating host rating for user ID: {}", user.getId());
        List<Review> reviews = reviewService.getReviewsByUser(user.getId());
        
        if (reviews.isEmpty()) {
            return null;
        }

        double sum = reviews.stream()
            .filter(review -> review.getRating() != null)
            .mapToInt(Review::getRating)
            .sum();
        
        return sum / reviews.size();
    }

    /**
     * Calculate guest rating for user
     */
    public Double guestRating(User user) {
        logger.debug("Calculating guest rating for user ID: {}", user.getId());
        // This would typically be calculated from reviews where the user is the reviewee
        // For now, return the same as host rating
        return hostRating(user);
    }

    /**
     * Get total number of reviews for user
     */
    public Integer totalReviews(User user) {
        logger.debug("Getting total reviews for user ID: {}", user.getId());
        return reviewService.getReviewsByUser(user.getId()).size();
    }
}




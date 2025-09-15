package com.enterprise.airbnb.graphql.resolver;

import com.enterprise.airbnb.model.Property;
import com.enterprise.airbnb.model.User;
import com.enterprise.airbnb.model.Booking;
import com.enterprise.airbnb.model.Review;
import com.enterprise.airbnb.model.PropertyAvailability;
import com.enterprise.airbnb.service.PropertyService;
import com.enterprise.airbnb.service.UserService;
import com.enterprise.airbnb.service.BookingService;
import com.enterprise.airbnb.service.ReviewService;
import com.enterprise.airbnb.service.PropertyAvailabilityService;
import graphql.kickstart.tools.GraphQLResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * GraphQL Resolver for Property entity
 */
@Component
public class PropertyResolver implements GraphQLResolver<Property> {

    private static final Logger logger = LoggerFactory.getLogger(PropertyResolver.class);

    @Autowired
    private UserService userService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private PropertyAvailabilityService propertyAvailabilityService;

    /**
     * Resolve host for property
     */
    public User host(Property property) {
        logger.debug("Resolving host for property ID: {}", property.getId());
        return property.getHost();
    }

    /**
     * Resolve bookings for property
     */
    public List<Booking> bookings(Property property) {
        logger.debug("Resolving bookings for property ID: {}", property.getId());
        return bookingService.getBookingsByProperty(property.getId());
    }

    /**
     * Resolve reviews for property
     */
    public List<Review> reviews(Property property) {
        logger.debug("Resolving reviews for property ID: {}", property.getId());
        return reviewService.getReviewsByProperty(property.getId());
    }

    /**
     * Resolve availability for property
     */
    public List<PropertyAvailability> availability(Property property) {
        logger.debug("Resolving availability for property ID: {}", property.getId());
        return propertyAvailabilityService.getAvailabilityByProperty(property.getId());
    }

    /**
     * Calculate average rating for property
     */
    public Double rating(Property property) {
        logger.debug("Calculating rating for property ID: {}", property.getId());
        List<Review> reviews = reviewService.getReviewsByProperty(property.getId());
        
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
     * Get total number of reviews for property
     */
    public Integer totalReviews(Property property) {
        logger.debug("Getting total reviews for property ID: {}", property.getId());
        return reviewService.getReviewsByProperty(property.getId()).size();
    }
}




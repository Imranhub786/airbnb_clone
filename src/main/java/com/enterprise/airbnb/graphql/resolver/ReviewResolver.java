package com.enterprise.airbnb.graphql.resolver;

import com.enterprise.airbnb.model.Review;
import com.enterprise.airbnb.model.Booking;
import com.enterprise.airbnb.model.Property;
import com.enterprise.airbnb.model.User;
import com.enterprise.airbnb.service.BookingService;
import com.enterprise.airbnb.service.PropertyService;
import com.enterprise.airbnb.service.UserService;
import graphql.kickstart.tools.GraphQLResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * GraphQL Resolver for Review entity
 */
@Component
public class ReviewResolver implements GraphQLResolver<Review> {

    private static final Logger logger = LoggerFactory.getLogger(ReviewResolver.class);

    @Autowired
    private BookingService bookingService;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private UserService userService;

    /**
     * Resolve booking for review
     */
    public Booking booking(Review review) {
        logger.debug("Resolving booking for review ID: {}", review.getId());
        return review.getBooking();
    }

    /**
     * Resolve property for review
     */
    public Property property(Review review) {
        logger.debug("Resolving property for review ID: {}", review.getId());
        return review.getProperty();
    }

    /**
     * Resolve reviewer for review
     */
    public User reviewer(Review review) {
        logger.debug("Resolving reviewer for review ID: {}", review.getId());
        return review.getReviewer();
    }

    /**
     * Resolve reviewee for review
     */
    public User reviewee(Review review) {
        logger.debug("Resolving reviewee for review ID: {}", review.getId());
        return review.getReviewee();
    }

    /**
     * Check if review is verified stay
     */
    public Boolean isVerifiedStay(Review review) {
        logger.debug("Checking if review is verified stay for review ID: {}", review.getId());
        // A review is verified if it's associated with a completed booking
        return review.getBooking() != null && 
               review.getBooking().getStatus() != null &&
               review.getBooking().getStatus().name().equals("COMPLETED");
    }
}




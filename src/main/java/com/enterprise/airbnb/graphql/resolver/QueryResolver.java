package com.enterprise.airbnb.graphql.resolver;

import com.enterprise.airbnb.model.User;
import com.enterprise.airbnb.model.Property;
import com.enterprise.airbnb.model.Booking;
import com.enterprise.airbnb.model.Payment;
import com.enterprise.airbnb.model.Review;
import com.enterprise.airbnb.model.Image;
import com.enterprise.airbnb.model.UserRole;
import com.enterprise.airbnb.model.AccountStatus;
import com.enterprise.airbnb.model.PropertyStatus;
import com.enterprise.airbnb.model.PropertyType;
import com.enterprise.airbnb.model.BookingStatus;
import com.enterprise.airbnb.model.PaymentStatus;
import com.enterprise.airbnb.model.ReviewStatus;
import com.enterprise.airbnb.model.ImageCategory;
import com.enterprise.airbnb.service.UserService;
import com.enterprise.airbnb.service.PropertyService;
import com.enterprise.airbnb.service.BookingService;
import com.enterprise.airbnb.service.PaymentService;
import com.enterprise.airbnb.service.ReviewService;
import com.enterprise.airbnb.service.ImageService;
import com.enterprise.airbnb.graphql.dto.PropertySearchInput;
import com.enterprise.airbnb.graphql.dto.PropertySearchResponse;
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * GraphQL Query Resolver
 */
@Component
public class QueryResolver implements GraphQLQueryResolver {

    private static final Logger logger = LoggerFactory.getLogger(QueryResolver.class);

    @Autowired
    private UserService userService;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ImageService imageService;

    // User Queries

    /**
     * Get user by ID
     */
    public User user(Long id) {
        logger.debug("GraphQL query: user(id: {})", id);
        return userService.getUserById(id).orElse(null);
    }

    /**
     * Get users with pagination and filters
     */
    public List<User> users(Integer page, Integer size, UserRole role, AccountStatus status) {
        logger.debug("GraphQL query: users(page: {}, size: {}, role: {}, status: {})", page, size, role, status);
        
        Pageable pageable = PageRequest.of(page != null ? page : 0, size != null ? size : 10);
        Page<User> userPage = userService.getUsers(pageable, role, status);
        return userPage.getContent();
    }

    /**
     * Get current authenticated user
     */
    public User currentUser() {
        logger.debug("GraphQL query: currentUser()");
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        
        String email = authentication.getName();
        return userService.getUserByEmail(email).orElse(null);
    }

    // Property Queries

    /**
     * Get property by ID
     */
    public Property property(Long id) {
        logger.debug("GraphQL query: property(id: {})", id);
        return propertyService.getPropertyById(id).orElse(null);
    }

    /**
     * Get properties with pagination and filters
     */
    public List<Property> properties(Integer page, Integer size, PropertyStatus status, PropertyType type) {
        logger.debug("GraphQL query: properties(page: {}, size: {}, status: {}, type: {})", page, size, status, type);
        
        Pageable pageable = PageRequest.of(page != null ? page : 0, size != null ? size : 10);
        Page<Property> propertyPage = propertyService.getProperties(pageable, status, type);
        return propertyPage.getContent();
    }

    /**
     * Get properties by host
     */
    public List<Property> propertiesByHost(Long hostId, Integer page, Integer size) {
        logger.debug("GraphQL query: propertiesByHost(hostId: {}, page: {}, size: {})", hostId, page, size);
        
        Pageable pageable = PageRequest.of(page != null ? page : 0, size != null ? size : 10);
        return propertyService.getPropertiesByHost(hostId, pageable);
    }

    /**
     * Search properties
     */
    public PropertySearchResponse searchProperties(PropertySearchInput input) {
        logger.debug("GraphQL query: searchProperties(input: {})", input);
        
        // This would typically call a more sophisticated search service
        // For now, return a basic implementation
        Pageable pageable = PageRequest.of(
            input.getPage() != null ? input.getPage() : 0, 
            input.getSize() != null ? input.getSize() : 10
        );
        
        Page<Property> propertyPage = propertyService.searchProperties(input, pageable);
        
        PropertySearchResponse response = new PropertySearchResponse();
        response.setProperties(propertyPage.getContent());
        response.setTotalCount(propertyPage.getTotalElements());
        response.setPage(propertyPage.getNumber());
        response.setSize(propertyPage.getSize());
        response.setTotalPages(propertyPage.getTotalPages());
        response.setHasNext(propertyPage.hasNext());
        response.setHasPrevious(propertyPage.hasPrevious());
        
        return response;
    }

    /**
     * Get properties by location
     */
    public List<Property> propertiesByLocation(Double latitude, Double longitude, Double radius) {
        logger.debug("GraphQL query: propertiesByLocation(lat: {}, lng: {}, radius: {})", latitude, longitude, radius);
        
        // This would typically use a geospatial search
        // For now, return all properties
        return propertyService.getAllProperties();
    }

    // Booking Queries

    /**
     * Get booking by ID
     */
    public Booking booking(Long id) {
        logger.debug("GraphQL query: booking(id: {})", id);
        return bookingService.getBookingById(id).orElse(null);
    }

    /**
     * Get booking by reference
     */
    public Booking bookingByReference(String reference) {
        logger.debug("GraphQL query: bookingByReference(reference: {})", reference);
        return bookingService.getBookingByReference(reference).orElse(null);
    }

    /**
     * Get bookings with pagination and filters
     */
    public List<Booking> bookings(Integer page, Integer size, BookingStatus status) {
        logger.debug("GraphQL query: bookings(page: {}, size: {}, status: {})", page, size, status);
        
        Pageable pageable = PageRequest.of(page != null ? page : 0, size != null ? size : 10);
        Page<Booking> bookingPage = bookingService.getBookings(pageable, status);
        return bookingPage.getContent();
    }

    /**
     * Get bookings by user
     */
    public List<Booking> bookingsByUser(Long userId, Integer page, Integer size) {
        logger.debug("GraphQL query: bookingsByUser(userId: {}, page: {}, size: {})", userId, page, size);
        
        Pageable pageable = PageRequest.of(page != null ? page : 0, size != null ? size : 10);
        return bookingService.getBookingsByUser(userId, pageable);
    }

    /**
     * Get bookings by property
     */
    public List<Booking> bookingsByProperty(Long propertyId, Integer page, Integer size) {
        logger.debug("GraphQL query: bookingsByProperty(propertyId: {}, page: {}, size: {})", propertyId, page, size);
        
        Pageable pageable = PageRequest.of(page != null ? page : 0, size != null ? size : 10);
        return bookingService.getBookingsByProperty(propertyId, pageable);
    }

    /**
     * Get bookings by host
     */
    public List<Booking> bookingsByHost(Long hostId, Integer page, Integer size) {
        logger.debug("GraphQL query: bookingsByHost(hostId: {}, page: {}, size: {})", hostId, page, size);
        
        Pageable pageable = PageRequest.of(page != null ? page : 0, size != null ? size : 10);
        return bookingService.getBookingsByHost(hostId, pageable);
    }

    /**
     * Get upcoming bookings for user
     */
    public List<Booking> upcomingBookings(Long userId) {
        logger.debug("GraphQL query: upcomingBookings(userId: {})", userId);
        return bookingService.getUpcomingBookings(userId);
    }

    /**
     * Get current bookings for user
     */
    public List<Booking> currentBookings(Long userId) {
        logger.debug("GraphQL query: currentBookings(userId: {})", userId);
        return bookingService.getCurrentBookings(userId);
    }

    /**
     * Get past bookings for user
     */
    public List<Booking> pastBookings(Long userId) {
        logger.debug("GraphQL query: pastBookings(userId: {})", userId);
        return bookingService.getPastBookings(userId);
    }

    // Payment Queries

    /**
     * Get payment by ID
     */
    public Payment payment(Long id) {
        logger.debug("GraphQL query: payment(id: {})", id);
        return paymentService.getPaymentById(id).orElse(null);
    }

    /**
     * Get payments with pagination and filters
     */
    public List<Payment> payments(Integer page, Integer size, PaymentStatus status) {
        logger.debug("GraphQL query: payments(page: {}, size: {}, status: {})", page, size, status);
        
        Pageable pageable = PageRequest.of(page != null ? page : 0, size != null ? size : 10);
        Page<Payment> paymentPage = paymentService.getPayments(pageable, status);
        return paymentPage.getContent();
    }

    /**
     * Get payments by user
     */
    public List<Payment> paymentsByUser(Long userId, Integer page, Integer size) {
        logger.debug("GraphQL query: paymentsByUser(userId: {}, page: {}, size: {})", userId, page, size);
        
        Pageable pageable = PageRequest.of(page != null ? page : 0, size != null ? size : 10);
        return paymentService.getPaymentsByUser(userId, pageable);
    }

    /**
     * Get payments by booking
     */
    public List<Payment> paymentsByBooking(Long bookingId) {
        logger.debug("GraphQL query: paymentsByBooking(bookingId: {})", bookingId);
        return paymentService.getPaymentsByBooking(bookingId);
    }

    // Review Queries

    /**
     * Get review by ID
     */
    public Review review(Long id) {
        logger.debug("GraphQL query: review(id: {})", id);
        return reviewService.getReviewById(id).orElse(null);
    }

    /**
     * Get reviews with pagination and filters
     */
    public List<Review> reviews(Integer page, Integer size, ReviewStatus status) {
        logger.debug("GraphQL query: reviews(page: {}, size: {}, status: {})", page, size, status);
        
        Pageable pageable = PageRequest.of(page != null ? page : 0, size != null ? size : 10);
        Page<Review> reviewPage = reviewService.getReviews(pageable, status);
        return reviewPage.getContent();
    }

    /**
     * Get reviews by property
     */
    public List<Review> reviewsByProperty(Long propertyId, Integer page, Integer size) {
        logger.debug("GraphQL query: reviewsByProperty(propertyId: {}, page: {}, size: {})", propertyId, page, size);
        
        Pageable pageable = PageRequest.of(page != null ? page : 0, size != null ? size : 10);
        return reviewService.getReviewsByProperty(propertyId, pageable);
    }

    /**
     * Get reviews by user
     */
    public List<Review> reviewsByUser(Long userId, Integer page, Integer size) {
        logger.debug("GraphQL query: reviewsByUser(userId: {}, page: {}, size: {})", userId, page, size);
        
        Pageable pageable = PageRequest.of(page != null ? page : 0, size != null ? size : 10);
        return reviewService.getReviewsByUser(userId, pageable);
    }

    /**
     * Get reviews by booking
     */
    public List<Review> reviewsByBooking(Long bookingId) {
        logger.debug("GraphQL query: reviewsByBooking(bookingId: {})", bookingId);
        return reviewService.getReviewsByBooking(bookingId);
    }

    // Image Queries

    /**
     * Get image by ID
     */
    public Image image(Long id) {
        logger.debug("GraphQL query: image(id: {})", id);
        return imageService.getImageById(id).orElse(null);
    }

    /**
     * Get images with pagination and filters
     */
    public List<Image> images(Integer page, Integer size, ImageCategory category) {
        logger.debug("GraphQL query: images(page: {}, size: {}, category: {})", page, size, category);
        
        Pageable pageable = PageRequest.of(page != null ? page : 0, size != null ? size : 10);
        Page<Image> imagePage = imageService.getImages(pageable, category);
        return imagePage.getContent();
    }

    /**
     * Get images by property
     */
    public List<Image> imagesByProperty(Long propertyId) {
        logger.debug("GraphQL query: imagesByProperty(propertyId: {})", propertyId);
        return imageService.getImagesByProperty(propertyId);
    }

    /**
     * Get images by user
     */
    public List<Image> imagesByUser(Long userId) {
        logger.debug("GraphQL query: imagesByUser(userId: {})", userId);
        return imageService.getImagesByUser(userId);
    }
}




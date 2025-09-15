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
import com.enterprise.airbnb.model.Amenity;
import com.enterprise.airbnb.model.PaymentType;
import com.enterprise.airbnb.model.PaymentMethod;
import com.enterprise.airbnb.model.ReviewType;
import com.enterprise.airbnb.model.ImageCategory;
import com.enterprise.airbnb.service.UserService;
import com.enterprise.airbnb.service.PropertyService;
import com.enterprise.airbnb.service.BookingService;
import com.enterprise.airbnb.service.PaymentService;
import com.enterprise.airbnb.service.ReviewService;
import com.enterprise.airbnb.service.ImageService;
import com.enterprise.airbnb.graphql.dto.UserInput;
import com.enterprise.airbnb.graphql.dto.PropertyInput;
import com.enterprise.airbnb.graphql.dto.BookingInput;
import com.enterprise.airbnb.graphql.dto.PaymentInput;
import com.enterprise.airbnb.graphql.dto.ReviewInput;
import com.enterprise.airbnb.graphql.dto.AuthResponse;
import com.enterprise.airbnb.graphql.dto.BookingResponse;
import com.enterprise.airbnb.graphql.dto.PaymentResponse;
import com.enterprise.airbnb.util.JwtUtil;
import graphql.kickstart.tools.GraphQLMutationResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * GraphQL Mutation Resolver
 */
@Component
public class MutationResolver implements GraphQLMutationResolver {

    private static final Logger logger = LoggerFactory.getLogger(MutationResolver.class);

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

    @Autowired
    private JwtUtil jwtUtil;

    // User Mutations

    /**
     * Register a new user
     */
    public AuthResponse registerUser(UserInput input) {
        logger.debug("GraphQL mutation: registerUser(input: {})", input);
        
        try {
            User user = new User();
            user.setFirstName(input.getFirstName());
            user.setLastName(input.getLastName());
            user.setEmail(input.getEmail());
            user.setPassword(input.getPassword());
            user.setPhoneNumber(input.getPhoneNumber());
            user.setRole(input.getRole() != null ? input.getRole() : UserRole.GUEST);
            user.setBio(input.getBio());
            user.setDateOfBirth(input.getDateOfBirth());
            user.setPreferredCurrency(input.getPreferredCurrency() != null ? input.getPreferredCurrency() : "USD");
            user.setPreferredLanguage(input.getPreferredLanguage() != null ? input.getPreferredLanguage() : "en");

            User savedUser = userService.createUser(user);
            String token = jwtUtil.generateToken(savedUser.getEmail());

            AuthResponse response = new AuthResponse();
            response.setUser(savedUser);
            response.setToken(token);
            response.setMessage("User registered successfully");
            
            return response;
        } catch (Exception e) {
            logger.error("Error registering user: {}", e.getMessage());
            throw new RuntimeException("Registration failed: " + e.getMessage());
        }
    }

    /**
     * Login user
     */
    public AuthResponse loginUser(String email, String password) {
        logger.debug("GraphQL mutation: loginUser(email: {})", email);
        
        try {
            // This would typically use authentication manager
            // For now, return a basic implementation
            User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            String token = jwtUtil.generateToken(user.getEmail());

            AuthResponse response = new AuthResponse();
            response.setUser(user);
            response.setToken(token);
            response.setMessage("Login successful");
            
            return response;
        } catch (Exception e) {
            logger.error("Error logging in user: {}", e.getMessage());
            throw new RuntimeException("Login failed: " + e.getMessage());
        }
    }

    /**
     * Update user
     */
    public User updateUser(Long id, UserInput input) {
        logger.debug("GraphQL mutation: updateUser(id: {}, input: {})", id, input);
        
        User user = userService.getUserById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setFirstName(input.getFirstName());
        user.setLastName(input.getLastName());
        user.setPhoneNumber(input.getPhoneNumber());
        user.setBio(input.getBio());
        user.setDateOfBirth(input.getDateOfBirth());
        user.setPreferredCurrency(input.getPreferredCurrency());
        user.setPreferredLanguage(input.getPreferredLanguage());
        
        return userService.updateUser(user);
    }

    /**
     * Delete user
     */
    public Boolean deleteUser(Long id) {
        logger.debug("GraphQL mutation: deleteUser(id: {})", id);
        
        try {
            userService.deleteUser(id);
            return true;
        } catch (Exception e) {
            logger.error("Error deleting user: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Change password
     */
    public Boolean changePassword(Long id, String oldPassword, String newPassword) {
        logger.debug("GraphQL mutation: changePassword(id: {})", id);
        
        try {
            // This would typically validate old password
            User user = userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            user.setPassword(newPassword);
            userService.updateUser(user);
            return true;
        } catch (Exception e) {
            logger.error("Error changing password: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Reset password
     */
    public Boolean resetPassword(Long id, String newPassword) {
        logger.debug("GraphQL mutation: resetPassword(id: {})", id);
        
        try {
            User user = userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            user.setPassword(newPassword);
            userService.updateUser(user);
            return true;
        } catch (Exception e) {
            logger.error("Error resetting password: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Verify email
     */
    public Boolean verifyEmail(Long id) {
        logger.debug("GraphQL mutation: verifyEmail(id: {})", id);
        
        try {
            User user = userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            user.setIsEmailVerified(true);
            userService.updateUser(user);
            return true;
        } catch (Exception e) {
            logger.error("Error verifying email: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Verify phone
     */
    public Boolean verifyPhone(Long id) {
        logger.debug("GraphQL mutation: verifyPhone(id: {})", id);
        
        try {
            User user = userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            user.setIsPhoneVerified(true);
            userService.updateUser(user);
            return true;
        } catch (Exception e) {
            logger.error("Error verifying phone: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Update user role
     */
    public User updateUserRole(Long id, UserRole role) {
        logger.debug("GraphQL mutation: updateUserRole(id: {}, role: {})", id, role);
        
        User user = userService.getUserById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setRole(role);
        return userService.updateUser(user);
    }

    /**
     * Update user status
     */
    public User updateUserStatus(Long id, AccountStatus status) {
        logger.debug("GraphQL mutation: updateUserStatus(id: {}, status: {})", id, status);
        
        User user = userService.getUserById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setStatus(status);
        return userService.updateUser(user);
    }

    // Property Mutations

    /**
     * Create property
     */
    public Property createProperty(PropertyInput input) {
        logger.debug("GraphQL mutation: createProperty(input: {})", input);
        
        Property property = new Property();
        property.setTitle(input.getTitle());
        property.setDescription(input.getDescription());
        property.setPropertyType(input.getPropertyType());
        property.setPricePerNight(input.getPricePerNight());
        property.setCurrency(input.getCurrency() != null ? input.getCurrency() : "USD");
        property.setMaxGuests(input.getMaxGuests());
        property.setBedrooms(input.getBedrooms());
        property.setBathrooms(input.getBathrooms());
        property.setBeds(input.getBeds());
        property.setAddress(input.getAddress());
        property.setAmenities(input.getAmenities());
        property.setImages(input.getImages());
        property.setMainImageUrl(input.getMainImageUrl());
        property.setCleaningFee(input.getCleaningFee());
        property.setServiceFee(input.getServiceFee());
        property.setSecurityDeposit(input.getSecurityDeposit());
        property.setMinimumNights(input.getMinimumNights() != null ? input.getMinimumNights() : 1);
        property.setMaximumNights(input.getMaximumNights() != null ? input.getMaximumNights() : 30);
        property.setCheckInTime(input.getCheckInTime() != null ? input.getCheckInTime() : "15:00");
        property.setCheckOutTime(input.getCheckOutTime() != null ? input.getCheckOutTime() : "11:00");
        property.setInstantBook(input.getInstantBook() != null ? input.getInstantBook() : false);
        property.setIsPetFriendly(input.getIsPetFriendly() != null ? input.getIsPetFriendly() : false);
        property.setIsSmokingAllowed(input.getIsSmokingAllowed() != null ? input.getIsSmokingAllowed() : false);
        property.setIsEntirePlace(input.getIsEntirePlace() != null ? input.getIsEntirePlace() : true);
        property.setCancellationPolicy(input.getCancellationPolicy() != null ? input.getCancellationPolicy() : "MODERATE");
        property.setHouseRules(input.getHouseRules());

        // Set host from current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String email = authentication.getName();
            User host = userService.getUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("Host not found"));
            property.setHost(host);
        }

        return propertyService.createProperty(property);
    }

    /**
     * Update property
     */
    public Property updateProperty(Long id, PropertyInput input) {
        logger.debug("GraphQL mutation: updateProperty(id: {}, input: {})", id, input);
        
        Property property = propertyService.getPropertyById(id)
            .orElseThrow(() -> new RuntimeException("Property not found"));
        
        property.setTitle(input.getTitle());
        property.setDescription(input.getDescription());
        property.setPropertyType(input.getPropertyType());
        property.setPricePerNight(input.getPricePerNight());
        property.setCurrency(input.getCurrency());
        property.setMaxGuests(input.getMaxGuests());
        property.setBedrooms(input.getBedrooms());
        property.setBathrooms(input.getBathrooms());
        property.setBeds(input.getBeds());
        property.setAddress(input.getAddress());
        property.setAmenities(input.getAmenities());
        property.setImages(input.getImages());
        property.setMainImageUrl(input.getMainImageUrl());
        property.setCleaningFee(input.getCleaningFee());
        property.setServiceFee(input.getServiceFee());
        property.setSecurityDeposit(input.getSecurityDeposit());
        property.setMinimumNights(input.getMinimumNights());
        property.setMaximumNights(input.getMaximumNights());
        property.setCheckInTime(input.getCheckInTime());
        property.setCheckOutTime(input.getCheckOutTime());
        property.setInstantBook(input.getInstantBook());
        property.setIsPetFriendly(input.getIsPetFriendly());
        property.setIsSmokingAllowed(input.getIsSmokingAllowed());
        property.setIsEntirePlace(input.getIsEntirePlace());
        property.setCancellationPolicy(input.getCancellationPolicy());
        property.setHouseRules(input.getHouseRules());
        
        return propertyService.updateProperty(property);
    }

    /**
     * Delete property
     */
    public Boolean deleteProperty(Long id) {
        logger.debug("GraphQL mutation: deleteProperty(id: {})", id);
        
        try {
            propertyService.deleteProperty(id);
            return true;
        } catch (Exception e) {
            logger.error("Error deleting property: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Update property status
     */
    public Property updatePropertyStatus(Long id, PropertyStatus status) {
        logger.debug("GraphQL mutation: updatePropertyStatus(id: {}, status: {})", id, status);
        
        Property property = propertyService.getPropertyById(id)
            .orElseThrow(() -> new RuntimeException("Property not found"));
        
        property.setStatus(status);
        return propertyService.updateProperty(property);
    }

    /**
     * Add property image
     */
    public Property addPropertyImage(Long id, String imageUrl) {
        logger.debug("GraphQL mutation: addPropertyImage(id: {}, imageUrl: {})", id, imageUrl);
        
        Property property = propertyService.getPropertyById(id)
            .orElseThrow(() -> new RuntimeException("Property not found"));
        
        property.getImages().add(imageUrl);
        return propertyService.updateProperty(property);
    }

    /**
     * Remove property image
     */
    public Property removePropertyImage(Long id, String imageUrl) {
        logger.debug("GraphQL mutation: removePropertyImage(id: {}, imageUrl: {})", id, imageUrl);
        
        Property property = propertyService.getPropertyById(id)
            .orElseThrow(() -> new RuntimeException("Property not found"));
        
        property.getImages().remove(imageUrl);
        return propertyService.updateProperty(property);
    }

    /**
     * Set main property image
     */
    public Property setMainPropertyImage(Long id, String imageUrl) {
        logger.debug("GraphQL mutation: setMainPropertyImage(id: {}, imageUrl: {})", id, imageUrl);
        
        Property property = propertyService.getPropertyById(id)
            .orElseThrow(() -> new RuntimeException("Property not found"));
        
        property.setMainImageUrl(imageUrl);
        return propertyService.updateProperty(property);
    }

    /**
     * Add property amenity
     */
    public Property addPropertyAmenity(Long id, Amenity amenity) {
        logger.debug("GraphQL mutation: addPropertyAmenity(id: {}, amenity: {})", id, amenity);
        
        Property property = propertyService.getPropertyById(id)
            .orElseThrow(() -> new RuntimeException("Property not found"));
        
        property.getAmenities().add(amenity);
        return propertyService.updateProperty(property);
    }

    /**
     * Remove property amenity
     */
    public Property removePropertyAmenity(Long id, Amenity amenity) {
        logger.debug("GraphQL mutation: removePropertyAmenity(id: {}, amenity: {})", id, amenity);
        
        Property property = propertyService.getPropertyById(id)
            .orElseThrow(() -> new RuntimeException("Property not found"));
        
        property.getAmenities().remove(amenity);
        return propertyService.updateProperty(property);
    }

    // Booking Mutations

    /**
     * Create booking
     */
    public BookingResponse createBooking(BookingInput input) {
        logger.debug("GraphQL mutation: createBooking(input: {})", input);
        
        try {
            Booking booking = new Booking();
            booking.setCheckInDate(input.getCheckInDate());
            booking.setCheckOutDate(input.getCheckOutDate());
            booking.setNumberOfGuests(input.getNumberOfGuests());
            booking.setSpecialRequests(input.getSpecialRequests());
            booking.setGuestMessage(input.getGuestMessage());

            // Set property
            Property property = propertyService.getPropertyById(input.getPropertyId())
                .orElseThrow(() -> new RuntimeException("Property not found"));
            booking.setProperty(property);

            // Set guest
            User guest = userService.getUserById(input.getGuestId())
                .orElseThrow(() -> new RuntimeException("Guest not found"));
            booking.setGuest(guest);

            // Calculate pricing
            booking.setBasePrice(property.getPricePerNight());
            booking.setCleaningFee(property.getCleaningFee());
            booking.setServiceFee(property.getServiceFee());
            booking.setSecurityDeposit(property.getSecurityDeposit());
            booking.setCurrency(property.getCurrency());

            Booking createdBooking = bookingService.createBooking(booking);

            BookingResponse response = new BookingResponse();
            response.setBooking(createdBooking);
            response.setMessage("Booking created successfully");
            
            return response;
        } catch (Exception e) {
            logger.error("Error creating booking: {}", e.getMessage());
            throw new RuntimeException("Booking creation failed: " + e.getMessage());
        }
    }

    /**
     * Update booking
     */
    public Booking updateBooking(Long id, BookingInput input) {
        logger.debug("GraphQL mutation: updateBooking(id: {}, input: {})", id, input);
        
        Booking booking = bookingService.getBookingById(id)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        booking.setCheckInDate(input.getCheckInDate());
        booking.setCheckOutDate(input.getCheckOutDate());
        booking.setNumberOfGuests(input.getNumberOfGuests());
        booking.setSpecialRequests(input.getSpecialRequests());
        booking.setGuestMessage(input.getGuestMessage());
        
        return bookingService.updateBooking(booking);
    }

    /**
     * Cancel booking
     */
    public Booking cancelBooking(Long id, String reason) {
        logger.debug("GraphQL mutation: cancelBooking(id: {}, reason: {})", id, reason);
        
        return bookingService.cancelBooking(id, reason);
    }

    /**
     * Confirm booking
     */
    public Booking confirmBooking(Long id) {
        logger.debug("GraphQL mutation: confirmBooking(id: {})", id);
        
        return bookingService.confirmBooking(id);
    }

    /**
     * Check in booking
     */
    public Booking checkInBooking(Long id) {
        logger.debug("GraphQL mutation: checkInBooking(id: {})", id);
        
        return bookingService.checkInBooking(id);
    }

    /**
     * Check out booking
     */
    public Booking checkOutBooking(Long id) {
        logger.debug("GraphQL mutation: checkOutBooking(id: {})", id);
        
        return bookingService.checkOutBooking(id);
    }

    /**
     * Complete booking
     */
    public Booking completeBooking(Long id) {
        logger.debug("GraphQL mutation: completeBooking(id: {})", id);
        
        return bookingService.completeBooking(id);
    }

    // Payment Mutations

    /**
     * Create payment
     */
    public PaymentResponse createPayment(PaymentInput input) {
        logger.debug("GraphQL mutation: createPayment(input: {})", input);
        
        try {
            Payment payment = new Payment();
            payment.setAmount(input.getAmount());
            payment.setCurrency(input.getCurrency());
            payment.setPaymentType(input.getPaymentType());
            payment.setPaymentMethod(input.getPaymentMethod());
            payment.setDescription(input.getDescription());
            payment.setReturnUrl(input.getReturnUrl());
            payment.setCancelUrl(input.getCancelUrl());

            // Set booking
            Booking booking = bookingService.getBookingById(input.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));
            payment.setBooking(booking);

            // Set user
            User user = userService.getUserById(input.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
            payment.setUser(user);

            Payment createdPayment = paymentService.createPayment(payment);

            PaymentResponse response = new PaymentResponse();
            response.setPayment(createdPayment);
            response.setMessage("Payment created successfully");
            
            return response;
        } catch (Exception e) {
            logger.error("Error creating payment: {}", e.getMessage());
            throw new RuntimeException("Payment creation failed: " + e.getMessage());
        }
    }

    /**
     * Process payment
     */
    public PaymentResponse processPayment(Long id) {
        logger.debug("GraphQL mutation: processPayment(id: {})", id);
        
        try {
            Payment payment = paymentService.processPayment(id);
            
            PaymentResponse response = new PaymentResponse();
            response.setPayment(payment);
            response.setMessage("Payment processed successfully");
            
            return response;
        } catch (Exception e) {
            logger.error("Error processing payment: {}", e.getMessage());
            throw new RuntimeException("Payment processing failed: " + e.getMessage());
        }
    }

    /**
     * Refund payment
     */
    public Payment refundPayment(Long id, BigDecimal amount, String reason) {
        logger.debug("GraphQL mutation: refundPayment(id: {}, amount: {}, reason: {})", id, amount, reason);
        
        return paymentService.refundPayment(id, amount, reason);
    }

    /**
     * Update payment status
     */
    public Payment updatePaymentStatus(Long id, PaymentStatus status) {
        logger.debug("GraphQL mutation: updatePaymentStatus(id: {}, status: {})", id, status);
        
        return paymentService.updatePaymentStatus(id, status);
    }

    // Review Mutations

    /**
     * Create review
     */
    public Review createReview(ReviewInput input) {
        logger.debug("GraphQL mutation: createReview(input: {})", input);
        
        Review review = new Review();
        review.setRating(input.getRating());
        review.setReviewText(input.getReviewText());
        review.setReviewType(input.getReviewType());
        review.setIsAnonymous(input.getIsAnonymous() != null ? input.getIsAnonymous() : false);

        // Set booking
        Booking booking = bookingService.getBookingById(input.getBookingId())
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        review.setBooking(booking);

        // Set property
        Property property = propertyService.getPropertyById(input.getPropertyId())
            .orElseThrow(() -> new RuntimeException("Property not found"));
        review.setProperty(property);

        // Set reviewer
        User reviewer = userService.getUserById(input.getReviewerId())
            .orElseThrow(() -> new RuntimeException("Reviewer not found"));
        review.setReviewer(reviewer);

        // Set reviewee
        User reviewee = userService.getUserById(input.getRevieweeId())
            .orElseThrow(() -> new RuntimeException("Reviewee not found"));
        review.setReviewee(reviewee);

        return reviewService.createReview(review);
    }

    /**
     * Update review
     */
    public Review updateReview(Long id, ReviewInput input) {
        logger.debug("GraphQL mutation: updateReview(id: {}, input: {})", id, input);
        
        Review review = reviewService.getReviewById(id)
            .orElseThrow(() -> new RuntimeException("Review not found"));
        
        review.setRating(input.getRating());
        review.setReviewText(input.getReviewText());
        review.setIsAnonymous(input.getIsAnonymous());
        
        return reviewService.updateReview(review);
    }

    /**
     * Delete review
     */
    public Boolean deleteReview(Long id) {
        logger.debug("GraphQL mutation: deleteReview(id: {})", id);
        
        try {
            reviewService.deleteReview(id);
            return true;
        } catch (Exception e) {
            logger.error("Error deleting review: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Publish review
     */
    public Review publishReview(Long id) {
        logger.debug("GraphQL mutation: publishReview(id: {})", id);
        
        return reviewService.publishReview(id);
    }

    /**
     * Reject review
     */
    public Review rejectReview(Long id, String reason) {
        logger.debug("GraphQL mutation: rejectReview(id: {}, reason: {})", id, reason);
        
        return reviewService.rejectReview(id, reason);
    }

    /**
     * Add review response
     */
    public Review addReviewResponse(Long id, String responseText) {
        logger.debug("GraphQL mutation: addReviewResponse(id: {}, responseText: {})", id, responseText);
        
        return reviewService.addReviewResponse(id, responseText);
    }

    /**
     * Vote review helpful
     */
    public Review voteReviewHelpful(Long id) {
        logger.debug("GraphQL mutation: voteReviewHelpful(id: {})", id);
        
        return reviewService.voteReviewHelpful(id);
    }

    /**
     * Vote review not helpful
     */
    public Review voteReviewNotHelpful(Long id) {
        logger.debug("GraphQL mutation: voteReviewNotHelpful(id: {})", id);
        
        return reviewService.voteReviewNotHelpful(id);
    }

    /**
     * Report review
     */
    public Review reportReview(Long id, String reason) {
        logger.debug("GraphQL mutation: reportReview(id: {}, reason: {})", id, reason);
        
        return reviewService.reportReview(id, reason);
    }

    // Image Mutations

    /**
     * Upload image
     */
    public Image uploadImage(MultipartFile file, ImageCategory category, Long propertyId, Long userId, Long bookingId) {
        logger.debug("GraphQL mutation: uploadImage(category: {}, propertyId: {}, userId: {}, bookingId: {})", 
            category, propertyId, userId, bookingId);
        
        try {
            return imageService.uploadImage(file, category, propertyId, userId, bookingId);
        } catch (Exception e) {
            logger.error("Error uploading image: {}", e.getMessage());
            throw new RuntimeException("Image upload failed: " + e.getMessage());
        }
    }

    /**
     * Delete image
     */
    public Boolean deleteImage(Long id) {
        logger.debug("GraphQL mutation: deleteImage(id: {})", id);
        
        try {
            imageService.deleteImage(id);
            return true;
        } catch (Exception e) {
            logger.error("Error deleting image: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Update image
     */
    public Image updateImage(Long id, String altText, String caption) {
        logger.debug("GraphQL mutation: updateImage(id: {}, altText: {}, caption: {})", id, altText, caption);
        
        return imageService.updateImage(id, altText, caption);
    }

    /**
     * Set main image
     */
    public Image setMainImage(Long id) {
        logger.debug("GraphQL mutation: setMainImage(id: {})", id);
        
        return imageService.setMainImage(id);
    }
}




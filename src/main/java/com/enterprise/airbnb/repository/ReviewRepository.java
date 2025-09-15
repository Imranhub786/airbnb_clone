package com.enterprise.airbnb.repository;

import com.enterprise.airbnb.model.Review;
import com.enterprise.airbnb.model.ReviewType;
import com.enterprise.airbnb.model.ReviewStatus;
import com.enterprise.airbnb.model.Booking;
import com.enterprise.airbnb.model.Property;
import com.enterprise.airbnb.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Review entity operations
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    /**
     * Find reviews by booking
     */
    List<Review> findByBooking(Booking booking);
    
    /**
     * Find reviews by booking with pagination
     */
    Page<Review> findByBooking(Booking booking, Pageable pageable);
    
    /**
     * Find reviews by booking ID
     */
    List<Review> findByBookingId(Long bookingId);
    
    /**
     * Find reviews by booking ID with pagination
     */
    Page<Review> findByBookingId(Long bookingId, Pageable pageable);
    
    /**
     * Find reviews by property
     */
    List<Review> findByProperty(Property property);
    
    /**
     * Find reviews by property with pagination
     */
    Page<Review> findByProperty(Property property, Pageable pageable);
    
    /**
     * Find reviews by property ID
     */
    List<Review> findByPropertyId(Long propertyId);
    
    /**
     * Find reviews by property ID with pagination
     */
    Page<Review> findByPropertyId(Long propertyId, Pageable pageable);
    
    /**
     * Find reviews by reviewer
     */
    List<Review> findByReviewer(User reviewer);
    
    /**
     * Find reviews by reviewer with pagination
     */
    Page<Review> findByReviewer(User reviewer, Pageable pageable);
    
    /**
     * Find reviews by reviewer ID
     */
    List<Review> findByReviewerId(Long reviewerId);
    
    /**
     * Find reviews by reviewer ID with pagination
     */
    Page<Review> findByReviewerId(Long reviewerId, Pageable pageable);
    
    /**
     * Find reviews by reviewee
     */
    List<Review> findByReviewee(User reviewee);
    
    /**
     * Find reviews by reviewee with pagination
     */
    Page<Review> findByReviewee(User reviewee, Pageable pageable);
    
    /**
     * Find reviews by reviewee ID
     */
    List<Review> findByRevieweeId(Long revieweeId);
    
    /**
     * Find reviews by reviewee ID with pagination
     */
    Page<Review> findByRevieweeId(Long revieweeId, Pageable pageable);
    
    /**
     * Find reviews by review type
     */
    List<Review> findByReviewType(ReviewType reviewType);
    
    /**
     * Find reviews by review type with pagination
     */
    Page<Review> findByReviewType(ReviewType reviewType, Pageable pageable);
    
    /**
     * Find reviews by status
     */
    List<Review> findByStatus(ReviewStatus status);
    
    /**
     * Find reviews by status with pagination
     */
    Page<Review> findByStatus(ReviewStatus status, Pageable pageable);
    
    /**
     * Find reviews by rating
     */
    List<Review> findByRating(Integer rating);
    
    /**
     * Find reviews by rating with pagination
     */
    Page<Review> findByRating(Integer rating, Pageable pageable);
    
    /**
     * Find reviews by rating range
     */
    List<Review> findByRatingBetween(Integer minRating, Integer maxRating);
    
    /**
     * Find reviews by rating range with pagination
     */
    Page<Review> findByRatingBetween(Integer minRating, Integer maxRating, Pageable pageable);
    
    /**
     * Find reviews by property and status
     */
    List<Review> findByPropertyAndStatus(Property property, ReviewStatus status);
    
    /**
     * Find reviews by property and status with pagination
     */
    Page<Review> findByPropertyAndStatus(Property property, ReviewStatus status, Pageable pageable);
    
    /**
     * Find reviews by reviewer and status
     */
    List<Review> findByReviewerAndStatus(User reviewer, ReviewStatus status);
    
    /**
     * Find reviews by reviewer and status with pagination
     */
    Page<Review> findByReviewerAndStatus(User reviewer, ReviewStatus status, Pageable pageable);
    
    /**
     * Find reviews by reviewee and status
     */
    List<Review> findByRevieweeAndStatus(User reviewee, ReviewStatus status);
    
    /**
     * Find reviews by reviewee and status with pagination
     */
    Page<Review> findByRevieweeAndStatus(User reviewee, ReviewStatus status, Pageable pageable);
    
    /**
     * Find reviews by review type and status
     */
    List<Review> findByReviewTypeAndStatus(ReviewType reviewType, ReviewStatus status);
    
    /**
     * Find reviews by review type and status with pagination
     */
    Page<Review> findByReviewTypeAndStatus(ReviewType reviewType, ReviewStatus status, Pageable pageable);
    
    /**
     * Find reviews by property and review type
     */
    List<Review> findByPropertyAndReviewType(Property property, ReviewType reviewType);
    
    /**
     * Find reviews by property and review type with pagination
     */
    Page<Review> findByPropertyAndReviewType(Property property, ReviewType reviewType, Pageable pageable);
    
    /**
     * Find reviews by reviewer and review type
     */
    List<Review> findByReviewerAndReviewType(User reviewer, ReviewType reviewType);
    
    /**
     * Find reviews by reviewer and review type with pagination
     */
    Page<Review> findByReviewerAndReviewType(User reviewer, ReviewType reviewType, Pageable pageable);
    
    /**
     * Find reviews by reviewee and review type
     */
    List<Review> findByRevieweeAndReviewType(User reviewee, ReviewType reviewType);
    
    /**
     * Find reviews by reviewee and review type with pagination
     */
    Page<Review> findByRevieweeAndReviewType(User reviewee, ReviewType reviewType, Pageable pageable);
    
    /**
     * Find reviews by anonymous status
     */
    List<Review> findByIsAnonymous(Boolean isAnonymous);
    
    /**
     * Find reviews by anonymous status with pagination
     */
    Page<Review> findByIsAnonymous(Boolean isAnonymous, Pageable pageable);
    
    /**
     * Find reviews by verified stay status
     */
    List<Review> findByIsVerifiedStay(Boolean isVerifiedStay);
    
    /**
     * Find reviews by verified stay status with pagination
     */
    Page<Review> findByIsVerifiedStay(Boolean isVerifiedStay, Pageable pageable);
    
    /**
     * Find reviews by featured status
     */
    List<Review> findByIsFeatured(Boolean isFeatured);
    
    /**
     * Find reviews by featured status with pagination
     */
    Page<Review> findByIsFeatured(Boolean isFeatured, Pageable pageable);
    
    /**
     * Find reviews with responses
     */
    @Query("SELECT r FROM Review r WHERE r.responseText IS NOT NULL AND r.responseText != ''")
    List<Review> findReviewsWithResponses();
    
    /**
     * Find reviews with responses with pagination
     */
    @Query("SELECT r FROM Review r WHERE r.responseText IS NOT NULL AND r.responseText != ''")
    Page<Review> findReviewsWithResponses(Pageable pageable);
    
    /**
     * Find reviews without responses
     */
    @Query("SELECT r FROM Review r WHERE r.responseText IS NULL OR r.responseText = ''")
    List<Review> findReviewsWithoutResponses();
    
    /**
     * Find reviews without responses with pagination
     */
    @Query("SELECT r FROM Review r WHERE r.responseText IS NULL OR r.responseText = ''")
    Page<Review> findReviewsWithoutResponses(Pageable pageable);
    
    /**
     * Find reviews by helpful count range
     */
    List<Review> findByHelpfulCountBetween(Integer minHelpful, Integer maxHelpful);
    
    /**
     * Find reviews by helpful count range with pagination
     */
    Page<Review> findByHelpfulCountBetween(Integer minHelpful, Integer maxHelpful, Pageable pageable);
    
    /**
     * Find reviews by reported count
     */
    List<Review> findByReportedCountGreaterThan(Integer minReported);
    
    /**
     * Find reviews by reported count with pagination
     */
    Page<Review> findByReportedCountGreaterThan(Integer minReported, Pageable pageable);
    
    /**
     * Find reviews created after a specific date
     */
    List<Review> findByCreatedAtAfter(LocalDateTime date);
    
    /**
     * Find reviews created between dates
     */
    List<Review> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find reviews created between dates with pagination
     */
    Page<Review> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    /**
     * Find reviews with response date after a specific date
     */
    List<Review> findByResponseDateAfter(LocalDateTime date);
    
    /**
     * Find reviews with response date between dates
     */
    List<Review> findByResponseDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find reviews with response date between dates with pagination
     */
    Page<Review> findByResponseDateBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    /**
     * Find reviews moderated after a specific date
     */
    List<Review> findByModeratedAtAfter(LocalDateTime date);
    
    /**
     * Find reviews moderated between dates
     */
    List<Review> findByModeratedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find reviews moderated between dates with pagination
     */
    Page<Review> findByModeratedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    /**
     * Find reviews by moderator
     */
    List<Review> findByModeratedBy(String moderatedBy);
    
    /**
     * Find reviews by moderator with pagination
     */
    Page<Review> findByModeratedBy(String moderatedBy, Pageable pageable);
    
    /**
     * Find published reviews
     */
    @Query("SELECT r FROM Review r WHERE r.status = 'PUBLISHED'")
    List<Review> findPublishedReviews();
    
    /**
     * Find published reviews with pagination
     */
    @Query("SELECT r FROM Review r WHERE r.status = 'PUBLISHED'")
    Page<Review> findPublishedReviews(Pageable pageable);
    
    /**
     * Find pending reviews
     */
    @Query("SELECT r FROM Review r WHERE r.status = 'PENDING'")
    List<Review> findPendingReviews();
    
    /**
     * Find pending reviews with pagination
     */
    @Query("SELECT r FROM Review r WHERE r.status = 'PENDING'")
    Page<Review> findPendingReviews(Pageable pageable);
    
    /**
     * Find rejected reviews
     */
    @Query("SELECT r FROM Review r WHERE r.status = 'REJECTED'")
    List<Review> findRejectedReviews();
    
    /**
     * Find rejected reviews with pagination
     */
    @Query("SELECT r FROM Review r WHERE r.status = 'REJECTED'")
    Page<Review> findRejectedReviews(Pageable pageable);
    
    /**
     * Find reviews by property and rating
     */
    List<Review> findByPropertyAndRating(Property property, Integer rating);
    
    /**
     * Find reviews by property and rating with pagination
     */
    Page<Review> findByPropertyAndRating(Property property, Integer rating, Pageable pageable);
    
    /**
     * Find reviews by property and rating range
     */
    List<Review> findByPropertyAndRatingBetween(Property property, Integer minRating, Integer maxRating);
    
    /**
     * Find reviews by property and rating range with pagination
     */
    Page<Review> findByPropertyAndRatingBetween(Property property, Integer minRating, Integer maxRating, Pageable pageable);
    
    /**
     * Find reviews by reviewer and rating
     */
    List<Review> findByReviewerAndRating(User reviewer, Integer rating);
    
    /**
     * Find reviews by reviewer and rating with pagination
     */
    Page<Review> findByReviewerAndRating(User reviewer, Integer rating, Pageable pageable);
    
    /**
     * Find reviews by reviewee and rating
     */
    List<Review> findByRevieweeAndRating(User reviewee, Integer rating);
    
    /**
     * Find reviews by reviewee and rating with pagination
     */
    Page<Review> findByRevieweeAndRating(User reviewee, Integer rating, Pageable pageable);
    
    /**
     * Find reviews by review text containing (case insensitive)
     */
    List<Review> findByReviewTextContainingIgnoreCase(String reviewText);
    
    /**
     * Find reviews by review text containing with pagination
     */
    Page<Review> findByReviewTextContainingIgnoreCase(String reviewText, Pageable pageable);
    
    /**
     * Find reviews by response text containing (case insensitive)
     */
    List<Review> findByResponseTextContainingIgnoreCase(String responseText);
    
    /**
     * Find reviews by response text containing with pagination
     */
    Page<Review> findByResponseTextContainingIgnoreCase(String responseText, Pageable pageable);
    
    /**
     * Find reviews by moderation notes containing (case insensitive)
     */
    List<Review> findByModerationNotesContainingIgnoreCase(String moderationNotes);
    
    /**
     * Find reviews by moderation notes containing with pagination
     */
    Page<Review> findByModerationNotesContainingIgnoreCase(String moderationNotes, Pageable pageable);
    
    /**
     * Find reviews by booking and review type
     */
    List<Review> findByBookingAndReviewType(Booking booking, ReviewType reviewType);
    
    /**
     * Find reviews by booking and review type with pagination
     */
    Page<Review> findByBookingAndReviewType(Booking booking, ReviewType reviewType, Pageable pageable);
    
    /**
     * Find reviews by booking and status
     */
    List<Review> findByBookingAndStatus(Booking booking, ReviewStatus status);
    
    /**
     * Find reviews by booking and status with pagination
     */
    Page<Review> findByBookingAndStatus(Booking booking, ReviewStatus status, Pageable pageable);
    
    /**
     * Find reviews by booking and rating
     */
    List<Review> findByBookingAndRating(Booking booking, Integer rating);
    
    /**
     * Find reviews by booking and rating with pagination
     */
    Page<Review> findByBookingAndRating(Booking booking, Integer rating, Pageable pageable);
    
    /**
     * Count reviews by property
     */
    long countByProperty(Property property);
    
    /**
     * Count reviews by reviewer
     */
    long countByReviewer(User reviewer);
    
    /**
     * Count reviews by reviewee
     */
    long countByReviewee(User reviewee);
    
    /**
     * Count reviews by review type
     */
    long countByReviewType(ReviewType reviewType);
    
    /**
     * Count reviews by status
     */
    long countByStatus(ReviewStatus status);
    
    /**
     * Count reviews by rating
     */
    long countByRating(Integer rating);
    
    /**
     * Count reviews by property and status
     */
    long countByPropertyAndStatus(Property property, ReviewStatus status);
    
    /**
     * Count reviews by reviewer and status
     */
    long countByReviewerAndStatus(User reviewer, ReviewStatus status);
    
    /**
     * Count reviews by reviewee and status
     */
    long countByRevieweeAndStatus(User reviewee, ReviewStatus status);
    
    /**
     * Count reviews by review type and status
     */
    long countByReviewTypeAndStatus(ReviewType reviewType, ReviewStatus status);
    
    /**
     * Count reviews by property and review type
     */
    long countByPropertyAndReviewType(Property property, ReviewType reviewType);
    
    /**
     * Count reviews by reviewer and review type
     */
    long countByReviewerAndReviewType(User reviewer, ReviewType reviewType);
    
    /**
     * Count reviews by reviewee and review type
     */
    long countByRevieweeAndReviewType(User reviewee, ReviewType reviewType);
    
    /**
     * Count reviews by booking
     */
    long countByBooking(Booking booking);
    
    /**
     * Count reviews by booking and review type
     */
    long countByBookingAndReviewType(Booking booking, ReviewType reviewType);
    
    /**
     * Count reviews by booking and status
     */
    long countByBookingAndStatus(Booking booking, ReviewStatus status);
    
    /**
     * Count reviews created after a specific date
     */
    long countByCreatedAtAfter(LocalDateTime date);
    
    /**
     * Count reviews by anonymous status
     */
    long countByIsAnonymous(Boolean isAnonymous);
    
    /**
     * Count reviews by verified stay status
     */
    long countByIsVerifiedStay(Boolean isVerifiedStay);
    
    /**
     * Count reviews by featured status
     */
    long countByIsFeatured(Boolean isFeatured);
    
    /**
     * Count reviews with responses
     */
    @Query("SELECT COUNT(r) FROM Review r WHERE r.responseText IS NOT NULL AND r.responseText != ''")
    long countReviewsWithResponses();
    
    /**
     * Count reviews without responses
     */
    @Query("SELECT COUNT(r) FROM Review r WHERE r.responseText IS NULL OR r.responseText = ''")
    long countReviewsWithoutResponses();
    
    /**
     * Count reviews by reported count
     */
    long countByReportedCountGreaterThan(Integer minReported);
    
    /**
     * Calculate average rating by property
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.property = :property AND r.status = 'PUBLISHED'")
    Double calculateAverageRatingByProperty(@Param("property") Property property);
    
    /**
     * Calculate average rating by reviewer
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.reviewer = :reviewer AND r.status = 'PUBLISHED'")
    Double calculateAverageRatingByReviewer(@Param("reviewer") User reviewer);
    
    /**
     * Calculate average rating by reviewee
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.reviewee = :reviewee AND r.status = 'PUBLISHED'")
    Double calculateAverageRatingByReviewee(@Param("reviewee") User reviewee);
    
    /**
     * Calculate average rating by review type
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.reviewType = :reviewType AND r.status = 'PUBLISHED'")
    Double calculateAverageRatingByReviewType(@Param("reviewType") ReviewType reviewType);
    
    /**
     * Calculate average rating by property and review type
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.property = :property AND r.reviewType = :reviewType AND r.status = 'PUBLISHED'")
    Double calculateAverageRatingByPropertyAndReviewType(@Param("property") Property property, @Param("reviewType") ReviewType reviewType);
    
    /**
     * Check if review exists by booking and reviewer
     */
    boolean existsByBookingAndReviewer(Booking booking, User reviewer);
    
    /**
     * Check if review exists by booking and review type
     */
    boolean existsByBookingAndReviewType(Booking booking, ReviewType reviewType);
    
    /**
     * Check if review exists by booking, reviewer and review type
     */
    boolean existsByBookingAndReviewerAndReviewType(Booking booking, User reviewer, ReviewType reviewType);
    
    // Missing methods for compilation
    
    List<Review> findByGuestId(Long guestId);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.property.id = :propertyId AND r.reviewStatus = 'PUBLISHED'")
    Double findAverageRatingByPropertyId(@Param("propertyId") Long propertyId);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.property.id = :propertyId AND r.reviewStatus = 'PUBLISHED'")
    Long countByPropertyId(@Param("propertyId") Long propertyId);
    
    // Additional missing methods
    Page<Review> findByType(ReviewType reviewType, Pageable pageable);
    Page<Review> findByRatingRange(Integer minRating, Integer maxRating, Pageable pageable);
    Double getPropertyAverageRating(Long propertyId);
    List<Object> getPropertyRatingDistribution(Long propertyId);
    Double getUserAverageRating(Long userId);
    List<Object> getUserRatingDistribution(Long userId);
    Page<Review> findRecentReviews(Pageable pageable);
    List<Object> getTopRatedProperties(int limit);
    List<Object> getTopRatedHosts(int limit);
    Page<Review> findReviewsWithPhotos(Pageable pageable);
    Page<Review> findReviewsWithoutPhotos(Pageable pageable);
    Page<Review> searchReviews(String searchTerm, Pageable pageable);
    Page<Review> searchReviewsByComment(String searchTerm, Pageable pageable);
    Page<Review> searchReviewsByResponse(String searchTerm, Pageable pageable);
    List<Object> getReviewStatistics();
    List<Object> getReviewTrends();
    List<Object> getReviewAnalytics();
    List<Object> exportReviewsToCsv();
    Page<Review> getReviewModerationQueue(Pageable pageable);
    Page<Review> findByHostId(Long hostId, Pageable pageable);
}


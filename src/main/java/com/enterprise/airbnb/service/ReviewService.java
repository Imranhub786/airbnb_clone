package com.enterprise.airbnb.service;

import com.enterprise.airbnb.model.Review;
import com.enterprise.airbnb.model.ReviewType;
import com.enterprise.airbnb.model.ReviewStatus;
import com.enterprise.airbnb.repository.ReviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service for Review operations
 */
@Service
@Transactional
public class ReviewService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);

    @Autowired
    private ReviewRepository reviewRepository;

    /**
     * Get all reviews with pagination
     */
    @Cacheable(value = "reviews", key = "#pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Review> getAllReviews(Pageable pageable) {
        logger.info("Fetching all reviews with pagination: {}", pageable);
        return reviewRepository.findAll(pageable);
    }

    /**
     * Get review by ID
     */
    @Cacheable(value = "reviews", key = "#id")
    public Optional<Review> getReviewById(Long id) {
        logger.info("Fetching review by ID: {}", id);
        return reviewRepository.findById(id);
    }

    /**
     * Create new review
     */
    @CacheEvict(value = {"reviews", "properties", "users"}, allEntries = true)
    public Review createReview(Review review) {
        logger.info("Creating new review for property ID: {}", review.getProperty().getId());
        return reviewRepository.save(review);
    }

    /**
     * Update review
     */
    @CacheEvict(value = {"reviews", "properties", "users"}, allEntries = true)
    public Review updateReview(Review review) {
        logger.info("Updating review with ID: {}", review.getId());
        return reviewRepository.save(review);
    }

    /**
     * Delete review
     */
    @CacheEvict(value = {"reviews", "properties", "users"}, allEntries = true)
    public void deleteReview(Long id) {
        logger.info("Deleting review with ID: {}", id);
        reviewRepository.deleteById(id);
    }

    /**
     * Get reviews by property
     */
    @Cacheable(value = "reviews", key = "'property_' + #propertyId + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Review> getReviewsByPropertyId(Long propertyId, Pageable pageable) {
        logger.info("Fetching reviews by property ID: {}", propertyId);
        return reviewRepository.findByPropertyId(propertyId, pageable);
    }

    /**
     * Get reviews by reviewer
     */
    @Cacheable(value = "reviews", key = "'reviewer_' + #reviewerId + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Review> getReviewsByReviewerId(Long reviewerId, Pageable pageable) {
        logger.info("Fetching reviews by reviewer ID: {}", reviewerId);
        return reviewRepository.findByReviewerId(reviewerId, pageable);
    }

    /**
     * Get reviews by reviewee
     */
    @Cacheable(value = "reviews", key = "'reviewee_' + #revieweeId + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Review> getReviewsByRevieweeId(Long revieweeId, Pageable pageable) {
        logger.info("Fetching reviews by reviewee ID: {}", revieweeId);
        return reviewRepository.findByRevieweeId(revieweeId, pageable);
    }

    /**
     * Get reviews by type
     */
    @Cacheable(value = "reviews", key = "'type_' + #type + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Review> getReviewsByType(ReviewType type, Pageable pageable) {
        logger.info("Fetching reviews by type: {}", type);
        return reviewRepository.findByType(type, pageable);
    }

    /**
     * Get reviews by status
     */
    @Cacheable(value = "reviews", key = "'status_' + #status + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Review> getReviewsByStatus(ReviewStatus status, Pageable pageable) {
        logger.info("Fetching reviews by status: {}", status);
        return reviewRepository.findByStatus(status, pageable);
    }

    /**
     * Get reviews by rating
     */
    @Cacheable(value = "reviews", key = "'rating_' + #rating + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Review> getReviewsByRating(Integer rating, Pageable pageable) {
        logger.info("Fetching reviews by rating: {}", rating);
        return reviewRepository.findByRating(rating, pageable);
    }

    /**
     * Get reviews by rating range
     */
    @Cacheable(value = "reviews", key = "'ratingRange_' + #minRating + '_' + #maxRating + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Review> getReviewsByRatingRange(Integer minRating, Integer maxRating, Pageable pageable) {
        logger.info("Fetching reviews by rating range: {} to {}", minRating, maxRating);
        return reviewRepository.findByRatingRange(minRating, maxRating, pageable);
    }

    /**
     * Get property average rating
     */
    @Cacheable(value = "properties", key = "'avgRating_' + #propertyId")
    public double getPropertyAverageRating(Long propertyId) {
        logger.info("Fetching average rating for property ID: {}", propertyId);
        return reviewRepository.getPropertyAverageRating(propertyId);
    }

    /**
     * Get property rating distribution
     */
    @Cacheable(value = "properties", key = "'ratingDist_' + #propertyId")
    public Object getPropertyRatingDistribution(Long propertyId) {
        logger.info("Fetching rating distribution for property ID: {}", propertyId);
        return reviewRepository.getPropertyRatingDistribution(propertyId);
    }

    /**
     * Get user average rating
     */
    @Cacheable(value = "users", key = "'avgRating_' + #userId")
    public double getUserAverageRating(Long userId) {
        logger.info("Fetching average rating for user ID: {}", userId);
        return reviewRepository.getUserAverageRating(userId);
    }

    /**
     * Get user rating distribution
     */
    @Cacheable(value = "users", key = "'ratingDist_' + #userId")
    public Object getUserRatingDistribution(Long userId) {
        logger.info("Fetching rating distribution for user ID: {}", userId);
        return reviewRepository.getUserRatingDistribution(userId);
    }

    /**
     * Get recent reviews
     */
    @Cacheable(value = "reviews", key = "'recent_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Review> getRecentReviews(Pageable pageable) {
        logger.info("Fetching recent reviews");
        return reviewRepository.findRecentReviews(pageable);
    }

    /**
     * Get top rated properties
     */
    @Cacheable(value = "properties", key = "'topRated_' + #limit")
    public List<Object> getTopRatedProperties(int limit) {
        logger.info("Fetching top rated properties with limit: {}", limit);
        return reviewRepository.getTopRatedProperties(limit);
    }

    /**
     * Get top rated hosts
     */
    @Cacheable(value = "users", key = "'topRatedHosts_' + #limit")
    public List<Object> getTopRatedHosts(int limit) {
        logger.info("Fetching top rated hosts with limit: {}", limit);
        return reviewRepository.getTopRatedHosts(limit);
    }

    /**
     * Get reviews with photos
     */
    @Cacheable(value = "reviews", key = "'withPhotos_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Review> getReviewsWithPhotos(Pageable pageable) {
        logger.info("Fetching reviews with photos");
        return reviewRepository.findReviewsWithPhotos(pageable);
    }

    /**
     * Get reviews without photos
     */
    @Cacheable(value = "reviews", key = "'withoutPhotos_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Review> getReviewsWithoutPhotos(Pageable pageable) {
        logger.info("Fetching reviews without photos");
        return reviewRepository.findReviewsWithoutPhotos(pageable);
    }

    /**
     * Search reviews by keyword
     */
    @Cacheable(value = "reviews", key = "'search_' + #keyword + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Review> searchReviews(String keyword, Pageable pageable) {
        logger.info("Searching reviews with keyword: {}", keyword);
        return reviewRepository.searchReviews(keyword, pageable);
    }

    /**
     * Search reviews by comment
     */
    @Cacheable(value = "reviews", key = "'searchComment_' + #comment + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Review> searchReviewsByComment(String comment, Pageable pageable) {
        logger.info("Searching reviews by comment: {}", comment);
        return reviewRepository.searchReviewsByComment(comment, pageable);
    }

    /**
     * Search reviews by response
     */
    @Cacheable(value = "reviews", key = "'searchResponse_' + #response + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Review> searchReviewsByResponse(String response, Pageable pageable) {
        logger.info("Searching reviews by response: {}", response);
        return reviewRepository.searchReviewsByResponse(response, pageable);
    }

    /**
     * Get reviews with responses
     */
    @Cacheable(value = "reviews", key = "'withResponses_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Review> getReviewsWithResponses(Pageable pageable) {
        logger.info("Fetching reviews with responses");
        return reviewRepository.findReviewsWithResponses(pageable);
    }

    /**
     * Get reviews without responses
     */
    @Cacheable(value = "reviews", key = "'withoutResponses_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Review> getReviewsWithoutResponses(Pageable pageable) {
        logger.info("Fetching reviews without responses");
        return reviewRepository.findReviewsWithoutResponses(pageable);
    }

    /**
     * Add response to review
     */
    @CacheEvict(value = {"reviews"}, allEntries = true)
    public void addResponseToReview(Long id, String response) {
        logger.info("Adding response to review ID: {}", id);
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        review.setResponseText(response);
        reviewRepository.save(review);
    }

    /**
     * Update response to review
     */
    @CacheEvict(value = {"reviews"}, allEntries = true)
    public void updateResponseToReview(Long id, String response) {
        logger.info("Updating response to review ID: {}", id);
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        review.setResponseText(response);
        reviewRepository.save(review);
    }

    /**
     * Delete response from review
     */
    @CacheEvict(value = {"reviews"}, allEntries = true)
    public void deleteResponseFromReview(Long id) {
        logger.info("Deleting response from review ID: {}", id);
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        review.setResponseText(null);
        reviewRepository.save(review);
    }

    /**
     * Report review
     */
    @CacheEvict(value = {"reviews"}, allEntries = true)
    public void reportReview(Long id, String reason) {
        logger.info("Reporting review ID: {} with reason: {}", id, reason);
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        review.setStatus(ReviewStatus.REPORTED);
        reviewRepository.save(review);
    }

    /**
     * Get reported reviews
     */
    @Cacheable(value = "reviews", key = "'reported_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Review> getReportedReviews(Pageable pageable) {
        logger.info("Fetching reported reviews");
        return reviewRepository.findByStatus(ReviewStatus.REPORTED, pageable);
    }

    /**
     * Resolve review report
     */
    @CacheEvict(value = {"reviews"}, allEntries = true)
    public void resolveReviewReport(Long id) {
        logger.info("Resolving report for review ID: {}", id);
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        review.setStatus(ReviewStatus.APPROVED);
        reviewRepository.save(review);
    }

    /**
     * Get review statistics
     */
    @Cacheable(value = "reviews", key = "'statistics'")
    public Object getReviewStatistics() {
        logger.info("Fetching review statistics");
        return reviewRepository.getReviewStatistics();
    }

    /**
     * Get review trends
     */
    @Cacheable(value = "reviews", key = "'trends'")
    public Object getReviewTrends() {
        logger.info("Fetching review trends");
        return reviewRepository.getReviewTrends();
    }

    /**
     * Get review analytics
     */
    @Cacheable(value = "reviews", key = "'analytics'")
    public Object getReviewAnalytics() {
        logger.info("Fetching review analytics");
        return reviewRepository.getReviewAnalytics();
    }

    /**
     * Export reviews to CSV
     */
    public String exportReviewsToCsv() {
        logger.info("Exporting reviews to CSV");
        return reviewRepository.exportReviewsToCsv();
    }

    /**
     * Get review moderation queue
     */
    @Cacheable(value = "reviews", key = "'moderationQueue_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Review> getReviewModerationQueue(Pageable pageable) {
        logger.info("Fetching review moderation queue");
        return reviewRepository.getReviewModerationQueue(pageable);
    }

    /**
     * Moderate review
     */
    @CacheEvict(value = {"reviews"}, allEntries = true)
    public void moderateReview(Long id, ReviewStatus status) {
        logger.info("Moderating review ID: {} with status: {}", id, status);
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        review.setStatus(status);
        reviewRepository.save(review);
    }

    /**
     * Get reviews by property ID with pagination
     */
    public List<Review> getReviewsByProperty(Long propertyId, Pageable pageable) {
        logger.debug("Fetching reviews by property ID: {} with pagination", propertyId);
        return getReviewsByPropertyId(propertyId, pageable).getContent();
    }

    /**
     * Get reviews by user ID with pagination
     */
    public List<Review> getReviewsByUser(Long userId, Pageable pageable) {
        logger.debug("Fetching reviews by user ID: {} with pagination", userId);
        return getReviewsByUserId(userId, pageable).getContent();
    }

    /**
     * Get reviews by booking ID
     */
    public List<Review> getReviewsByBooking(Long bookingId) {
        logger.debug("Fetching reviews by booking ID: {}", bookingId);
        return getReviewsByBookingId(bookingId, PageRequest.of(0, 100)).getContent();
    }
    
    /**
     * Get reviews by user ID with pagination
     */
    public Page<Review> getReviewsByUserId(Long userId, Pageable pageable) {
        logger.debug("Fetching reviews by user ID: {} with pagination", userId);
        return reviewRepository.findByReviewerId(userId, pageable);
    }
    
    /**
     * Get reviews by booking ID with pagination
     */
    public Page<Review> getReviewsByBookingId(Long bookingId, PageRequest pageRequest) {
        logger.debug("Fetching reviews by booking ID: {} with pagination", bookingId);
        return reviewRepository.findByBookingId(bookingId, pageRequest);
    }
}


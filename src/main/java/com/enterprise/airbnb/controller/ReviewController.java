package com.enterprise.airbnb.controller;

import com.enterprise.airbnb.model.Review;
import com.enterprise.airbnb.model.ReviewType;
import com.enterprise.airbnb.model.ReviewStatus;
import com.enterprise.airbnb.service.ReviewService;
import com.enterprise.airbnb.util.JwtRequestFilter;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Review operations
 */
@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {

    private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    /**
     * Get all reviews with pagination
     */
    @GetMapping
    public ResponseEntity<Page<Review>> getAllReviews(Pageable pageable) {
        logger.info("Fetching all reviews with pagination: {}", pageable);
        Page<Review> reviews = reviewService.getAllReviews(pageable);
        return ResponseEntity.ok(reviews);
    }

    /**
     * Get review by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable Long id) {
        logger.info("Fetching review by ID: {}", id);
        Optional<Review> review = reviewService.getReviewById(id);
        return review.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create new review
     */
    @PostMapping
    @PreAuthorize("hasRole('GUEST') or hasRole('HOST') or hasRole('SUPER_HOST') or hasRole('ADMIN')")
    public ResponseEntity<Review> createReview(@Valid @RequestBody Review review) {
        logger.info("Creating new review for property ID: {}", review.getProperty().getId());
        try {
            Review createdReview = reviewService.createReview(review);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
        } catch (Exception e) {
            logger.error("Error creating review: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Update review
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GUEST') or hasRole('HOST') or hasRole('SUPER_HOST') or hasRole('ADMIN')")
    public ResponseEntity<Review> updateReview(@PathVariable Long id, @Valid @RequestBody Review review) {
        logger.info("Updating review with ID: {}", id);
        try {
            review.setId(id);
            Review updatedReview = reviewService.updateReview(review);
            return ResponseEntity.ok(updatedReview);
        } catch (Exception e) {
            logger.error("Error updating review: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Delete review
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GUEST') or hasRole('HOST') or hasRole('SUPER_HOST') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        logger.info("Deleting review with ID: {}", id);
        try {
            reviewService.deleteReview(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting review: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get reviews by property
     */
    @GetMapping("/property/{propertyId}")
    public ResponseEntity<Page<Review>> getReviewsByProperty(@PathVariable Long propertyId, Pageable pageable) {
        logger.info("Fetching reviews by property ID: {}", propertyId);
        Page<Review> reviews = reviewService.getReviewsByPropertyId(propertyId, pageable);
        return ResponseEntity.ok(reviews);
    }

    /**
     * Get reviews by reviewer
     */
    @GetMapping("/reviewer/{reviewerId}")
    public ResponseEntity<Page<Review>> getReviewsByReviewer(@PathVariable Long reviewerId, Pageable pageable) {
        logger.info("Fetching reviews by reviewer ID: {}", reviewerId);
        Page<Review> reviews = reviewService.getReviewsByReviewerId(reviewerId, pageable);
        return ResponseEntity.ok(reviews);
    }

    /**
     * Get reviews by reviewee
     */
    @GetMapping("/reviewee/{revieweeId}")
    public ResponseEntity<Page<Review>> getReviewsByReviewee(@PathVariable Long revieweeId, Pageable pageable) {
        logger.info("Fetching reviews by reviewee ID: {}", revieweeId);
        Page<Review> reviews = reviewService.getReviewsByRevieweeId(revieweeId, pageable);
        return ResponseEntity.ok(reviews);
    }

    /**
     * Get reviews by type
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<Page<Review>> getReviewsByType(@PathVariable ReviewType type, Pageable pageable) {
        logger.info("Fetching reviews by type: {}", type);
        Page<Review> reviews = reviewService.getReviewsByType(type, pageable);
        return ResponseEntity.ok(reviews);
    }

    /**
     * Get reviews by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<Review>> getReviewsByStatus(@PathVariable ReviewStatus status, Pageable pageable) {
        logger.info("Fetching reviews by status: {}", status);
        Page<Review> reviews = reviewService.getReviewsByStatus(status, pageable);
        return ResponseEntity.ok(reviews);
    }

    /**
     * Get reviews by rating
     */
    @GetMapping("/rating/{rating}")
    public ResponseEntity<Page<Review>> getReviewsByRating(@PathVariable Integer rating, Pageable pageable) {
        logger.info("Fetching reviews by rating: {}", rating);
        Page<Review> reviews = reviewService.getReviewsByRating(rating, pageable);
        return ResponseEntity.ok(reviews);
    }

    /**
     * Get reviews by rating range
     */
    @GetMapping("/rating-range")
    public ResponseEntity<Page<Review>> getReviewsByRatingRange(
            @RequestParam Integer minRating,
            @RequestParam Integer maxRating,
            Pageable pageable) {
        
        logger.info("Fetching reviews by rating range: {} to {}", minRating, maxRating);
        Page<Review> reviews = reviewService.getReviewsByRatingRange(minRating, maxRating, pageable);
        return ResponseEntity.ok(reviews);
    }

    /**
     * Get property average rating
     */
    @GetMapping("/property/{propertyId}/average-rating")
    public ResponseEntity<Double> getPropertyAverageRating(@PathVariable Long propertyId) {
        logger.info("Fetching average rating for property ID: {}", propertyId);
        double averageRating = reviewService.getPropertyAverageRating(propertyId);
        return ResponseEntity.ok(averageRating);
    }

    /**
     * Get property rating distribution
     */
    @GetMapping("/property/{propertyId}/rating-distribution")
    public ResponseEntity<Object> getPropertyRatingDistribution(@PathVariable Long propertyId) {
        logger.info("Fetching rating distribution for property ID: {}", propertyId);
        Object distribution = reviewService.getPropertyRatingDistribution(propertyId);
        return ResponseEntity.ok(distribution);
    }

    /**
     * Get user average rating
     */
    @GetMapping("/user/{userId}/average-rating")
    public ResponseEntity<Double> getUserAverageRating(@PathVariable Long userId) {
        logger.info("Fetching average rating for user ID: {}", userId);
        double averageRating = reviewService.getUserAverageRating(userId);
        return ResponseEntity.ok(averageRating);
    }

    /**
     * Get user rating distribution
     */
    @GetMapping("/user/{userId}/rating-distribution")
    public ResponseEntity<Object> getUserRatingDistribution(@PathVariable Long userId) {
        logger.info("Fetching rating distribution for user ID: {}", userId);
        Object distribution = reviewService.getUserRatingDistribution(userId);
        return ResponseEntity.ok(distribution);
    }

    /**
     * Get recent reviews
     */
    @GetMapping("/recent")
    public ResponseEntity<Page<Review>> getRecentReviews(Pageable pageable) {
        logger.info("Fetching recent reviews");
        Page<Review> reviews = reviewService.getRecentReviews(pageable);
        return ResponseEntity.ok(reviews);
    }

    /**
     * Get top rated properties
     */
    @GetMapping("/top-rated-properties")
    public ResponseEntity<List<Object>> getTopRatedProperties(@RequestParam(defaultValue = "10") int limit) {
        logger.info("Fetching top rated properties with limit: {}", limit);
        List<Object> topProperties = reviewService.getTopRatedProperties(limit);
        return ResponseEntity.ok(topProperties);
    }

    /**
     * Get top rated hosts
     */
    @GetMapping("/top-rated-hosts")
    public ResponseEntity<List<Object>> getTopRatedHosts(@RequestParam(defaultValue = "10") int limit) {
        logger.info("Fetching top rated hosts with limit: {}", limit);
        List<Object> topHosts = reviewService.getTopRatedHosts(limit);
        return ResponseEntity.ok(topHosts);
    }

    /**
     * Get reviews with photos
     */
    @GetMapping("/with-photos")
    public ResponseEntity<Page<Review>> getReviewsWithPhotos(Pageable pageable) {
        logger.info("Fetching reviews with photos");
        Page<Review> reviews = reviewService.getReviewsWithPhotos(pageable);
        return ResponseEntity.ok(reviews);
    }

    /**
     * Get reviews without photos
     */
    @GetMapping("/without-photos")
    public ResponseEntity<Page<Review>> getReviewsWithoutPhotos(Pageable pageable) {
        logger.info("Fetching reviews without photos");
        Page<Review> reviews = reviewService.getReviewsWithoutPhotos(pageable);
        return ResponseEntity.ok(reviews);
    }

    /**
     * Get reviews by keyword
     */
    @GetMapping("/search")
    public ResponseEntity<Page<Review>> searchReviews(@RequestParam String keyword, Pageable pageable) {
        logger.info("Searching reviews with keyword: {}", keyword);
        Page<Review> reviews = reviewService.searchReviews(keyword, pageable);
        return ResponseEntity.ok(reviews);
    }

    /**
     * Get reviews by comment
     */
    @GetMapping("/search/comment")
    public ResponseEntity<Page<Review>> searchReviewsByComment(@RequestParam String comment, Pageable pageable) {
        logger.info("Searching reviews by comment: {}", comment);
        Page<Review> reviews = reviewService.searchReviewsByComment(comment, pageable);
        return ResponseEntity.ok(reviews);
    }

    /**
     * Get reviews by response
     */
    @GetMapping("/search/response")
    public ResponseEntity<Page<Review>> searchReviewsByResponse(@RequestParam String response, Pageable pageable) {
        logger.info("Searching reviews by response: {}", response);
        Page<Review> reviews = reviewService.searchReviewsByResponse(response, pageable);
        return ResponseEntity.ok(reviews);
    }

    /**
     * Get reviews with responses
     */
    @GetMapping("/with-responses")
    public ResponseEntity<Page<Review>> getReviewsWithResponses(Pageable pageable) {
        logger.info("Fetching reviews with responses");
        Page<Review> reviews = reviewService.getReviewsWithResponses(pageable);
        return ResponseEntity.ok(reviews);
    }

    /**
     * Get reviews without responses
     */
    @GetMapping("/without-responses")
    public ResponseEntity<Page<Review>> getReviewsWithoutResponses(Pageable pageable) {
        logger.info("Fetching reviews without responses");
        Page<Review> reviews = reviewService.getReviewsWithoutResponses(pageable);
        return ResponseEntity.ok(reviews);
    }

    /**
     * Add response to review
     */
    @PostMapping("/{id}/response")
    @PreAuthorize("hasRole('HOST') or hasRole('SUPER_HOST') or hasRole('ADMIN')")
    public ResponseEntity<Void> addResponseToReview(@PathVariable Long id, @RequestParam String response) {
        logger.info("Adding response to review ID: {}", id);
        try {
            reviewService.addResponseToReview(id, response);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error adding response to review: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Update response to review
     */
    @PutMapping("/{id}/response")
    @PreAuthorize("hasRole('HOST') or hasRole('SUPER_HOST') or hasRole('ADMIN')")
    public ResponseEntity<Void> updateResponseToReview(@PathVariable Long id, @RequestParam String response) {
        logger.info("Updating response to review ID: {}", id);
        try {
            reviewService.updateResponseToReview(id, response);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error updating response to review: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Delete response from review
     */
    @DeleteMapping("/{id}/response")
    @PreAuthorize("hasRole('HOST') or hasRole('SUPER_HOST') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteResponseFromReview(@PathVariable Long id) {
        logger.info("Deleting response from review ID: {}", id);
        try {
            reviewService.deleteResponseFromReview(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error deleting response from review: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Report review
     */
    @PostMapping("/{id}/report")
    @PreAuthorize("hasRole('GUEST') or hasRole('HOST') or hasRole('SUPER_HOST') or hasRole('ADMIN')")
    public ResponseEntity<Void> reportReview(@PathVariable Long id, @RequestParam String reason) {
        logger.info("Reporting review ID: {} with reason: {}", id, reason);
        try {
            reviewService.reportReview(id, reason);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error reporting review: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get reported reviews
     */
    @GetMapping("/reported")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<Review>> getReportedReviews(Pageable pageable) {
        logger.info("Fetching reported reviews");
        Page<Review> reviews = reviewService.getReportedReviews(pageable);
        return ResponseEntity.ok(reviews);
    }

    /**
     * Resolve review report
     */
    @PatchMapping("/{id}/resolve-report")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> resolveReviewReport(@PathVariable Long id) {
        logger.info("Resolving report for review ID: {}", id);
        try {
            reviewService.resolveReviewReport(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error resolving review report: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get review statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Object> getReviewStatistics() {
        logger.info("Fetching review statistics");
        Object statistics = reviewService.getReviewStatistics();
        return ResponseEntity.ok(statistics);
    }

    /**
     * Get review trends
     */
    @GetMapping("/trends")
    public ResponseEntity<Object> getReviewTrends() {
        logger.info("Fetching review trends");
        Object trends = reviewService.getReviewTrends();
        return ResponseEntity.ok(trends);
    }

    /**
     * Get review analytics
     */
    @GetMapping("/analytics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> getReviewAnalytics() {
        logger.info("Fetching review analytics");
        Object analytics = reviewService.getReviewAnalytics();
        return ResponseEntity.ok(analytics);
    }

    /**
     * Export reviews to CSV
     */
    @GetMapping("/export/csv")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> exportReviewsToCsv() {
        logger.info("Exporting reviews to CSV");
        try {
            String csvData = reviewService.exportReviewsToCsv();
            return ResponseEntity.ok(csvData);
        } catch (Exception e) {
            logger.error("Error exporting reviews to CSV: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get review moderation queue
     */
    @GetMapping("/moderation-queue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<Review>> getReviewModerationQueue(Pageable pageable) {
        logger.info("Fetching review moderation queue");
        Page<Review> reviews = reviewService.getReviewModerationQueue(pageable);
        return ResponseEntity.ok(reviews);
    }

    /**
     * Moderate review
     */
    @PatchMapping("/{id}/moderate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> moderateReview(@PathVariable Long id, @RequestParam ReviewStatus status) {
        logger.info("Moderating review ID: {} with status: {}", id, status);
        try {
            reviewService.moderateReview(id, status);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error moderating review: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}

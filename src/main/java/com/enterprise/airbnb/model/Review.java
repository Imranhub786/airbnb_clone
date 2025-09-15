package com.enterprise.airbnb.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Review entity for property and user reviews
 */
@Entity
@Table(name = "reviews")
@Audited
@EntityListeners(AuditingEntityListener.class)
public class Review {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Booking is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;
    
    @NotNull(message = "Property is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;
    
    @NotNull(message = "Reviewer is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewer;
    
    @NotNull(message = "Reviewee is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewee_id", nullable = false)
    private User reviewee;
    
    @NotNull(message = "Review type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "review_type", nullable = false)
    private ReviewType reviewType;
    
    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    @Column(nullable = false)
    private Integer rating;
    
    @Size(max = 2000)
    @Column(name = "review_text", columnDefinition = "TEXT")
    private String reviewText;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "review_status", nullable = false)
    private ReviewStatus status = ReviewStatus.PUBLISHED;
    
    @Column(name = "is_anonymous")
    private Boolean isAnonymous = false;
    
    @Column(name = "is_verified_stay")
    private Boolean isVerifiedStay = true;
    
    @Column(name = "response_text", columnDefinition = "TEXT")
    private String responseText;
    
    @Column(name = "response_date")
    private LocalDateTime responseDate;
    
    @Column(name = "helpful_count")
    private Integer helpfulCount = 0;
    
    @Column(name = "not_helpful_count")
    private Integer notHelpfulCount = 0;
    
    @Column(name = "reported_count")
    private Integer reportedCount = 0;
    
    @Column(name = "is_featured")
    private Boolean isFeatured = false;
    
    @Column(name = "moderation_notes", columnDefinition = "TEXT")
    private String moderationNotes;
    
    @Column(name = "moderated_by")
    private String moderatedBy;
    
    @Column(name = "moderated_at")
    private LocalDateTime moderatedAt;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public Review() {}
    
    public Review(Booking booking, Property property, User reviewer, User reviewee, 
                 ReviewType reviewType, Integer rating, String reviewText) {
        this.booking = booking;
        this.property = property;
        this.reviewer = reviewer;
        this.reviewee = reviewee;
        this.reviewType = reviewType;
        this.rating = rating;
        this.reviewText = reviewText;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }
    
    public Property getProperty() { return property; }
    public void setProperty(Property property) { this.property = property; }
    
    public User getReviewer() { return reviewer; }
    public void setReviewer(User reviewer) { this.reviewer = reviewer; }
    
    public User getReviewee() { return reviewee; }
    public void setReviewee(User reviewee) { this.reviewee = reviewee; }
    
    public ReviewType getReviewType() { return reviewType; }
    public void setReviewType(ReviewType reviewType) { this.reviewType = reviewType; }
    
    // Alias method for compatibility
    public void setType(ReviewType reviewType) { this.reviewType = reviewType; }
    
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    
    public String getReviewText() { return reviewText; }
    public void setReviewText(String reviewText) { this.reviewText = reviewText; }
    
    public ReviewStatus getStatus() { return status; }
    public void setStatus(ReviewStatus status) { this.status = status; }
    
    public Boolean getIsAnonymous() { return isAnonymous; }
    public void setIsAnonymous(Boolean isAnonymous) { this.isAnonymous = isAnonymous; }
    
    public Boolean getIsVerifiedStay() { return isVerifiedStay; }
    public void setIsVerifiedStay(Boolean isVerifiedStay) { this.isVerifiedStay = isVerifiedStay; }
    
    public String getResponseText() { return responseText; }
    public void setResponseText(String responseText) { this.responseText = responseText; }
    
    public LocalDateTime getResponseDate() { return responseDate; }
    public void setResponseDate(LocalDateTime responseDate) { this.responseDate = responseDate; }
    
    public Integer getHelpfulCount() { return helpfulCount; }
    public void setHelpfulCount(Integer helpfulCount) { this.helpfulCount = helpfulCount; }
    
    public Integer getNotHelpfulCount() { return notHelpfulCount; }
    public void setNotHelpfulCount(Integer notHelpfulCount) { this.notHelpfulCount = notHelpfulCount; }
    
    public Integer getReportedCount() { return reportedCount; }
    public void setReportedCount(Integer reportedCount) { this.reportedCount = reportedCount; }
    
    public Boolean getIsFeatured() { return isFeatured; }
    public void setIsFeatured(Boolean isFeatured) { this.isFeatured = isFeatured; }
    
    public String getModerationNotes() { return moderationNotes; }
    public void setModerationNotes(String moderationNotes) { this.moderationNotes = moderationNotes; }
    
    public String getModeratedBy() { return moderatedBy; }
    public void setModeratedBy(String moderatedBy) { this.moderatedBy = moderatedBy; }
    
    public LocalDateTime getModeratedAt() { return moderatedAt; }
    public void setModeratedAt(LocalDateTime moderatedAt) { this.moderatedAt = moderatedAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Utility methods
    public boolean isPublished() {
        return status == ReviewStatus.PUBLISHED;
    }
    
    public boolean isPending() {
        return status == ReviewStatus.PENDING;
    }
    
    public boolean isRejected() {
        return status == ReviewStatus.REJECTED;
    }
    
    public boolean hasResponse() {
        return responseText != null && !responseText.trim().isEmpty();
    }
    
    public void addHelpfulVote() {
        this.helpfulCount++;
    }
    
    public void addNotHelpfulVote() {
        this.notHelpfulCount++;
    }
    
    public void reportReview() {
        this.reportedCount++;
    }
    
    public double getHelpfulnessRatio() {
        int total = helpfulCount + notHelpfulCount;
        return total > 0 ? (double) helpfulCount / total : 0.0;
    }
    
    // Missing method for compilation
    public void setComment(String comment) {
        this.reviewText = comment;
    }
    
    public String getComment() {
        return this.reviewText;
    }
}


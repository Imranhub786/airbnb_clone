package com.enterprise.airbnb.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Booking entity representing property reservations
 */
@Entity
@Table(name = "bookings")
@Audited
@EntityListeners(AuditingEntityListener.class)
public class Booking {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Booking reference is required")
    @Size(max = 20)
    @Column(name = "booking_reference", unique = true, nullable = false)
    private String bookingReference;
    
    @NotNull(message = "Property is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;
    
    @NotNull(message = "Guest is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_id", nullable = false)
    private User guest;
    
    @NotNull(message = "Check-in date is required")
    @Column(name = "check_in_date", nullable = false)
    private LocalDate checkInDate;
    
    @NotNull(message = "Check-out date is required")
    @Column(name = "check_out_date", nullable = false)
    private LocalDate checkOutDate;
    
    @NotNull(message = "Number of guests is required")
    @Min(value = 1, message = "Number of guests must be at least 1")
    @Column(name = "number_of_guests", nullable = false)
    private Integer numberOfGuests;
    
    @NotNull(message = "Number of nights is required")
    @Min(value = 1, message = "Number of nights must be at least 1")
    @Column(name = "number_of_nights", nullable = false)
    private Integer numberOfNights;
    
    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Base price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Base price must have at most 10 integer digits and 2 decimal places")
    @Column(name = "base_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal basePrice;
    
    @Column(name = "cleaning_fee", precision = 12, scale = 2)
    private BigDecimal cleaningFee;
    
    @Column(name = "service_fee", precision = 12, scale = 2)
    private BigDecimal serviceFee;
    
    @Column(name = "security_deposit", precision = 12, scale = 2)
    private BigDecimal securityDeposit;
    
    @Column(name = "taxes", precision = 12, scale = 2)
    private BigDecimal taxes;
    
    @NotNull(message = "Total price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Total price must have at most 10 integer digits and 2 decimal places")
    @Column(name = "total_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPrice;
    
    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3)
    @Column(nullable = false, length = 3)
    private String currency = "USD";
    
    @Enumerated(EnumType.STRING)
    @Column(name = "booking_status", nullable = false)
    private BookingStatus status = BookingStatus.PENDING;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    
    @Column(name = "special_requests", columnDefinition = "TEXT")
    private String specialRequests;
    
    @Column(name = "guest_message", columnDefinition = "TEXT")
    private String guestMessage;
    
    @Column(name = "host_message", columnDefinition = "TEXT")
    private String hostMessage;
    
    @Column(name = "cancellation_reason")
    private String cancellationReason;
    
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;
    
    @Column(name = "cancelled_by")
    private String cancelledBy;
    
    @Column(name = "refund_amount", precision = 12, scale = 2)
    private BigDecimal refundAmount;
    
    @Column(name = "refund_processed_at")
    private LocalDateTime refundProcessedAt;
    
    @Column(name = "check_in_instructions", columnDefinition = "TEXT")
    private String checkInInstructions;
    
    @Column(name = "check_out_instructions", columnDefinition = "TEXT")
    private String checkOutInstructions;
    
    @Column(name = "key_exchange_method")
    private String keyExchangeMethod;
    
    @Column(name = "emergency_contact")
    private String emergencyContact;
    
    @Column(name = "host_rating")
    private Integer hostRating;
    
    @Column(name = "property_rating")
    private Integer propertyRating;
    
    @Column(name = "guest_review", columnDefinition = "TEXT")
    private String guestReview;
    
    @Column(name = "host_review", columnDefinition = "TEXT")
    private String hostReview;
    
    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;
    
    @Column(name = "is_instant_book")
    private Boolean isInstantBook = false;
    
    @Column(name = "booking_source")
    private String bookingSource = "WEB";
    
    @Column(name = "ip_address")
    private String ipAddress;
    
    @Column(name = "user_agent")
    private String userAgent;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Relationships
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Payment> payments = new ArrayList<>();
    
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();
    
    // Constructors
    public Booking() {}
    
    public Booking(Property property, User guest, LocalDate checkInDate, LocalDate checkOutDate, 
                  Integer numberOfGuests, BigDecimal totalPrice) {
        this.property = property;
        this.guest = guest;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numberOfGuests = numberOfGuests;
        this.totalPrice = totalPrice;
        this.numberOfNights = (int) checkInDate.until(checkOutDate).getDays();
        this.bookingReference = generateBookingReference();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getBookingReference() { return bookingReference; }
    public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }
    
    public Property getProperty() { return property; }
    public void setProperty(Property property) { this.property = property; }
    
    public User getGuest() { return guest; }
    public void setGuest(User guest) { this.guest = guest; }
    
    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }
    
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }
    
    public Integer getNumberOfGuests() { return numberOfGuests; }
    public void setNumberOfGuests(Integer numberOfGuests) { this.numberOfGuests = numberOfGuests; }
    
    public Integer getNumberOfNights() { return numberOfNights; }
    public void setNumberOfNights(Integer numberOfNights) { this.numberOfNights = numberOfNights; }
    
    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }
    
    public BigDecimal getCleaningFee() { return cleaningFee; }
    public void setCleaningFee(BigDecimal cleaningFee) { this.cleaningFee = cleaningFee; }
    
    public BigDecimal getServiceFee() { return serviceFee; }
    public void setServiceFee(BigDecimal serviceFee) { this.serviceFee = serviceFee; }
    
    public BigDecimal getSecurityDeposit() { return securityDeposit; }
    public void setSecurityDeposit(BigDecimal securityDeposit) { this.securityDeposit = securityDeposit; }
    
    public BigDecimal getTaxes() { return taxes; }
    public void setTaxes(BigDecimal taxes) { this.taxes = taxes; }
    
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
    
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    
    public String getSpecialRequests() { return specialRequests; }
    public void setSpecialRequests(String specialRequests) { this.specialRequests = specialRequests; }
    
    public String getGuestMessage() { return guestMessage; }
    public void setGuestMessage(String guestMessage) { this.guestMessage = guestMessage; }
    
    public String getHostMessage() { return hostMessage; }
    public void setHostMessage(String hostMessage) { this.hostMessage = hostMessage; }
    
    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }
    
    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; }
    
    public String getCancelledBy() { return cancelledBy; }
    public void setCancelledBy(String cancelledBy) { this.cancelledBy = cancelledBy; }
    
    public BigDecimal getRefundAmount() { return refundAmount; }
    public void setRefundAmount(BigDecimal refundAmount) { this.refundAmount = refundAmount; }
    
    public LocalDateTime getRefundProcessedAt() { return refundProcessedAt; }
    public void setRefundProcessedAt(LocalDateTime refundProcessedAt) { this.refundProcessedAt = refundProcessedAt; }
    
    public String getCheckInInstructions() { return checkInInstructions; }
    public void setCheckInInstructions(String checkInInstructions) { this.checkInInstructions = checkInInstructions; }
    
    public String getCheckOutInstructions() { return checkOutInstructions; }
    public void setCheckOutInstructions(String checkOutInstructions) { this.checkOutInstructions = checkOutInstructions; }
    
    public String getKeyExchangeMethod() { return keyExchangeMethod; }
    public void setKeyExchangeMethod(String keyExchangeMethod) { this.keyExchangeMethod = keyExchangeMethod; }
    
    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }
    
    public Integer getHostRating() { return hostRating; }
    public void setHostRating(Integer hostRating) { this.hostRating = hostRating; }
    
    public Integer getPropertyRating() { return propertyRating; }
    public void setPropertyRating(Integer propertyRating) { this.propertyRating = propertyRating; }
    
    public String getGuestReview() { return guestReview; }
    public void setGuestReview(String guestReview) { this.guestReview = guestReview; }
    
    public String getHostReview() { return hostReview; }
    public void setHostReview(String hostReview) { this.hostReview = hostReview; }
    
    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }
    
    public Boolean getIsInstantBook() { return isInstantBook; }
    public void setIsInstantBook(Boolean isInstantBook) { this.isInstantBook = isInstantBook; }
    
    public String getBookingSource() { return bookingSource; }
    public void setBookingSource(String bookingSource) { this.bookingSource = bookingSource; }
    
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public List<Payment> getPayments() { return payments; }
    public void setPayments(List<Payment> payments) { this.payments = payments; }
    
    public List<Review> getReviews() { return reviews; }
    public void setReviews(List<Review> reviews) { this.reviews = reviews; }
    
    // Utility methods
    public boolean isActive() {
        return status == BookingStatus.CONFIRMED || status == BookingStatus.CHECKED_IN;
    }
    
    public boolean isCompleted() {
        return status == BookingStatus.COMPLETED;
    }
    
    public boolean isCancelled() {
        return status == BookingStatus.CANCELLED;
    }
    
    public boolean canBeCancelled() {
        return status == BookingStatus.PENDING || status == BookingStatus.CONFIRMED;
    }
    
    public boolean isPaid() {
        return paymentStatus == PaymentStatus.COMPLETED;
    }
    
    public BigDecimal calculateTotalPrice() {
        BigDecimal total = basePrice;
        
        if (cleaningFee != null) {
            total = total.add(cleaningFee);
        }
        
        if (serviceFee != null) {
            total = total.add(serviceFee);
        }
        
        if (taxes != null) {
            total = total.add(taxes);
        }
        
        return total;
    }
    
    private String generateBookingReference() {
        return "BK" + System.currentTimeMillis() + String.format("%04d", (int) (Math.random() * 10000));
    }
    
    // Missing method for compilation
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalPrice = totalAmount;
    }
    
    public BigDecimal getTotalAmount() {
        return this.totalPrice;
    }
    
    // Missing method for compilation
    public void calculateTotalAmount() {
        if (property != null && checkInDate != null && checkOutDate != null) {
            int nights = (int) java.time.temporal.ChronoUnit.DAYS.between(checkInDate, checkOutDate);
            BigDecimal basePrice = property.getPricePerNight().multiply(BigDecimal.valueOf(nights));
            BigDecimal total = basePrice;
            
            if (cleaningFee != null) {
                total = total.add(cleaningFee);
            }
            
            if (serviceFee != null) {
                total = total.add(serviceFee);
            }
            
            if (taxes != null) {
                total = total.add(taxes);
            }
            
            this.totalPrice = total;
        }
    }
}


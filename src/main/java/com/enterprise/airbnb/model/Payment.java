package com.enterprise.airbnb.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payment entity for handling PayPal payments
 */
@Entity
@Table(name = "payments")
@Audited
@EntityListeners(AuditingEntityListener.class)
public class Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Payment reference is required")
    @Size(max = 50)
    @Column(name = "payment_reference", unique = true, nullable = false)
    private String paymentReference;
    
    @NotNull(message = "Booking is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;
    
    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Amount must have at most 10 integer digits and 2 decimal places")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;
    
    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3)
    @Column(nullable = false, length = 3)
    private String currency = "USD";
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false)
    private PaymentType paymentType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;
    
    @Size(max = 100)
    @Column(name = "paypal_payment_id")
    private String paypalPaymentId;
    
    @Size(max = 100)
    @Column(name = "paypal_order_id")
    private String paypalOrderId;
    
    @Size(max = 100)
    @Column(name = "paypal_capture_id")
    private String paypalCaptureId;
    
    @Size(max = 100)
    @Column(name = "paypal_refund_id")
    private String paypalRefundId;
    
    @Size(max = 100)
    @Column(name = "paypal_transaction_id")
    private String paypalTransactionId;
    
    @Column(name = "paypal_fee", precision = 12, scale = 2)
    private BigDecimal paypalFee;
    
    @Column(name = "net_amount", precision = 12, scale = 2)
    private BigDecimal netAmount;
    
    @Column(name = "exchange_rate", precision = 10, scale = 6)
    private BigDecimal exchangeRate;
    
    @Size(max = 3)
    @Column(name = "original_currency", length = 3)
    private String originalCurrency;
    
    @Column(name = "original_amount", precision = 12, scale = 2)
    private BigDecimal originalAmount;
    
    @Size(max = 1000)
    @Column(name = "description")
    private String description;
    
    @Size(max = 1000)
    @Column(name = "failure_reason")
    private String failureReason;
    
    @Size(max = 1000)
    @Column(name = "refund_reason")
    private String refundReason;
    
    @Column(name = "processed_at")
    private LocalDateTime processedAt;
    
    @Column(name = "failed_at")
    private LocalDateTime failedAt;
    
    @Column(name = "refunded_at")
    private LocalDateTime refundedAt;
    
    @Column(name = "refund_amount", precision = 12, scale = 2)
    private BigDecimal refundAmount;
    
    @Column(name = "is_partial_refund")
    private Boolean isPartialRefund = false;
    
    @Size(max = 1000)
    @Column(name = "webhook_data", columnDefinition = "TEXT")
    private String webhookData;
    
    @Size(max = 1000)
    @Column(name = "callback_url")
    private String callbackUrl;
    
    @Size(max = 1000)
    @Column(name = "return_url")
    private String returnUrl;
    
    @Size(max = 1000)
    @Column(name = "cancel_url")
    private String cancelUrl;
    
    @Column(name = "retry_count")
    private Integer retryCount = 0;
    
    @Column(name = "max_retries")
    private Integer maxRetries = 3;
    
    @Column(name = "next_retry_at")
    private LocalDateTime nextRetryAt;
    
    @Size(max = 1000)
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public Payment() {}
    
    public Payment(Booking booking, User user, BigDecimal amount, PaymentType paymentType, 
                  PaymentMethod paymentMethod) {
        this.booking = booking;
        this.user = user;
        this.amount = amount;
        this.paymentType = paymentType;
        this.paymentMethod = paymentMethod;
        this.paymentReference = generatePaymentReference();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getPaymentReference() { return paymentReference; }
    public void setPaymentReference(String paymentReference) { this.paymentReference = paymentReference; }
    
    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public PaymentType getPaymentType() { return paymentType; }
    public void setPaymentType(PaymentType paymentType) { this.paymentType = paymentType; }
    
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }
    
    public String getPaypalPaymentId() { return paypalPaymentId; }
    public void setPaypalPaymentId(String paypalPaymentId) { this.paypalPaymentId = paypalPaymentId; }
    
    public String getPaypalOrderId() { return paypalOrderId; }
    public void setPaypalOrderId(String paypalOrderId) { this.paypalOrderId = paypalOrderId; }
    
    public String getPaypalCaptureId() { return paypalCaptureId; }
    public void setPaypalCaptureId(String paypalCaptureId) { this.paypalCaptureId = paypalCaptureId; }
    
    public String getPaypalRefundId() { return paypalRefundId; }
    public void setPaypalRefundId(String paypalRefundId) { this.paypalRefundId = paypalRefundId; }
    
    public String getPaypalTransactionId() { return paypalTransactionId; }
    public void setPaypalTransactionId(String paypalTransactionId) { this.paypalTransactionId = paypalTransactionId; }
    
    public BigDecimal getPaypalFee() { return paypalFee; }
    public void setPaypalFee(BigDecimal paypalFee) { this.paypalFee = paypalFee; }
    
    public BigDecimal getNetAmount() { return netAmount; }
    public void setNetAmount(BigDecimal netAmount) { this.netAmount = netAmount; }
    
    public BigDecimal getExchangeRate() { return exchangeRate; }
    public void setExchangeRate(BigDecimal exchangeRate) { this.exchangeRate = exchangeRate; }
    
    public String getOriginalCurrency() { return originalCurrency; }
    public void setOriginalCurrency(String originalCurrency) { this.originalCurrency = originalCurrency; }
    
    public BigDecimal getOriginalAmount() { return originalAmount; }
    public void setOriginalAmount(BigDecimal originalAmount) { this.originalAmount = originalAmount; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }
    
    public String getRefundReason() { return refundReason; }
    public void setRefundReason(String refundReason) { this.refundReason = refundReason; }
    
    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }
    
    public LocalDateTime getFailedAt() { return failedAt; }
    public void setFailedAt(LocalDateTime failedAt) { this.failedAt = failedAt; }
    
    public LocalDateTime getRefundedAt() { return refundedAt; }
    public void setRefundedAt(LocalDateTime refundedAt) { this.refundedAt = refundedAt; }
    
    public BigDecimal getRefundAmount() { return refundAmount; }
    public void setRefundAmount(BigDecimal refundAmount) { this.refundAmount = refundAmount; }
    
    public Boolean getIsPartialRefund() { return isPartialRefund; }
    public void setIsPartialRefund(Boolean isPartialRefund) { this.isPartialRefund = isPartialRefund; }
    
    public String getWebhookData() { return webhookData; }
    public void setWebhookData(String webhookData) { this.webhookData = webhookData; }
    
    public String getCallbackUrl() { return callbackUrl; }
    public void setCallbackUrl(String callbackUrl) { this.callbackUrl = callbackUrl; }
    
    public String getReturnUrl() { return returnUrl; }
    public void setReturnUrl(String returnUrl) { this.returnUrl = returnUrl; }
    
    public String getCancelUrl() { return cancelUrl; }
    public void setCancelUrl(String cancelUrl) { this.cancelUrl = cancelUrl; }
    
    public Integer getRetryCount() { return retryCount; }
    public void setRetryCount(Integer retryCount) { this.retryCount = retryCount; }
    
    public Integer getMaxRetries() { return maxRetries; }
    public void setMaxRetries(Integer maxRetries) { this.maxRetries = maxRetries; }
    
    public LocalDateTime getNextRetryAt() { return nextRetryAt; }
    public void setNextRetryAt(LocalDateTime nextRetryAt) { this.nextRetryAt = nextRetryAt; }
    
    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Utility methods
    public boolean isSuccessful() {
        return status == PaymentStatus.COMPLETED;
    }
    
    public boolean isPending() {
        return status == PaymentStatus.PENDING || status == PaymentStatus.PROCESSING;
    }
    
    public boolean isFailed() {
        return status == PaymentStatus.FAILED || status == PaymentStatus.CANCELLED;
    }
    
    public boolean isRefunded() {
        return status == PaymentStatus.REFUNDED || status == PaymentStatus.PARTIALLY_REFUNDED;
    }
    
    public boolean canRetry() {
        return retryCount < maxRetries && (status == PaymentStatus.FAILED || status == PaymentStatus.PENDING);
    }
    
    public void incrementRetryCount() {
        this.retryCount++;
    }
    
    public BigDecimal calculateNetAmount() {
        if (paypalFee != null) {
            return amount.subtract(paypalFee);
        }
        return amount;
    }
    
    private String generatePaymentReference() {
        return "PAY" + System.currentTimeMillis() + String.format("%04d", (int) (Math.random() * 10000));
    }
    
    // Missing method for compilation
    public void setTransactionId(String transactionId) {
        this.paypalTransactionId = transactionId;
    }
    
    public String getTransactionId() {
        return this.paypalTransactionId;
    }
}


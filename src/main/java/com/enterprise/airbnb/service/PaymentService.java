package com.enterprise.airbnb.service;

import com.enterprise.airbnb.model.Payment;
import com.enterprise.airbnb.model.PaymentStatus;
import com.enterprise.airbnb.model.PaymentMethod;
import com.enterprise.airbnb.model.Booking;
import com.enterprise.airbnb.repository.PaymentRepository;
import com.enterprise.airbnb.repository.BookingRepository;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service for Payment operations with PayPal integration
 */
@Service
@Transactional
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PayPalService payPalService;

    /**
     * Process payment with PayPal
     */
    @CacheEvict(value = {"payments", "bookings"}, allEntries = true)
    public Payment processPayment(Payment payment) {
        logger.info("Processing payment for booking ID: {}", payment.getBooking().getId());
        
        try {
            // Set initial status
            payment.setStatus(PaymentStatus.PENDING);
            
            // Process with PayPal
            boolean paypalSuccess = processPayPalPayment(payment);
            
            if (paypalSuccess) {
                payment.setStatus(PaymentStatus.COMPLETED);
                payment.setProcessedAt(java.time.LocalDateTime.now());
                
                // Update booking status
                Booking booking = payment.getBooking();
                booking.setStatus(com.enterprise.airbnb.model.BookingStatus.CONFIRMED);
                bookingRepository.save(booking);
            } else {
                payment.setStatus(PaymentStatus.FAILED);
            }
            
            return paymentRepository.save(payment);
        } catch (Exception e) {
            logger.error("Error processing payment: {}", e.getMessage());
            payment.setStatus(PaymentStatus.FAILED);
            return paymentRepository.save(payment);
        }
    }

    /**
     * Process PayPal payment
     */
    private boolean processPayPalPayment(Payment payment) {
        logger.info("Processing PayPal payment for amount: {}", payment.getAmount());
        
        try {
            // Create PayPal order
            String orderId = payPalService.createOrder(payment);
            payment.setPaypalOrderId(orderId);
            
            // For now, we'll capture immediately
            // In a real implementation, you might want to capture after user approval
            boolean success = payPalService.captureOrder(orderId);
            
            if (success) {
                // Get order details to extract capture ID
                com.paypal.orders.Order order = payPalService.getOrderDetails(orderId);
                if (order.purchaseUnits() != null && !order.purchaseUnits().isEmpty()) {
                    com.paypal.orders.PurchaseUnit purchaseUnit = order.purchaseUnits().get(0);
                    if (purchaseUnit.payments() != null && 
                        purchaseUnit.payments().captures() != null && 
                        !purchaseUnit.payments().captures().isEmpty()) {
                        String captureId = purchaseUnit.payments().captures().get(0).id();
                        payment.setPaypalCaptureId(captureId);
                    }
                }
            }
            
            return success;
            
        } catch (Exception e) {
            logger.error("PayPal payment processing failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Refund payment
     */
    @CacheEvict(value = {"payments", "bookings"}, allEntries = true)
    public Payment refundPayment(Long paymentId, BigDecimal refundAmount) {
        logger.info("Processing refund for payment ID: {} with amount: {}", paymentId, refundAmount);
        
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new RuntimeException("Only completed payments can be refunded");
        }
        
        if (refundAmount.compareTo(payment.getAmount()) > 0) {
            throw new RuntimeException("Refund amount cannot exceed payment amount");
        }
        
        try {
            // Process PayPal refund
            boolean refundSuccess = processPayPalRefund(payment, refundAmount);
            
            if (refundSuccess) {
                payment.setRefundAmount(refundAmount);
                payment.setRefundedAt(java.time.LocalDateTime.now());
                payment.setStatus(PaymentStatus.REFUNDED);
            }
            
            return paymentRepository.save(payment);
        } catch (Exception e) {
            logger.error("Error processing refund: {}", e.getMessage());
            throw new RuntimeException("Refund processing failed");
        }
    }

    /**
     * Process PayPal refund
     */
    private boolean processPayPalRefund(Payment payment, BigDecimal refundAmount) {
        logger.info("Processing PayPal refund for amount: {}", refundAmount);
        
        try {
            // Use PayPal service to process refund
            String captureId = payment.getPaypalCaptureId();
            if (captureId == null) {
                logger.error("No PayPal capture ID found for payment: {}", payment.getId());
                return false;
            }
            
            return payPalService.refundPayment(captureId, refundAmount);
            
        } catch (Exception e) {
            logger.error("PayPal refund processing failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Get all payments with pagination
     */
    @Cacheable(value = "payments", key = "#pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Payment> getAllPayments(Pageable pageable) {
        logger.info("Fetching all payments with pagination: {}", pageable);
        return paymentRepository.findAll(pageable);
    }

    /**
     * Get payment by ID
     */
    @Cacheable(value = "payments", key = "#id")
    public Optional<Payment> getPaymentById(Long id) {
        logger.info("Fetching payment by ID: {}", id);
        return paymentRepository.findById(id);
    }

    /**
     * Get payments by booking
     */
    @Cacheable(value = "payments", key = "'booking_' + #bookingId + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Payment> getPaymentsByBookingId(Long bookingId, Pageable pageable) {
        logger.info("Fetching payments by booking ID: {}", bookingId);
        return paymentRepository.findByBookingId(bookingId, pageable);
    }

    /**
     * Get payments by status
     */
    @Cacheable(value = "payments", key = "'status_' + #status + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Payment> getPaymentsByStatus(PaymentStatus status, Pageable pageable) {
        logger.info("Fetching payments by status: {}", status);
        return paymentRepository.findByStatus(status, pageable);
    }

    /**
     * Get payments by method
     */
    @Cacheable(value = "payments", key = "'method_' + #method + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Payment> getPaymentsByMethod(PaymentMethod method, Pageable pageable) {
        logger.info("Fetching payments by method: {}", method);
        return paymentRepository.findByPaymentMethod(method, pageable);
    }

    /**
     * Get payments by date range
     */
    @Cacheable(value = "payments", key = "'dateRange_' + #startDate + '_' + #endDate + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Payment> getPaymentsByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        logger.info("Fetching payments by date range: {} to {}", startDate, endDate);
        return paymentRepository.findByDateRange(startDate, endDate, pageable);
    }

    /**
     * Get payment statistics
     */
    @Cacheable(value = "payments", key = "'statistics'")
    public Object getPaymentStatistics() {
        logger.info("Fetching payment statistics");
        return paymentRepository.getPaymentStatistics();
    }

    /**
     * Get revenue by date range
     */
    @Cacheable(value = "payments", key = "'revenue_' + #startDate + '_' + #endDate")
    public Object getRevenueByDateRange(LocalDate startDate, LocalDate endDate) {
        logger.info("Fetching revenue from {} to {}", startDate, endDate);
        return paymentRepository.getRevenueByDateRange(startDate, endDate);
    }

    /**
     * Get monthly revenue
     */
    @Cacheable(value = "payments", key = "'monthlyRevenue'")
    public List<Object> getMonthlyRevenue() {
        logger.info("Fetching monthly revenue");
        return paymentRepository.getMonthlyRevenue();
    }

    /**
     * Get payment trends
     */
    @Cacheable(value = "payments", key = "'trends_' + #startDate + '_' + #endDate")
    public Object getPaymentTrends(LocalDate startDate, LocalDate endDate) {
        logger.info("Fetching payment trends from {} to {}", startDate, endDate);
        return paymentRepository.getPaymentTrends(startDate, endDate);
    }

    /**
     * Get failed payments
     */
    @Cacheable(value = "payments", key = "'failed_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Payment> getFailedPayments(Pageable pageable) {
        logger.info("Fetching failed payments");
        return paymentRepository.findByStatus(PaymentStatus.FAILED, pageable);
    }

    /**
     * Get pending payments
     */
    @Cacheable(value = "payments", key = "'pending_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Payment> getPendingPayments(Pageable pageable) {
        logger.info("Fetching pending payments");
        return paymentRepository.findByStatus(PaymentStatus.PENDING, pageable);
    }

    /**
     * Get completed payments
     */
    @Cacheable(value = "payments", key = "'completed_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Payment> getCompletedPayments(Pageable pageable) {
        logger.info("Fetching completed payments");
        return paymentRepository.findByStatus(PaymentStatus.COMPLETED, pageable);
    }

    /**
     * Get refunded payments
     */
    @Cacheable(value = "payments", key = "'refunded_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Payment> getRefundedPayments(Pageable pageable) {
        logger.info("Fetching refunded payments");
        return paymentRepository.findByStatus(PaymentStatus.REFUNDED, pageable);
    }

    /**
     * Retry failed payment
     */
    @CacheEvict(value = {"payments", "bookings"}, allEntries = true)
    public Payment retryFailedPayment(Long paymentId) {
        logger.info("Retrying failed payment ID: {}", paymentId);
        
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        if (payment.getStatus() != PaymentStatus.FAILED) {
            throw new RuntimeException("Only failed payments can be retried");
        }
        
        return processPayment(payment);
    }

    /**
     * Cancel payment
     */
    @CacheEvict(value = {"payments", "bookings"}, allEntries = true)
    public void cancelPayment(Long paymentId) {
        logger.info("Cancelling payment ID: {}", paymentId);
        
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            throw new RuntimeException("Completed payments cannot be cancelled");
        }
        
        payment.setStatus(PaymentStatus.CANCELLED);
        paymentRepository.save(payment);
    }

    /**
     * Get payment analytics
     */
    @Cacheable(value = "payments", key = "'analytics'")
    public Object getPaymentAnalytics() {
        logger.info("Fetching payment analytics");
        return paymentRepository.getPaymentAnalytics();
    }

    /**
     * Export payments to CSV
     */
    public String exportPaymentsToCsv() {
        logger.info("Exporting payments to CSV");
        return paymentRepository.exportPaymentsToCsv();
    }

    /**
     * Get payment methods statistics
     */
    @Cacheable(value = "payments", key = "'methodStatistics'")
    public Object getPaymentMethodStatistics() {
        logger.info("Fetching payment method statistics");
        return paymentRepository.getPaymentMethodStatistics();
    }

    /**
     * Get currency statistics
     */
    @Cacheable(value = "payments", key = "'currencyStatistics'")
    public Object getCurrencyStatistics() {
        logger.info("Fetching currency statistics");
        return paymentRepository.getCurrencyStatistics();
    }

    /**
     * Get average payment amount
     */
    @Cacheable(value = "payments", key = "'averageAmount'")
    public BigDecimal getAveragePaymentAmount() {
        logger.info("Fetching average payment amount");
        return paymentRepository.getAveragePaymentAmount();
    }

    /**
     * Get total revenue
     */
    @Cacheable(value = "payments", key = "'totalRevenue'")
    public BigDecimal getTotalRevenue() {
        logger.info("Fetching total revenue");
        return paymentRepository.getTotalRevenue();
    }

    /**
     * Get total refunds
     */
    @Cacheable(value = "payments", key = "'totalRefunds'")
    public BigDecimal getTotalRefunds() {
        logger.info("Fetching total refunds");
        return paymentRepository.getTotalRefunds();
    }

    /**
     * Get net revenue
     */
    @Cacheable(value = "payments", key = "'netRevenue'")
    public BigDecimal getNetRevenue() {
        logger.info("Fetching net revenue");
        BigDecimal totalRevenue = getTotalRevenue();
        BigDecimal totalRefunds = getTotalRefunds();
        return totalRevenue.subtract(totalRefunds);
    }

    /**
     * Get payments by user ID with pagination
     */
    public List<Payment> getPaymentsByUser(Long userId, Pageable pageable) {
        logger.debug("Fetching payments by user ID: {} with pagination", userId);
        return getPaymentsByUserId(userId, pageable).getContent();
    }
    
    /**
     * Get payments by user ID with pagination
     */
    public Page<Payment> getPaymentsByUserId(Long userId, Pageable pageable) {
        logger.debug("Fetching payments by user ID: {} with pagination", userId);
        return paymentRepository.findByUserId(userId, pageable);
    }

    /**
     * Get payments by booking ID
     */
    public List<Payment> getPaymentsByBooking(Long bookingId) {
        logger.debug("Fetching payments by booking ID: {}", bookingId);
        return getPaymentsByBookingId(bookingId, PageRequest.of(0, 100)).getContent();
    }

    /**
     * Update payment
     */
    @CacheEvict(value = {"payments", "bookings"}, allEntries = true)
    public Payment updatePayment(Payment payment) {
        logger.info("Updating payment ID: {}", payment.getId());
        return paymentRepository.save(payment);
    }

    /**
     * Update payment status
     */
    @CacheEvict(value = {"payments", "bookings"}, allEntries = true)
    public Payment updatePaymentStatus(Long paymentId, PaymentStatus status) {
        logger.info("Updating payment status for ID: {} to {}", paymentId, status);
        
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        payment.setStatus(status);
        
        if (status == PaymentStatus.COMPLETED) {
            payment.setProcessedAt(java.time.LocalDateTime.now());
        } else if (status == PaymentStatus.FAILED) {
            payment.setFailedAt(java.time.LocalDateTime.now());
        }
        
        return paymentRepository.save(payment);
    }
}


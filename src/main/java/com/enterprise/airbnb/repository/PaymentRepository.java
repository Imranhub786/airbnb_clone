package com.enterprise.airbnb.repository;

import com.enterprise.airbnb.model.Payment;
import com.enterprise.airbnb.model.PaymentStatus;
import com.enterprise.airbnb.model.PaymentMethod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for Payment operations
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Find payments by booking
    Page<Payment> findByBookingId(Long bookingId, Pageable pageable);

    // Find payments by status
    Page<Payment> findByStatus(PaymentStatus status, Pageable pageable);

    // Find payments by method
    Page<Payment> findByPaymentMethod(PaymentMethod method, Pageable pageable);

    // Find payments by date range
    @Query("SELECT p FROM Payment p WHERE p.createdAt BETWEEN :startDate AND :endDate")
    Page<Payment> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, Pageable pageable);

    // Find old failed payments
    @Query("SELECT p FROM Payment p WHERE p.status = 'FAILED' AND p.createdAt < :cutoffDate")
    List<Payment> findOldFailedPayments(@Param("cutoffDate") LocalDateTime cutoffDate);

    // Get payment statistics
    @Query("SELECT COUNT(p) as totalPayments, SUM(p.amount) as totalAmount FROM Payment p")
    Object getPaymentStatistics();

    // Get revenue by date range
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'COMPLETED' AND p.createdAt BETWEEN :startDate AND :endDate")
    Object getRevenueByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Get monthly revenue
    @Query("SELECT MONTH(p.createdAt) as month, SUM(p.amount) as revenue FROM Payment p WHERE p.status = 'COMPLETED' GROUP BY MONTH(p.createdAt)")
    List<Object> getMonthlyRevenue();

    // PayPal webhook methods
    Payment findByPaypalCaptureId(String paypalCaptureId);
    Payment findByPaypalOrderId(String paypalOrderId);
    Payment findByPaypalTransactionId(String paypalTransactionId);

    // Get payment trends
    @Query("SELECT DATE(p.createdAt) as date, COUNT(p) as count, SUM(p.amount) as amount FROM Payment p WHERE p.createdAt BETWEEN :startDate AND :endDate GROUP BY DATE(p.createdAt)")
    Object getPaymentTrends(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Get payment analytics
    @Query("SELECT p.paymentMethod as method, COUNT(p) as count, SUM(p.amount) as amount FROM Payment p GROUP BY p.paymentMethod")
    Object getPaymentAnalytics();

    // Export payments to CSV
    @Query("SELECT p FROM Payment p")
    String exportPaymentsToCsv();

    // Get payment method statistics
    @Query("SELECT p.paymentMethod, COUNT(p) FROM Payment p GROUP BY p.paymentMethod")
    Object getPaymentMethodStatistics();

    // Get currency statistics
    @Query("SELECT p.currency, COUNT(p) FROM Payment p GROUP BY p.currency")
    Object getCurrencyStatistics();

    // Get average payment amount
    @Query("SELECT AVG(p.amount) FROM Payment p WHERE p.status = 'COMPLETED'")
    java.math.BigDecimal getAveragePaymentAmount();

    // Get total revenue
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'COMPLETED'")
    java.math.BigDecimal getTotalRevenue();

    // Get total refunds
    @Query("SELECT SUM(p.refundAmount) FROM Payment p WHERE p.status = 'REFUNDED'")
    java.math.BigDecimal getTotalRefunds();
}


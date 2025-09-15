package com.enterprise.airbnb.repository;

import com.enterprise.airbnb.model.Booking;
import com.enterprise.airbnb.model.BookingStatus;
import com.enterprise.airbnb.model.PaymentStatus;
import com.enterprise.airbnb.model.Property;
import com.enterprise.airbnb.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Booking entity operations
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    /**
     * Find booking by reference
     */
    Optional<Booking> findByBookingReference(String bookingReference);
    
    /**
     * Find bookings by property
     */
    List<Booking> findByProperty(Property property);
    
    /**
     * Find bookings by property with pagination
     */
    Page<Booking> findByProperty(Property property, Pageable pageable);
    
    /**
     * Find bookings by property ID
     */
    List<Booking> findByPropertyId(Long propertyId);
    
    /**
     * Find bookings by property ID with pagination
     */
    Page<Booking> findByPropertyId(Long propertyId, Pageable pageable);
    
    /**
     * Find bookings by guest
     */
    List<Booking> findByGuest(User guest);
    
    /**
     * Find bookings by guest with pagination
     */
    Page<Booking> findByGuest(User guest, Pageable pageable);
    
    /**
     * Find bookings by guest ID
     */
    List<Booking> findByGuestId(Long guestId);
    
    /**
     * Find bookings by guest ID with pagination
     */
    Page<Booking> findByGuestId(Long guestId, Pageable pageable);
    
    /**
     * Find bookings by host (through property)
     */
    @Query("SELECT b FROM Booking b JOIN b.property p WHERE p.host = :host")
    List<Booking> findByHost(@Param("host") User host);
    
    /**
     * Find bookings by host with pagination
     */
    @Query("SELECT b FROM Booking b JOIN b.property p WHERE p.host = :host")
    Page<Booking> findByHost(@Param("host") User host, Pageable pageable);
    
    /**
     * Find bookings by host ID
     */
    @Query("SELECT b FROM Booking b JOIN b.property p WHERE p.host.id = :hostId")
    List<Booking> findByHostId(@Param("hostId") Long hostId);
    
    /**
     * Find bookings by host ID with pagination
     */
    @Query("SELECT b FROM Booking b JOIN b.property p WHERE p.host.id = :hostId")
    Page<Booking> findByHostId(@Param("hostId") Long hostId, Pageable pageable);
    
    /**
     * Find bookings by status
     */
    List<Booking> findByStatus(BookingStatus status);
    
    /**
     * Find bookings by status with pagination
     */
    Page<Booking> findByStatus(BookingStatus status, Pageable pageable);
    
    /**
     * Find bookings by payment status
     */
    List<Booking> findByPaymentStatus(PaymentStatus paymentStatus);
    
    /**
     * Find bookings by payment status with pagination
     */
    Page<Booking> findByPaymentStatus(PaymentStatus paymentStatus, Pageable pageable);
    
    /**
     * Find bookings by property and status
     */
    List<Booking> findByPropertyAndStatus(Property property, BookingStatus status);
    
    /**
     * Find bookings by property and status with pagination
     */
    Page<Booking> findByPropertyAndStatus(Property property, BookingStatus status, Pageable pageable);
    
    /**
     * Find bookings by guest and status
     */
    List<Booking> findByGuestAndStatus(User guest, BookingStatus status);
    
    /**
     * Find bookings by guest and status with pagination
     */
    Page<Booking> findByGuestAndStatus(User guest, BookingStatus status, Pageable pageable);
    
    /**
     * Find bookings by check-in date
     */
    List<Booking> findByCheckInDate(LocalDate checkInDate);
    
    /**
     * Find bookings by check-in date with pagination
     */
    Page<Booking> findByCheckInDate(LocalDate checkInDate, Pageable pageable);
    
    /**
     * Find bookings by check-out date
     */
    List<Booking> findByCheckOutDate(LocalDate checkOutDate);
    
    /**
     * Find bookings by check-out date with pagination
     */
    Page<Booking> findByCheckOutDate(LocalDate checkOutDate, Pageable pageable);
    
    /**
     * Find bookings by check-in date range
     */
    List<Booking> findByCheckInDateBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Find bookings by check-in date range with pagination
     */
    Page<Booking> findByCheckInDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    /**
     * Find bookings by check-out date range
     */
    List<Booking> findByCheckOutDateBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Find bookings by check-out date range with pagination
     */
    Page<Booking> findByCheckOutDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    /**
     * Find bookings by date range (overlapping)
     */
    @Query("SELECT b FROM Booking b WHERE " +
           "b.checkInDate <= :endDate AND b.checkOutDate >= :startDate")
    List<Booking> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    /**
     * Find bookings by date range with pagination
     */
    @Query("SELECT b FROM Booking b WHERE " +
           "b.checkInDate <= :endDate AND b.checkOutDate >= :startDate")
    Page<Booking> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, Pageable pageable);
    
    /**
     * Find bookings by property and date range
     */
    @Query("SELECT b FROM Booking b WHERE b.property = :property AND " +
           "b.checkInDate <= :endDate AND b.checkOutDate >= :startDate")
    List<Booking> findByPropertyAndDateRange(@Param("property") Property property, 
                                           @Param("startDate") LocalDate startDate, 
                                           @Param("endDate") LocalDate endDate);
    
    /**
     * Find bookings by property and date range with pagination
     */
    @Query("SELECT b FROM Booking b WHERE b.property = :property AND " +
           "b.checkInDate <= :endDate AND b.checkOutDate >= :startDate")
    Page<Booking> findByPropertyAndDateRange(@Param("property") Property property, 
                                           @Param("startDate") LocalDate startDate, 
                                           @Param("endDate") LocalDate endDate, 
                                           Pageable pageable);
    
    /**
     * Find bookings by property ID and date range
     */
    @Query("SELECT b FROM Booking b WHERE b.property.id = :propertyId AND " +
           "b.checkInDate <= :endDate AND b.checkOutDate >= :startDate")
    List<Booking> findByPropertyIdAndDateRange(@Param("propertyId") Long propertyId, 
                                             @Param("startDate") LocalDate startDate, 
                                             @Param("endDate") LocalDate endDate);
    
    /**
     * Find bookings by property ID and date range with pagination
     */
    @Query("SELECT b FROM Booking b WHERE b.property.id = :propertyId AND " +
           "b.checkInDate <= :endDate AND b.checkOutDate >= :startDate")
    Page<Booking> findByPropertyIdAndDateRange(@Param("propertyId") Long propertyId, 
                                             @Param("startDate") LocalDate startDate, 
                                             @Param("endDate") LocalDate endDate, 
                                             Pageable pageable);
    
    /**
     * Find bookings by number of guests
     */
    List<Booking> findByNumberOfGuests(Integer numberOfGuests);
    
    /**
     * Find bookings by number of guests with pagination
     */
    Page<Booking> findByNumberOfGuests(Integer numberOfGuests, Pageable pageable);
    
    /**
     * Find bookings by number of nights
     */
    List<Booking> findByNumberOfNights(Integer numberOfNights);
    
    /**
     * Find bookings by number of nights with pagination
     */
    Page<Booking> findByNumberOfNights(Integer numberOfNights, Pageable pageable);
    
    /**
     * Find bookings by total price range
     */
    List<Booking> findByTotalPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    /**
     * Find bookings by total price range with pagination
     */
    Page<Booking> findByTotalPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    
    /**
     * Find bookings by currency
     */
    List<Booking> findByCurrency(String currency);
    
    /**
     * Find bookings by currency with pagination
     */
    Page<Booking> findByCurrency(String currency, Pageable pageable);
    
    /**
     * Find bookings by instant book
     */
    List<Booking> findByIsInstantBook(Boolean isInstantBook);
    
    /**
     * Find bookings by instant book with pagination
     */
    Page<Booking> findByIsInstantBook(Boolean isInstantBook, Pageable pageable);
    
    /**
     * Find bookings by booking source
     */
    List<Booking> findByBookingSource(String bookingSource);
    
    /**
     * Find bookings by booking source with pagination
     */
    Page<Booking> findByBookingSource(String bookingSource, Pageable pageable);
    
    /**
     * Find bookings created after a specific date
     */
    List<Booking> findByCreatedAtAfter(LocalDateTime date);
    
    /**
     * Find bookings created between dates
     */
    List<Booking> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find bookings created between dates with pagination
     */
    Page<Booking> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    /**
     * Find bookings cancelled after a specific date
     */
    List<Booking> findByCancelledAtAfter(LocalDateTime date);
    
    /**
     * Find bookings cancelled between dates
     */
    List<Booking> findByCancelledAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find bookings cancelled between dates with pagination
     */
    Page<Booking> findByCancelledAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    /**
     * Find bookings by cancelled by
     */
    List<Booking> findByCancelledBy(String cancelledBy);
    
    /**
     * Find bookings by cancelled by with pagination
     */
    Page<Booking> findByCancelledBy(String cancelledBy, Pageable pageable);
    
    /**
     * Find active bookings
     */
    @Query("SELECT b FROM Booking b WHERE b.status IN ('CONFIRMED', 'CHECKED_IN')")
    List<Booking> findActiveBookings();
    
    /**
     * Find active bookings with pagination
     */
    @Query("SELECT b FROM Booking b WHERE b.status IN ('CONFIRMED', 'CHECKED_IN')")
    Page<Booking> findActiveBookings(Pageable pageable);
    
    /**
     * Find completed bookings
     */
    @Query("SELECT b FROM Booking b WHERE b.status = 'COMPLETED'")
    List<Booking> findCompletedBookings();
    
    /**
     * Find completed bookings with pagination
     */
    @Query("SELECT b FROM Booking b WHERE b.status = 'COMPLETED'")
    Page<Booking> findCompletedBookings(Pageable pageable);
    
    /**
     * Find cancelled bookings
     */
    @Query("SELECT b FROM Booking b WHERE b.status = 'CANCELLED'")
    List<Booking> findCancelledBookings();
    
    /**
     * Find cancelled bookings with pagination
     */
    @Query("SELECT b FROM Booking b WHERE b.status = 'CANCELLED'")
    Page<Booking> findCancelledBookings(Pageable pageable);
    
    /**
     * Find bookings with payments
     */
    @Query("SELECT DISTINCT b FROM Booking b JOIN b.payments p")
    List<Booking> findBookingsWithPayments();
    
    /**
     * Find bookings without payments
     */
    @Query("SELECT b FROM Booking b WHERE b.payments IS EMPTY")
    List<Booking> findBookingsWithoutPayments();
    
    /**
     * Find bookings with reviews
     */
    @Query("SELECT DISTINCT b FROM Booking b JOIN b.reviews r")
    List<Booking> findBookingsWithReviews();
    
    /**
     * Find bookings without reviews
     */
    @Query("SELECT b FROM Booking b WHERE b.reviews IS EMPTY")
    List<Booking> findBookingsWithoutReviews();
    
    /**
     * Find bookings by host and date range
     */
    @Query("SELECT b FROM Booking b JOIN b.property p WHERE p.host = :host AND " +
           "b.checkInDate <= :endDate AND b.checkOutDate >= :startDate")
    List<Booking> findByHostAndDateRange(@Param("host") User host, 
                                       @Param("startDate") LocalDate startDate, 
                                       @Param("endDate") LocalDate endDate);
    
    /**
     * Find bookings by host and date range with pagination
     */
    @Query("SELECT b FROM Booking b JOIN b.property p WHERE p.host = :host AND " +
           "b.checkInDate <= :endDate AND b.checkOutDate >= :startDate")
    Page<Booking> findByHostAndDateRange(@Param("host") User host, 
                                       @Param("startDate") LocalDate startDate, 
                                       @Param("endDate") LocalDate endDate, 
                                       Pageable pageable);
    
    /**
     * Find bookings by guest and date range
     */
    @Query("SELECT b FROM Booking b WHERE b.guest = :guest AND " +
           "b.checkInDate <= :endDate AND b.checkOutDate >= :startDate")
    List<Booking> findByGuestAndDateRange(@Param("guest") User guest, 
                                        @Param("startDate") LocalDate startDate, 
                                        @Param("endDate") LocalDate endDate);
    
    /**
     * Find bookings by guest and date range with pagination
     */
    @Query("SELECT b FROM Booking b WHERE b.guest = :guest AND " +
           "b.checkInDate <= :endDate AND b.checkOutDate >= :startDate")
    Page<Booking> findByGuestAndDateRange(@Param("guest") User guest, 
                                        @Param("startDate") LocalDate startDate, 
                                        @Param("endDate") LocalDate endDate, 
                                        Pageable pageable);
    
    /**
     * Find upcoming bookings
     */
    @Query("SELECT b FROM Booking b WHERE b.checkInDate > :currentDate AND b.status IN ('CONFIRMED', 'PENDING')")
    List<Booking> findUpcomingBookings(@Param("currentDate") LocalDate currentDate);
    
    /**
     * Find upcoming bookings with pagination
     */
    @Query("SELECT b FROM Booking b WHERE b.checkInDate > :currentDate AND b.status IN ('CONFIRMED', 'PENDING')")
    Page<Booking> findUpcomingBookings(@Param("currentDate") LocalDate currentDate, Pageable pageable);
    
    /**
     * Find current bookings (checked in)
     */
    @Query("SELECT b FROM Booking b WHERE b.checkInDate <= :currentDate AND b.checkOutDate >= :currentDate AND b.status = 'CHECKED_IN'")
    List<Booking> findCurrentBookings(@Param("currentDate") LocalDate currentDate);
    
    /**
     * Find current bookings with pagination
     */
    @Query("SELECT b FROM Booking b WHERE b.checkInDate <= :currentDate AND b.checkOutDate >= :currentDate AND b.status = 'CHECKED_IN'")
    Page<Booking> findCurrentBookings(@Param("currentDate") LocalDate currentDate, Pageable pageable);
    
    /**
     * Find past bookings
     */
    @Query("SELECT b FROM Booking b WHERE b.checkOutDate < :currentDate AND b.status IN ('COMPLETED', 'CHECKED_OUT')")
    List<Booking> findPastBookings(@Param("currentDate") LocalDate currentDate);
    
    /**
     * Find past bookings with pagination
     */
    @Query("SELECT b FROM Booking b WHERE b.checkOutDate < :currentDate AND b.status IN ('COMPLETED', 'CHECKED_OUT')")
    Page<Booking> findPastBookings(@Param("currentDate") LocalDate currentDate, Pageable pageable);
    
    /**
     * Count bookings by property
     */
    long countByProperty(Property property);
    
    /**
     * Count bookings by guest
     */
    long countByGuest(User guest);
    
    /**
     * Count bookings by host
     */
    @Query("SELECT COUNT(b) FROM Booking b JOIN b.property p WHERE p.host = :host")
    long countByHost(@Param("host") User host);
    
    /**
     * Count bookings by status
     */
    long countByStatus(BookingStatus status);
    
    /**
     * Count bookings by payment status
     */
    long countByPaymentStatus(PaymentStatus paymentStatus);
    
    /**
     * Count bookings by property and status
     */
    long countByPropertyAndStatus(Property property, BookingStatus status);
    
    /**
     * Count bookings by guest and status
     */
    long countByGuestAndStatus(User guest, BookingStatus status);
    
    /**
     * Count bookings by host and status
     */
    @Query("SELECT COUNT(b) FROM Booking b JOIN b.property p WHERE p.host = :host AND b.status = :status")
    long countByHostAndStatus(@Param("host") User host, @Param("status") BookingStatus status);
    
    /**
     * Count bookings created after a specific date
     */
    long countByCreatedAtAfter(LocalDateTime date);
    
    /**
     * Count bookings by date range
     */
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.checkInDate <= :endDate AND b.checkOutDate >= :startDate")
    long countByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    /**
     * Count bookings by property and date range
     */
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.property = :property AND b.checkInDate <= :endDate AND b.checkOutDate >= :startDate")
    long countByPropertyAndDateRange(@Param("property") Property property, 
                                   @Param("startDate") LocalDate startDate, 
                                   @Param("endDate") LocalDate endDate);
    
    /**
     * Check if booking exists by reference
     */
    boolean existsByBookingReference(String bookingReference);
    
    /**
     * Check if property has overlapping bookings
     */
    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.property = :property AND " +
           "b.checkInDate <= :endDate AND b.checkOutDate >= :startDate AND " +
           "b.status IN ('CONFIRMED', 'CHECKED_IN', 'PENDING')")
    boolean hasOverlappingBookings(@Param("property") Property property, 
                                 @Param("startDate") LocalDate startDate, 
                                 @Param("endDate") LocalDate endDate);
    
    /**
     * Check if property has overlapping bookings (excluding specific booking)
     */
    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.property = :property AND " +
           "b.id != :excludeBookingId AND " +
           "b.checkInDate <= :endDate AND b.checkOutDate >= :startDate AND " +
           "b.status IN ('CONFIRMED', 'CHECKED_IN', 'PENDING')")
    boolean hasOverlappingBookingsExcluding(@Param("property") Property property, 
                                          @Param("excludeBookingId") Long excludeBookingId,
                                          @Param("startDate") LocalDate startDate, 
                                          @Param("endDate") LocalDate endDate);
    
    // Missing methods for compilation
    boolean isPropertyAvailable(Long propertyId, LocalDate checkIn, LocalDate checkOut);
    
    List<Booking> findBookingsRequiringPayment();
    
    @Query("SELECT new map(" +
           "COUNT(b) as totalBookings, " +
           "SUM(b.totalPrice) as totalRevenue, " +
           "AVG(b.totalPrice) as averageBookingValue, " +
           "COUNT(DISTINCT b.guest) as uniqueGuests, " +
           "COUNT(DISTINCT b.property) as uniqueProperties) " +
           "FROM Booking b")
    java.util.Map<String, Object> getBookingStatistics();
    
    @Query("SELECT COALESCE(SUM(b.totalPrice), 0) FROM Booking b WHERE " +
           "b.checkInDate BETWEEN :startDate AND :endDate AND b.status = 'COMPLETED'")
    BigDecimal getRevenueByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COALESCE(COUNT(b), 0) * 100.0 / " +
           "(SELECT COUNT(DISTINCT d) FROM Booking d WHERE d.property.id = :propertyId AND " +
           "d.checkInDate BETWEEN :startDate AND :endDate) " +
           "FROM Booking b WHERE b.property.id = :propertyId AND " +
           "b.checkInDate BETWEEN :startDate AND :endDate AND b.status = 'COMPLETED'")
    Double getOccupancyRateByProperty(@Param("propertyId") Long propertyId, 
                                    @Param("startDate") LocalDate startDate, 
                                    @Param("endDate") LocalDate endDate);
    
    @Query("SELECT AVG(b.totalPrice) FROM Booking b WHERE b.status = 'COMPLETED'")
    BigDecimal getAverageBookingValue();
    
    @Query("SELECT new map(" +
           "FUNCTION('DATE', b.checkInDate) as date, " +
           "COUNT(b) as bookingCount, " +
           "SUM(b.totalPrice) as revenue) " +
           "FROM Booking b WHERE b.checkInDate BETWEEN :startDate AND :endDate " +
           "GROUP BY FUNCTION('DATE', b.checkInDate) " +
           "ORDER BY FUNCTION('DATE', b.checkInDate)")
    List<java.util.Map<String, Object>> getBookingTrends(@Param("startDate") LocalDate startDate, 
                                                        @Param("endDate") LocalDate endDate);
    
    @Query("SELECT b.property FROM Booking b WHERE b.status = 'COMPLETED' " +
           "GROUP BY b.property ORDER BY COUNT(b) DESC")
    List<Property> getPopularProperties(@Param("limit") int limit);
    
    @Query("SELECT b FROM Booking b WHERE b.status IN ('CONFIRMED', 'PENDING') AND " +
           "EXISTS (SELECT b2 FROM Booking b2 WHERE b2.property = b.property AND " +
           "b2.id != b.id AND b2.status IN ('CONFIRMED', 'PENDING') AND " +
           "b2.checkInDate <= b.checkOutDate AND b2.checkOutDate >= b.checkInDate)")
    List<Booking> findBookingConflicts();
    
    @Query("SELECT b FROM Booking b WHERE b.guest.id = :userId ORDER BY b.createdAt DESC")
    List<Booking> getBookingHistory(@Param("userId") Long userId);
    
    @Query("SELECT CONCAT(b.bookingReference, ',', b.guest.firstName, ',', b.guest.lastName, ',', " +
           "b.property.title, ',', b.checkInDate, ',', b.checkOutDate, ',', b.totalPrice, ',', b.status) " +
           "FROM Booking b ORDER BY b.createdAt DESC")
    List<String> exportBookingsToCsv();
    
    // Additional methods for HostService
    @Query("SELECT COALESCE(SUM(b.totalPrice), 0) FROM Booking b JOIN b.property p WHERE p.host.id = :hostId AND b.status = 'COMPLETED'")
    BigDecimal getHostEarnings(@Param("hostId") Long hostId);
    
    @Query("SELECT COALESCE(SUM(b.totalPrice), 0) FROM Booking b JOIN b.property p WHERE p.host.id = :hostId AND " +
           "b.checkInDate BETWEEN :startDate AND :endDate AND b.status = 'COMPLETED'")
    BigDecimal getHostEarningsByDateRange(@Param("hostId") Long hostId, 
                                        @Param("startDate") LocalDate startDate, 
                                        @Param("endDate") LocalDate endDate);
    
    @Query("SELECT new map(" +
           "FUNCTION('YEAR', b.checkInDate) as year, " +
           "FUNCTION('MONTH', b.checkInDate) as month, " +
           "SUM(b.totalPrice) as earnings) " +
           "FROM Booking b JOIN b.property p WHERE p.host.id = :hostId AND b.status = 'COMPLETED' " +
           "GROUP BY FUNCTION('YEAR', b.checkInDate), FUNCTION('MONTH', b.checkInDate) " +
           "ORDER BY FUNCTION('YEAR', b.checkInDate), FUNCTION('MONTH', b.checkInDate)")
    List<java.util.Map<String, Object>> getHostMonthlyEarnings(@Param("hostId") Long hostId);
    
    // Additional missing methods for HostService
    List<Object> getHostPerformance(Long hostId);
    Double getHostOccupancyRate(Long hostId, LocalDate startDate, LocalDate endDate);
    Double getHostCancellationRate(Long hostId);
    List<Object> getHostDashboard(Long hostId);
    List<Object> getHostCalendar(Long hostId, LocalDate startDate, LocalDate endDate);
    List<Object> getHostAnalytics(Long hostId);
    List<Object> getTopPerformingHosts(int limit);
    Page<Booking> getBookingsByUserId(Long userId, Pageable pageable);
}


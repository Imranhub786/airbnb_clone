package com.enterprise.airbnb.controller;

import com.enterprise.airbnb.model.Booking;
import com.enterprise.airbnb.model.BookingStatus;
import com.enterprise.airbnb.service.BookingService;
import com.enterprise.airbnb.util.JwtRequestFilter;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Booking operations
 */
@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

    @Autowired
    private BookingService bookingService;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    /**
     * Get all bookings with pagination
     */
    @GetMapping
    public ResponseEntity<Page<Booking>> getAllBookings(Pageable pageable) {
        logger.info("Fetching all bookings with pagination: {}", pageable);
        Page<Booking> bookings = bookingService.getAllBookings(pageable);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Get booking by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        logger.info("Fetching booking by ID: {}", id);
        Optional<Booking> booking = bookingService.getBookingById(id);
        return booking.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create new booking
     */
    @PostMapping
    @PreAuthorize("hasRole('GUEST') or hasRole('HOST') or hasRole('SUPER_HOST') or hasRole('ADMIN')")
    public ResponseEntity<Booking> createBooking(@Valid @RequestBody Booking booking) {
        logger.info("Creating new booking for property ID: {}", booking.getProperty().getId());
        try {
            Booking createdBooking = bookingService.createBooking(booking);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBooking);
        } catch (Exception e) {
            logger.error("Error creating booking: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Update booking
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GUEST') or hasRole('HOST') or hasRole('SUPER_HOST') or hasRole('ADMIN')")
    public ResponseEntity<Booking> updateBooking(@PathVariable Long id, @Valid @RequestBody Booking booking) {
        logger.info("Updating booking with ID: {}", id);
        try {
            booking.setId(id);
            Booking updatedBooking = bookingService.updateBooking(booking);
            return ResponseEntity.ok(updatedBooking);
        } catch (Exception e) {
            logger.error("Error updating booking: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Cancel booking
     */
    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('GUEST') or hasRole('HOST') or hasRole('SUPER_HOST') or hasRole('ADMIN')")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long id) {
        logger.info("Cancelling booking with ID: {}", id);
        try {
            bookingService.cancelBooking(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error cancelling booking: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Confirm booking
     */
    @PatchMapping("/{id}/confirm")
    @PreAuthorize("hasRole('HOST') or hasRole('SUPER_HOST') or hasRole('ADMIN')")
    public ResponseEntity<Void> confirmBooking(@PathVariable Long id) {
        logger.info("Confirming booking with ID: {}", id);
        try {
            bookingService.confirmBooking(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error confirming booking: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Complete booking
     */
    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasRole('HOST') or hasRole('SUPER_HOST') or hasRole('ADMIN')")
    public ResponseEntity<Void> completeBooking(@PathVariable Long id) {
        logger.info("Completing booking with ID: {}", id);
        try {
            bookingService.completeBooking(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error completing booking: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get bookings by guest
     */
    @GetMapping("/guest/{guestId}")
    public ResponseEntity<Page<Booking>> getBookingsByGuest(@PathVariable Long guestId, Pageable pageable) {
        logger.info("Fetching bookings by guest ID: {}", guestId);
        Page<Booking> bookings = bookingService.getBookingsByGuestId(guestId, pageable);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Get bookings by property
     */
    @GetMapping("/property/{propertyId}")
    public ResponseEntity<Page<Booking>> getBookingsByProperty(@PathVariable Long propertyId, Pageable pageable) {
        logger.info("Fetching bookings by property ID: {}", propertyId);
        Page<Booking> bookings = bookingService.getBookingsByPropertyId(propertyId, pageable);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Get bookings by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<Booking>> getBookingsByStatus(@PathVariable BookingStatus status, Pageable pageable) {
        logger.info("Fetching bookings by status: {}", status);
        Page<Booking> bookings = bookingService.getBookingsByStatus(status, pageable);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Get bookings by date range
     */
    @GetMapping("/date-range")
    public ResponseEntity<Page<Booking>> getBookingsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Pageable pageable) {
        
        logger.info("Fetching bookings by date range: {} to {}", startDate, endDate);
        Page<Booking> bookings = bookingService.getBookingsByDateRange(startDate, endDate, pageable);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Check property availability
     */
    @GetMapping("/check-availability")
    public ResponseEntity<Boolean> checkPropertyAvailability(
            @RequestParam Long propertyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate) {
        
        logger.info("Checking availability for property ID: {} from {} to {}", propertyId, checkInDate, checkOutDate);
        boolean isAvailable = bookingService.isPropertyAvailable(propertyId, checkInDate, checkOutDate);
        return ResponseEntity.ok(isAvailable);
    }

    /**
     * Get upcoming bookings
     */
    @GetMapping("/upcoming")
    public ResponseEntity<Page<Booking>> getUpcomingBookings(Pageable pageable) {
        logger.info("Fetching upcoming bookings");
        Page<Booking> bookings = bookingService.getUpcomingBookings(pageable);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Get past bookings
     */
    @GetMapping("/past")
    public ResponseEntity<Page<Booking>> getPastBookings(Pageable pageable) {
        logger.info("Fetching past bookings");
        Page<Booking> bookings = bookingService.getPastBookings(pageable);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Get current bookings
     */
    @GetMapping("/current")
    public ResponseEntity<Page<Booking>> getCurrentBookings(Pageable pageable) {
        logger.info("Fetching current bookings");
        Page<Booking> bookings = bookingService.getCurrentBookings(pageable);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Get bookings requiring payment
     */
    @GetMapping("/pending-payment")
    public ResponseEntity<List<Booking>> getBookingsRequiringPayment() {
        logger.info("Fetching bookings requiring payment");
        List<Booking> bookings = bookingService.getBookingsRequiringPayment();
        return ResponseEntity.ok(bookings);
    }

    /**
     * Get bookings by host
     */
    @GetMapping("/host/{hostId}")
    public ResponseEntity<Page<Booking>> getBookingsByHost(@PathVariable Long hostId, Pageable pageable) {
        logger.info("Fetching bookings by host ID: {}", hostId);
        Page<Booking> bookings = bookingService.getBookingsByHostId(hostId, pageable);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Get booking statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Object> getBookingStatistics() {
        logger.info("Fetching booking statistics");
        Object statistics = bookingService.getBookingStatistics();
        return ResponseEntity.ok(statistics);
    }

    /**
     * Get revenue by date range
     */
    @GetMapping("/revenue")
    public ResponseEntity<Object> getRevenueByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        logger.info("Fetching revenue from {} to {}", startDate, endDate);
        Object revenue = bookingService.getRevenueByDateRange(startDate, endDate);
        return ResponseEntity.ok(revenue);
    }

    /**
     * Get occupancy rate by property
     */
    @GetMapping("/occupancy-rate/{propertyId}")
    public ResponseEntity<Double> getOccupancyRateByProperty(
            @PathVariable Long propertyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        logger.info("Fetching occupancy rate for property ID: {} from {} to {}", propertyId, startDate, endDate);
        double occupancyRate = bookingService.getOccupancyRateByProperty(propertyId, startDate, endDate);
        return ResponseEntity.ok(occupancyRate);
    }

    /**
     * Get average booking value
     */
    @GetMapping("/average-value")
    public ResponseEntity<Object> getAverageBookingValue() {
        logger.info("Fetching average booking value");
        Object averageValue = bookingService.getAverageBookingValue();
        return ResponseEntity.ok(averageValue);
    }

    /**
     * Get booking trends
     */
    @GetMapping("/trends")
    public ResponseEntity<Object> getBookingTrends(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        logger.info("Fetching booking trends from {} to {}", startDate, endDate);
        Object trends = bookingService.getBookingTrends(startDate, endDate);
        return ResponseEntity.ok(trends);
    }

    /**
     * Get popular properties
     */
    @GetMapping("/popular-properties")
    public ResponseEntity<List<Object>> getPopularProperties(@RequestParam(defaultValue = "10") int limit) {
        logger.info("Fetching popular properties with limit: {}", limit);
        List<Object> popularProperties = bookingService.getPopularProperties(limit);
        return ResponseEntity.ok(popularProperties);
    }

    /**
     * Get booking conflicts
     */
    @GetMapping("/conflicts")
    public ResponseEntity<List<Booking>> getBookingConflicts() {
        logger.info("Fetching booking conflicts");
        List<Booking> conflicts = bookingService.getBookingConflicts();
        return ResponseEntity.ok(conflicts);
    }

    /**
     * Resolve booking conflict
     */
    @PatchMapping("/{id}/resolve-conflict")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> resolveBookingConflict(@PathVariable Long id) {
        logger.info("Resolving booking conflict for ID: {}", id);
        try {
            bookingService.resolveBookingConflict(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error resolving booking conflict: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get booking history
     */
    @GetMapping("/{id}/history")
    public ResponseEntity<List<Object>> getBookingHistory(@PathVariable Long id) {
        logger.info("Fetching booking history for ID: {}", id);
        List<Object> history = bookingService.getBookingHistory(id);
        return ResponseEntity.ok(history);
    }

    /**
     * Export bookings to CSV
     */
    @GetMapping("/export/csv")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> exportBookingsToCsv() {
        logger.info("Exporting bookings to CSV");
        try {
            String csvData = bookingService.exportBookingsToCsv();
            return ResponseEntity.ok(csvData);
        } catch (Exception e) {
            logger.error("Error exporting bookings to CSV: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}

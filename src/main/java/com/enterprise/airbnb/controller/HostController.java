package com.enterprise.airbnb.controller;

import com.enterprise.airbnb.model.Property;
import com.enterprise.airbnb.model.User;
import com.enterprise.airbnb.model.Booking;
import com.enterprise.airbnb.model.Review;
import com.enterprise.airbnb.service.HostService;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Host operations
 */
@RestController
@RequestMapping("/api/hosts")
@CrossOrigin(origins = "*")
public class HostController {

    private static final Logger logger = LoggerFactory.getLogger(HostController.class);

    @Autowired
    private HostService hostService;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    /**
     * Get all hosts with pagination
     */
    @GetMapping
    public ResponseEntity<Page<User>> getAllHosts(Pageable pageable) {
        logger.info("Fetching all hosts with pagination: {}", pageable);
        Page<User> hosts = hostService.getAllHosts(pageable);
        return ResponseEntity.ok(hosts);
    }

    /**
     * Get host by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getHostById(@PathVariable Long id) {
        logger.info("Fetching host by ID: {}", id);
        Optional<User> host = hostService.getHostById(id);
        return host.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create new host
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> createHost(@Valid @RequestBody User host) {
        logger.info("Creating new host: {}", host.getEmail());
        try {
            User createdHost = hostService.createHost(host);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdHost);
        } catch (Exception e) {
            logger.error("Error creating host: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Update host
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('HOST') or hasRole('SUPER_HOST') or hasRole('ADMIN')")
    public ResponseEntity<User> updateHost(@PathVariable Long id, @Valid @RequestBody User host) {
        logger.info("Updating host with ID: {}", id);
        try {
            host.setId(id);
            User updatedHost = hostService.updateHost(host);
            return ResponseEntity.ok(updatedHost);
        } catch (Exception e) {
            logger.error("Error updating host: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Delete host
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteHost(@PathVariable Long id) {
        logger.info("Deleting host with ID: {}", id);
        try {
            hostService.deleteHost(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting host: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get host's properties
     */
    @GetMapping("/{id}/properties")
    public ResponseEntity<Page<Property>> getHostProperties(@PathVariable Long id, Pageable pageable) {
        logger.info("Fetching properties for host ID: {}", id);
        Page<Property> properties = hostService.getHostProperties(id, pageable);
        return ResponseEntity.ok(properties);
    }

    /**
     * Get host's bookings
     */
    @GetMapping("/{id}/bookings")
    public ResponseEntity<Page<Booking>> getHostBookings(@PathVariable Long id, Pageable pageable) {
        logger.info("Fetching bookings for host ID: {}", id);
        Page<Booking> bookings = hostService.getHostBookings(id, pageable);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Get host's reviews
     */
    @GetMapping("/{id}/reviews")
    public ResponseEntity<Page<Review>> getHostReviews(@PathVariable Long id, Pageable pageable) {
        logger.info("Fetching reviews for host ID: {}", id);
        Page<Review> reviews = hostService.getHostReviews(id, pageable);
        return ResponseEntity.ok(reviews);
    }

    /**
     * Get host's earnings
     */
    @GetMapping("/{id}/earnings")
    public ResponseEntity<BigDecimal> getHostEarnings(@PathVariable Long id) {
        logger.info("Fetching earnings for host ID: {}", id);
        BigDecimal earnings = hostService.getHostEarnings(id);
        return ResponseEntity.ok(earnings);
    }

    /**
     * Get host's earnings by date range
     */
    @GetMapping("/{id}/earnings/date-range")
    public ResponseEntity<BigDecimal> getHostEarningsByDateRange(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        logger.info("Fetching earnings for host ID: {} from {} to {}", id, startDate, endDate);
        BigDecimal earnings = hostService.getHostEarningsByDateRange(id, startDate, endDate);
        return ResponseEntity.ok(earnings);
    }

    /**
     * Get host's monthly earnings
     */
    @GetMapping("/{id}/earnings/monthly")
    public ResponseEntity<List<Object>> getHostMonthlyEarnings(@PathVariable Long id) {
        logger.info("Fetching monthly earnings for host ID: {}", id);
        List<Object> monthlyEarnings = hostService.getHostMonthlyEarnings(id);
        return ResponseEntity.ok(monthlyEarnings);
    }

    /**
     * Get host's performance metrics
     */
    @GetMapping("/{id}/performance")
    public ResponseEntity<Object> getHostPerformance(@PathVariable Long id) {
        logger.info("Fetching performance metrics for host ID: {}", id);
        Object performance = hostService.getHostPerformance(id);
        return ResponseEntity.ok(performance);
    }

    /**
     * Get host's occupancy rate
     */
    @GetMapping("/{id}/occupancy-rate")
    public ResponseEntity<Double> getHostOccupancyRate(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        logger.info("Fetching occupancy rate for host ID: {} from {} to {}", id, startDate, endDate);
        double occupancyRate = hostService.getHostOccupancyRate(id, startDate, endDate);
        return ResponseEntity.ok(occupancyRate);
    }

    /**
     * Get host's average rating
     */
    @GetMapping("/{id}/average-rating")
    public ResponseEntity<Double> getHostAverageRating(@PathVariable Long id) {
        logger.info("Fetching average rating for host ID: {}", id);
        double averageRating = hostService.getHostAverageRating(id);
        return ResponseEntity.ok(averageRating);
    }

    /**
     * Get host's response rate
     */
    @GetMapping("/{id}/response-rate")
    public ResponseEntity<Double> getHostResponseRate(@PathVariable Long id) {
        logger.info("Fetching response rate for host ID: {}", id);
        double responseRate = hostService.getHostResponseRate(id);
        return ResponseEntity.ok(responseRate);
    }

    /**
     * Get host's cancellation rate
     */
    @GetMapping("/{id}/cancellation-rate")
    public ResponseEntity<Double> getHostCancellationRate(@PathVariable Long id) {
        logger.info("Fetching cancellation rate for host ID: {}", id);
        double cancellationRate = hostService.getHostCancellationRate(id);
        return ResponseEntity.ok(cancellationRate);
    }

    /**
     * Get host's dashboard data
     */
    @GetMapping("/{id}/dashboard")
    @PreAuthorize("hasRole('HOST') or hasRole('SUPER_HOST') or hasRole('ADMIN')")
    public ResponseEntity<Object> getHostDashboard(@PathVariable Long id) {
        logger.info("Fetching dashboard data for host ID: {}", id);
        Object dashboard = hostService.getHostDashboard(id);
        return ResponseEntity.ok(dashboard);
    }

    /**
     * Get host's calendar
     */
    @GetMapping("/{id}/calendar")
    @PreAuthorize("hasRole('HOST') or hasRole('SUPER_HOST') or hasRole('ADMIN')")
    public ResponseEntity<Object> getHostCalendar(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        logger.info("Fetching calendar for host ID: {} from {} to {}", id, startDate, endDate);
        Object calendar = hostService.getHostCalendar(id, startDate, endDate);
        return ResponseEntity.ok(calendar);
    }

    /**
     * Update host's calendar
     */
    @PutMapping("/{id}/calendar")
    @PreAuthorize("hasRole('HOST') or hasRole('SUPER_HOST') or hasRole('ADMIN')")
    public ResponseEntity<Void> updateHostCalendar(@PathVariable Long id, @RequestBody Object calendarData) {
        logger.info("Updating calendar for host ID: {}", id);
        try {
            hostService.updateHostCalendar(id, calendarData);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error updating host calendar: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get host's messages
     */
    @GetMapping("/{id}/messages")
    @PreAuthorize("hasRole('HOST') or hasRole('SUPER_HOST') or hasRole('ADMIN')")
    public ResponseEntity<Page<Object>> getHostMessages(@PathVariable Long id, Pageable pageable) {
        logger.info("Fetching messages for host ID: {}", id);
        Page<Object> messages = hostService.getHostMessages(id, pageable);
        return ResponseEntity.ok(messages);
    }

    /**
     * Send message to guest
     */
    @PostMapping("/{id}/messages")
    @PreAuthorize("hasRole('HOST') or hasRole('SUPER_HOST') or hasRole('ADMIN')")
    public ResponseEntity<Void> sendMessageToGuest(
            @PathVariable Long id,
            @RequestParam Long guestId,
            @RequestParam String message) {
        
        logger.info("Sending message from host ID: {} to guest ID: {}", id, guestId);
        try {
            hostService.sendMessageToGuest(id, guestId, message);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error sending message: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get host's notifications
     */
    @GetMapping("/{id}/notifications")
    @PreAuthorize("hasRole('HOST') or hasRole('SUPER_HOST') or hasRole('ADMIN')")
    public ResponseEntity<Page<Object>> getHostNotifications(@PathVariable Long id, Pageable pageable) {
        logger.info("Fetching notifications for host ID: {}", id);
        Page<Object> notifications = hostService.getHostNotifications(id, pageable);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Mark notification as read
     */
    @PatchMapping("/{id}/notifications/{notificationId}/read")
    @PreAuthorize("hasRole('HOST') or hasRole('SUPER_HOST') or hasRole('ADMIN')")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable Long id, @PathVariable Long notificationId) {
        logger.info("Marking notification as read for host ID: {} and notification ID: {}", id, notificationId);
        try {
            hostService.markNotificationAsRead(id, notificationId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error marking notification as read: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get host's settings
     */
    @GetMapping("/{id}/settings")
    @PreAuthorize("hasRole('HOST') or hasRole('SUPER_HOST') or hasRole('ADMIN')")
    public ResponseEntity<Object> getHostSettings(@PathVariable Long id) {
        logger.info("Fetching settings for host ID: {}", id);
        Object settings = hostService.getHostSettings(id);
        return ResponseEntity.ok(settings);
    }

    /**
     * Update host's settings
     */
    @PutMapping("/{id}/settings")
    @PreAuthorize("hasRole('HOST') or hasRole('SUPER_HOST') or hasRole('ADMIN')")
    public ResponseEntity<Void> updateHostSettings(@PathVariable Long id, @RequestBody Object settings) {
        logger.info("Updating settings for host ID: {}", id);
        try {
            hostService.updateHostSettings(id, settings);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error updating host settings: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get host's verification status
     */
    @GetMapping("/{id}/verification")
    public ResponseEntity<Object> getHostVerificationStatus(@PathVariable Long id) {
        logger.info("Fetching verification status for host ID: {}", id);
        Object verificationStatus = hostService.getHostVerificationStatus(id);
        return ResponseEntity.ok(verificationStatus);
    }

    /**
     * Submit host verification
     */
    @PostMapping("/{id}/verification")
    @PreAuthorize("hasRole('HOST') or hasRole('SUPER_HOST') or hasRole('ADMIN')")
    public ResponseEntity<Void> submitHostVerification(@PathVariable Long id, @RequestBody Object verificationData) {
        logger.info("Submitting verification for host ID: {}", id);
        try {
            hostService.submitHostVerification(id, verificationData);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error submitting host verification: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get host's payout information
     */
    @GetMapping("/{id}/payouts")
    @PreAuthorize("hasRole('HOST') or hasRole('SUPER_HOST') or hasRole('ADMIN')")
    public ResponseEntity<Page<Object>> getHostPayouts(@PathVariable Long id, Pageable pageable) {
        logger.info("Fetching payouts for host ID: {}", id);
        Page<Object> payouts = hostService.getHostPayouts(id, pageable);
        return ResponseEntity.ok(payouts);
    }

    /**
     * Request payout
     */
    @PostMapping("/{id}/payouts/request")
    @PreAuthorize("hasRole('HOST') or hasRole('SUPER_HOST') or hasRole('ADMIN')")
    public ResponseEntity<Void> requestPayout(@PathVariable Long id, @RequestParam BigDecimal amount) {
        logger.info("Requesting payout for host ID: {} with amount: {}", id, amount);
        try {
            hostService.requestPayout(id, amount);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error requesting payout: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get host's tax information
     */
    @GetMapping("/{id}/tax-info")
    @PreAuthorize("hasRole('HOST') or hasRole('SUPER_HOST') or hasRole('ADMIN')")
    public ResponseEntity<Object> getHostTaxInfo(@PathVariable Long id) {
        logger.info("Fetching tax information for host ID: {}", id);
        Object taxInfo = hostService.getHostTaxInfo(id);
        return ResponseEntity.ok(taxInfo);
    }

    /**
     * Update host's tax information
     */
    @PutMapping("/{id}/tax-info")
    @PreAuthorize("hasRole('HOST') or hasRole('SUPER_HOST') or hasRole('ADMIN')")
    public ResponseEntity<Void> updateHostTaxInfo(@PathVariable Long id, @RequestBody Object taxInfo) {
        logger.info("Updating tax information for host ID: {}", id);
        try {
            hostService.updateHostTaxInfo(id, taxInfo);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error updating host tax information: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get host's analytics
     */
    @GetMapping("/{id}/analytics")
    @PreAuthorize("hasRole('HOST') or hasRole('SUPER_HOST') or hasRole('ADMIN')")
    public ResponseEntity<Object> getHostAnalytics(@PathVariable Long id) {
        logger.info("Fetching analytics for host ID: {}", id);
        Object analytics = hostService.getHostAnalytics(id);
        return ResponseEntity.ok(analytics);
    }

    /**
     * Get top performing hosts
     */
    @GetMapping("/top-performing")
    public ResponseEntity<List<Object>> getTopPerformingHosts(@RequestParam(defaultValue = "10") int limit) {
        logger.info("Fetching top performing hosts with limit: {}", limit);
        List<Object> topHosts = hostService.getTopPerformingHosts(limit);
        return ResponseEntity.ok(topHosts);
    }

    /**
     * Get host's guest reviews
     */
    @GetMapping("/{id}/guest-reviews")
    public ResponseEntity<Page<Review>> getHostGuestReviews(@PathVariable Long id, Pageable pageable) {
        logger.info("Fetching guest reviews for host ID: {}", id);
        Page<Review> reviews = hostService.getHostGuestReviews(id, pageable);
        return ResponseEntity.ok(reviews);
    }

    /**
     * Get host's property reviews
     */
    @GetMapping("/{id}/property-reviews")
    public ResponseEntity<Page<Review>> getHostPropertyReviews(@PathVariable Long id, Pageable pageable) {
        logger.info("Fetching property reviews for host ID: {}", id);
        Page<Review> reviews = hostService.getHostPropertyReviews(id, pageable);
        return ResponseEntity.ok(reviews);
    }
}





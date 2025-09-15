package com.enterprise.airbnb.service;

import com.enterprise.airbnb.event.BookingCreatedEvent;
import com.enterprise.airbnb.model.Booking;
import com.enterprise.airbnb.model.BookingStatus;
import com.enterprise.airbnb.repository.BookingRepository;
import com.enterprise.airbnb.repository.PropertyRepository;
import com.enterprise.airbnb.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service for Booking operations
 */
@Service
@Transactional
public class BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    /**
     * Get all bookings with pagination
     */
    @Cacheable(value = "bookings", key = "#pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Booking> getAllBookings(Pageable pageable) {
        logger.info("Fetching all bookings with pagination: {}", pageable);
        return bookingRepository.findAll(pageable);
    }

    /**
     * Get booking by ID
     */
    @Cacheable(value = "bookings", key = "#id")
    public Optional<Booking> getBookingById(Long id) {
        logger.info("Fetching booking by ID: {}", id);
        return bookingRepository.findById(id);
    }

    /**
     * Create new booking
     */
    @CacheEvict(value = {"bookings", "properties"}, allEntries = true)
    public Booking createBooking(Booking booking) {
        logger.info("Creating new booking for property ID: {}", booking.getProperty().getId());
        
        // Validate property availability
        if (!isPropertyAvailable(booking.getProperty().getId(), booking.getCheckInDate(), booking.getCheckOutDate())) {
            throw new RuntimeException("Property is not available for the selected dates");
        }
        
        // Set initial status
        booking.setStatus(BookingStatus.PENDING);
        
        // Calculate total amount
        booking.calculateTotalAmount();
        
        // Save booking
        Booking savedBooking = bookingRepository.save(booking);
        
        // Publish booking created event
        eventPublisher.publishEvent(new BookingCreatedEvent(this, savedBooking));
        
        return savedBooking;
    }

    /**
     * Update booking
     */
    @CacheEvict(value = {"bookings", "properties"}, allEntries = true)
    public Booking updateBooking(Booking booking) {
        logger.info("Updating booking with ID: {}", booking.getId());
        return bookingRepository.save(booking);
    }

    /**
     * Cancel booking
     */
    @CacheEvict(value = {"bookings", "properties"}, allEntries = true)
    public void cancelBooking(Long id) {
        logger.info("Cancelling booking with ID: {}", id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new RuntimeException("Booking is already cancelled");
        }
        
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    /**
     * Confirm booking
     */
    @CacheEvict(value = {"bookings", "properties"}, allEntries = true)
    public void confirmBooking(Long id) {
        logger.info("Confirming booking with ID: {}", id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new RuntimeException("Only pending bookings can be confirmed");
        }
        
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);
    }

    /**
     * Complete booking
     */
    @CacheEvict(value = {"bookings", "properties"}, allEntries = true)
    public void completeBooking(Long id) {
        logger.info("Completing booking with ID: {}", id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new RuntimeException("Only confirmed bookings can be completed");
        }
        
        booking.setStatus(BookingStatus.COMPLETED);
        bookingRepository.save(booking);
    }

    /**
     * Get bookings by guest
     */
    @Cacheable(value = "bookings", key = "'guest_' + #guestId + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Booking> getBookingsByGuestId(Long guestId, Pageable pageable) {
        logger.info("Fetching bookings by guest ID: {}", guestId);
        return bookingRepository.findByGuestId(guestId, pageable);
    }

    /**
     * Get bookings by property
     */
    @Cacheable(value = "bookings", key = "'property_' + #propertyId + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Booking> getBookingsByPropertyId(Long propertyId, Pageable pageable) {
        logger.info("Fetching bookings by property ID: {}", propertyId);
        return bookingRepository.findByPropertyId(propertyId, pageable);
    }

    /**
     * Get bookings by status
     */
    @Cacheable(value = "bookings", key = "'status_' + #status + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Booking> getBookingsByStatus(BookingStatus status, Pageable pageable) {
        logger.info("Fetching bookings by status: {}", status);
        return bookingRepository.findByStatus(status, pageable);
    }

    /**
     * Get bookings by date range
     */
    @Cacheable(value = "bookings", key = "'dateRange_' + #startDate + '_' + #endDate + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Booking> getBookingsByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        logger.info("Fetching bookings by date range: {} to {}", startDate, endDate);
        return bookingRepository.findByDateRange(startDate, endDate, pageable);
    }

    /**
     * Check property availability
     */
    @Cacheable(value = "properties", key = "'availability_' + #propertyId + '_' + #checkInDate + '_' + #checkOutDate")
    public boolean isPropertyAvailable(Long propertyId, LocalDate checkInDate, LocalDate checkOutDate) {
        logger.info("Checking availability for property ID: {} from {} to {}", propertyId, checkInDate, checkOutDate);
        return bookingRepository.isPropertyAvailable(propertyId, checkInDate, checkOutDate);
    }

    /**
     * Get upcoming bookings
     */
    @Cacheable(value = "bookings", key = "'upcoming_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Booking> getUpcomingBookings(Pageable pageable) {
        logger.info("Fetching upcoming bookings");
        return bookingRepository.findUpcomingBookings(LocalDate.now(), pageable);
    }

    /**
     * Get past bookings
     */
    @Cacheable(value = "bookings", key = "'past_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Booking> getPastBookings(Pageable pageable) {
        logger.info("Fetching past bookings");
        return bookingRepository.findPastBookings(LocalDate.now(), pageable);
    }

    /**
     * Get current bookings
     */
    @Cacheable(value = "bookings", key = "'current_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Booking> getCurrentBookings(Pageable pageable) {
        logger.info("Fetching current bookings");
        return bookingRepository.findCurrentBookings(LocalDate.now(), pageable);
    }

    /**
     * Get bookings requiring payment
     */
    @Cacheable(value = "bookings", key = "'pendingPayment'")
    public List<Booking> getBookingsRequiringPayment() {
        logger.info("Fetching bookings requiring payment");
        return bookingRepository.findBookingsRequiringPayment();
    }

    /**
     * Get bookings by host
     */
    @Cacheable(value = "bookings", key = "'host_' + #hostId + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Booking> getBookingsByHostId(Long hostId, Pageable pageable) {
        logger.info("Fetching bookings by host ID: {}", hostId);
        return bookingRepository.findByHostId(hostId, pageable);
    }

    /**
     * Get booking statistics
     */
    @Cacheable(value = "bookings", key = "'statistics'")
    public Object getBookingStatistics() {
        logger.info("Fetching booking statistics");
        return bookingRepository.getBookingStatistics();
    }

    /**
     * Get revenue by date range
     */
    @Cacheable(value = "bookings", key = "'revenue_' + #startDate + '_' + #endDate")
    public Object getRevenueByDateRange(LocalDate startDate, LocalDate endDate) {
        logger.info("Fetching revenue from {} to {}", startDate, endDate);
        return bookingRepository.getRevenueByDateRange(startDate, endDate);
    }

    /**
     * Get occupancy rate by property
     */
    @Cacheable(value = "properties", key = "'occupancy_' + #propertyId + '_' + #startDate + '_' + #endDate")
    public double getOccupancyRateByProperty(Long propertyId, LocalDate startDate, LocalDate endDate) {
        logger.info("Fetching occupancy rate for property ID: {} from {} to {}", propertyId, startDate, endDate);
        return bookingRepository.getOccupancyRateByProperty(propertyId, startDate, endDate);
    }

    /**
     * Get average booking value
     */
    @Cacheable(value = "bookings", key = "'averageValue'")
    public Object getAverageBookingValue() {
        logger.info("Fetching average booking value");
        return bookingRepository.getAverageBookingValue();
    }

    /**
     * Get booking trends
     */
    @Cacheable(value = "bookings", key = "'trends_' + #startDate + '_' + #endDate")
    public Object getBookingTrends(LocalDate startDate, LocalDate endDate) {
        logger.info("Fetching booking trends from {} to {}", startDate, endDate);
        return bookingRepository.getBookingTrends(startDate, endDate);
    }

    /**
     * Get popular properties
     */
    @Cacheable(value = "properties", key = "'popular_' + #limit")
    public List<Object> getPopularProperties(int limit) {
        logger.info("Fetching popular properties with limit: {}", limit);
        return (List<Object>) bookingRepository.getPopularProperties(limit);
    }

    /**
     * Get booking conflicts
     */
    @Cacheable(value = "bookings", key = "'conflicts'")
    public List<Booking> getBookingConflicts() {
        logger.info("Fetching booking conflicts");
        return bookingRepository.findBookingConflicts();
    }

    /**
     * Resolve booking conflict
     */
    @CacheEvict(value = {"bookings", "properties"}, allEntries = true)
    public void resolveBookingConflict(Long id) {
        logger.info("Resolving booking conflict for ID: {}", id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        // Implement conflict resolution logic
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);
    }

    /**
     * Get booking history
     */
    @Cacheable(value = "bookings", key = "'history_' + #id")
    public List<Object> getBookingHistory(Long id) {
        logger.info("Fetching booking history for ID: {}", id);
        return (List<Object>) bookingRepository.getBookingHistory(id);
    }

    /**
     * Export bookings to CSV
     */
    public String exportBookingsToCsv() {
        logger.info("Exporting bookings to CSV");
        List<String> csvData = bookingRepository.exportBookingsToCsv();
        return String.join("\n", csvData);
    }

    /**
     * Get bookings by user ID with pagination
     */
    public List<Booking> getBookingsByUser(Long userId, Pageable pageable) {
        logger.debug("Fetching bookings by user ID: {} with pagination", userId);
        return getBookingsByUserId(userId, pageable).getContent();
    }
    
    /**
     * Get bookings by user ID with pagination
     */
    public Page<Booking> getBookingsByUserId(Long userId, Pageable pageable) {
        logger.debug("Fetching bookings by user ID: {} with pagination", userId);
        return bookingRepository.getBookingsByUserId(userId, pageable);
    }

    /**
     * Get bookings by property ID with pagination
     */
    public List<Booking> getBookingsByProperty(Long propertyId, Pageable pageable) {
        logger.debug("Fetching bookings by property ID: {} with pagination", propertyId);
        return getBookingsByPropertyId(propertyId, pageable).getContent();
    }

    /**
     * Get bookings by host ID with pagination
     */
    public List<Booking> getBookingsByHost(Long hostId, Pageable pageable) {
        logger.debug("Fetching bookings by host ID: {} with pagination", hostId);
        return getBookingsByHostId(hostId, pageable).getContent();
    }

    /**
     * Get upcoming bookings for user
     */
    public List<Booking> getUpcomingBookings(Long userId) {
        logger.debug("Fetching upcoming bookings for user ID: {}", userId);
        return getUpcomingBookings(PageRequest.of(0, 100)).getContent();
    }

    /**
     * Get current bookings for user
     */
    public List<Booking> getCurrentBookings(Long userId) {
        logger.debug("Fetching current bookings for user ID: {}", userId);
        return getCurrentBookings(PageRequest.of(0, 100)).getContent();
    }

    /**
     * Get past bookings for user
     */
    public List<Booking> getPastBookings(Long userId) {
        logger.debug("Fetching past bookings for user ID: {}", userId);
        return getPastBookings(PageRequest.of(0, 100)).getContent();
    }
}


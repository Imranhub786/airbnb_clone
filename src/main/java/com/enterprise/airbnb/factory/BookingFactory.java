package com.enterprise.airbnb.factory;

import com.enterprise.airbnb.model.Booking;
import com.enterprise.airbnb.model.BookingStatus;
import com.enterprise.airbnb.model.PaymentStatus;
import com.enterprise.airbnb.model.Property;
import com.enterprise.airbnb.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Factory for creating Booking objects with proper initialization
 */
@Component
public class BookingFactory {

    private static final Logger logger = LoggerFactory.getLogger(BookingFactory.class);

    /**
     * Create a new booking with basic information
     */
    public Booking createBooking(Property property, User guest, LocalDate checkInDate, 
                               LocalDate checkOutDate, Integer numberOfGuests) {
        logger.info("Creating new booking for property: {} and guest: {}", property.getId(), guest.getId());
        
        // Validate dates
        if (checkInDate.isAfter(checkOutDate)) {
            throw new IllegalArgumentException("Check-in date cannot be after check-out date");
        }
        
        if (checkInDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past");
        }
        
        // Validate number of guests
        if (numberOfGuests > property.getMaxGuests()) {
            throw new IllegalArgumentException("Number of guests exceeds property maximum capacity");
        }
        
        // Calculate number of nights
        int numberOfNights = (int) checkInDate.until(checkOutDate).getDays();
        if (numberOfNights < property.getMinimumNights()) {
            throw new IllegalArgumentException("Booking duration is less than minimum nights required");
        }
        
        if (numberOfNights > property.getMaximumNights()) {
            throw new IllegalArgumentException("Booking duration exceeds maximum nights allowed");
        }
        
        // Calculate base price
        BigDecimal basePrice = property.getPricePerNight().multiply(BigDecimal.valueOf(numberOfNights));
        
        // Calculate total price
        BigDecimal totalPrice = basePrice;
        if (property.getCleaningFee() != null) {
            totalPrice = totalPrice.add(property.getCleaningFee());
        }
        if (property.getServiceFee() != null) {
            totalPrice = totalPrice.add(property.getServiceFee());
        }
        
        // Create booking
        Booking booking = new Booking();
        booking.setProperty(property);
        booking.setGuest(guest);
        booking.setCheckInDate(checkInDate);
        booking.setCheckOutDate(checkOutDate);
        booking.setNumberOfGuests(numberOfGuests);
        booking.setNumberOfNights(numberOfNights);
        booking.setBasePrice(basePrice);
        booking.setCleaningFee(property.getCleaningFee());
        booking.setServiceFee(property.getServiceFee());
        booking.setSecurityDeposit(property.getSecurityDeposit());
        booking.setTotalPrice(totalPrice);
        booking.setCurrency(property.getCurrency());
        booking.setStatus(BookingStatus.PENDING);
        booking.setPaymentStatus(PaymentStatus.PENDING);
        booking.setIsInstantBook(property.getInstantBook());
        booking.setBookingSource("WEB");
        booking.setCreatedAt(LocalDateTime.now());
        
        // Generate booking reference
        booking.setBookingReference(generateBookingReference());
        
        logger.info("Booking created successfully with reference: {}", booking.getBookingReference());
        return booking;
    }

    /**
     * Create a booking with special requests
     */
    public Booking createBookingWithRequests(Property property, User guest, LocalDate checkInDate, 
                                           LocalDate checkOutDate, Integer numberOfGuests, 
                                           String specialRequests, String guestMessage) {
        logger.info("Creating new booking with special requests for property: {} and guest: {}", 
                   property.getId(), guest.getId());
        
        Booking booking = createBooking(property, guest, checkInDate, checkOutDate, numberOfGuests);
        booking.setSpecialRequests(specialRequests);
        booking.setGuestMessage(guestMessage);
        
        logger.info("Booking with special requests created successfully with reference: {}", 
                   booking.getBookingReference());
        return booking;
    }

    /**
     * Create an instant booking
     */
    public Booking createInstantBooking(Property property, User guest, LocalDate checkInDate, 
                                      LocalDate checkOutDate, Integer numberOfGuests) {
        logger.info("Creating instant booking for property: {} and guest: {}", property.getId(), guest.getId());
        
        if (!property.getInstantBook()) {
            throw new IllegalArgumentException("Property does not support instant booking");
        }
        
        Booking booking = createBooking(property, guest, checkInDate, checkOutDate, numberOfGuests);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setIsInstantBook(true);
        
        logger.info("Instant booking created successfully with reference: {}", booking.getBookingReference());
        return booking;
    }

    /**
     * Create a booking with custom pricing
     */
    public Booking createBookingWithCustomPricing(Property property, User guest, LocalDate checkInDate, 
                                                LocalDate checkOutDate, Integer numberOfGuests, 
                                                BigDecimal customPricePerNight) {
        logger.info("Creating booking with custom pricing for property: {} and guest: {}", 
                   property.getId(), guest.getId());
        
        // Validate dates
        if (checkInDate.isAfter(checkOutDate)) {
            throw new IllegalArgumentException("Check-in date cannot be after check-out date");
        }
        
        if (checkInDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past");
        }
        
        // Validate number of guests
        if (numberOfGuests > property.getMaxGuests()) {
            throw new IllegalArgumentException("Number of guests exceeds property maximum capacity");
        }
        
        // Calculate number of nights
        int numberOfNights = (int) checkInDate.until(checkOutDate).getDays();
        if (numberOfNights < property.getMinimumNights()) {
            throw new IllegalArgumentException("Booking duration is less than minimum nights required");
        }
        
        if (numberOfNights > property.getMaximumNights()) {
            throw new IllegalArgumentException("Booking duration exceeds maximum nights allowed");
        }
        
        // Calculate base price with custom rate
        BigDecimal basePrice = customPricePerNight.multiply(BigDecimal.valueOf(numberOfNights));
        
        // Calculate total price
        BigDecimal totalPrice = basePrice;
        if (property.getCleaningFee() != null) {
            totalPrice = totalPrice.add(property.getCleaningFee());
        }
        if (property.getServiceFee() != null) {
            totalPrice = totalPrice.add(property.getServiceFee());
        }
        
        // Create booking
        Booking booking = new Booking();
        booking.setProperty(property);
        booking.setGuest(guest);
        booking.setCheckInDate(checkInDate);
        booking.setCheckOutDate(checkOutDate);
        booking.setNumberOfGuests(numberOfGuests);
        booking.setNumberOfNights(numberOfNights);
        booking.setBasePrice(basePrice);
        booking.setCleaningFee(property.getCleaningFee());
        booking.setServiceFee(property.getServiceFee());
        booking.setSecurityDeposit(property.getSecurityDeposit());
        booking.setTotalPrice(totalPrice);
        booking.setCurrency(property.getCurrency());
        booking.setStatus(BookingStatus.PENDING);
        booking.setPaymentStatus(PaymentStatus.PENDING);
        booking.setIsInstantBook(property.getInstantBook());
        booking.setBookingSource("WEB");
        booking.setCreatedAt(LocalDateTime.now());
        
        // Generate booking reference
        booking.setBookingReference(generateBookingReference());
        
        logger.info("Booking with custom pricing created successfully with reference: {}", 
                   booking.getBookingReference());
        return booking;
    }

    /**
     * Create a booking for mobile app
     */
    public Booking createMobileBooking(Property property, User guest, LocalDate checkInDate, 
                                     LocalDate checkOutDate, Integer numberOfGuests, 
                                     String userAgent, String ipAddress) {
        logger.info("Creating mobile booking for property: {} and guest: {}", property.getId(), guest.getId());
        
        Booking booking = createBooking(property, guest, checkInDate, checkOutDate, numberOfGuests);
        booking.setBookingSource("MOBILE");
        booking.setUserAgent(userAgent);
        booking.setIpAddress(ipAddress);
        
        logger.info("Mobile booking created successfully with reference: {}", booking.getBookingReference());
        return booking;
    }

    /**
     * Create a booking for API integration
     */
    public Booking createApiBooking(Property property, User guest, LocalDate checkInDate, 
                                  LocalDate checkOutDate, Integer numberOfGuests, 
                                  String apiSource) {
        logger.info("Creating API booking for property: {} and guest: {}", property.getId(), guest.getId());
        
        Booking booking = createBooking(property, guest, checkInDate, checkOutDate, numberOfGuests);
        booking.setBookingSource(apiSource);
        
        logger.info("API booking created successfully with reference: {}", booking.getBookingReference());
        return booking;
    }

    /**
     * Create a booking with extended stay
     */
    public Booking createExtendedStayBooking(Property property, User guest, LocalDate checkInDate, 
                                           LocalDate checkOutDate, Integer numberOfGuests, 
                                           BigDecimal extendedStayDiscount) {
        logger.info("Creating extended stay booking for property: {} and guest: {}", 
                   property.getId(), guest.getId());
        
        // Validate dates
        if (checkInDate.isAfter(checkOutDate)) {
            throw new IllegalArgumentException("Check-in date cannot be after check-out date");
        }
        
        if (checkInDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past");
        }
        
        // Validate number of guests
        if (numberOfGuests > property.getMaxGuests()) {
            throw new IllegalArgumentException("Number of guests exceeds property maximum capacity");
        }
        
        // Calculate number of nights
        int numberOfNights = (int) checkInDate.until(checkOutDate).getDays();
        if (numberOfNights < property.getMinimumNights()) {
            throw new IllegalArgumentException("Booking duration is less than minimum nights required");
        }
        
        if (numberOfNights > property.getMaximumNights()) {
            throw new IllegalArgumentException("Booking duration exceeds maximum nights allowed");
        }
        
        // Calculate base price with extended stay discount
        BigDecimal basePrice = property.getPricePerNight().multiply(BigDecimal.valueOf(numberOfNights));
        if (extendedStayDiscount != null && extendedStayDiscount.compareTo(BigDecimal.ZERO) > 0) {
            basePrice = basePrice.subtract(extendedStayDiscount);
        }
        
        // Calculate total price
        BigDecimal totalPrice = basePrice;
        if (property.getCleaningFee() != null) {
            totalPrice = totalPrice.add(property.getCleaningFee());
        }
        if (property.getServiceFee() != null) {
            totalPrice = totalPrice.add(property.getServiceFee());
        }
        
        // Create booking
        Booking booking = new Booking();
        booking.setProperty(property);
        booking.setGuest(guest);
        booking.setCheckInDate(checkInDate);
        booking.setCheckOutDate(checkOutDate);
        booking.setNumberOfGuests(numberOfGuests);
        booking.setNumberOfNights(numberOfNights);
        booking.setBasePrice(basePrice);
        booking.setCleaningFee(property.getCleaningFee());
        booking.setServiceFee(property.getServiceFee());
        booking.setSecurityDeposit(property.getSecurityDeposit());
        booking.setTotalPrice(totalPrice);
        booking.setCurrency(property.getCurrency());
        booking.setStatus(BookingStatus.PENDING);
        booking.setPaymentStatus(PaymentStatus.PENDING);
        booking.setIsInstantBook(property.getInstantBook());
        booking.setBookingSource("WEB");
        booking.setCreatedAt(LocalDateTime.now());
        
        // Generate booking reference
        booking.setBookingReference(generateBookingReference());
        
        logger.info("Extended stay booking created successfully with reference: {}", 
                   booking.getBookingReference());
        return booking;
    }

    /**
     * Create a booking with group discount
     */
    public Booking createGroupBooking(Property property, User guest, LocalDate checkInDate, 
                                    LocalDate checkOutDate, Integer numberOfGuests, 
                                    BigDecimal groupDiscount) {
        logger.info("Creating group booking for property: {} and guest: {}", property.getId(), guest.getId());
        
        // Validate dates
        if (checkInDate.isAfter(checkOutDate)) {
            throw new IllegalArgumentException("Check-in date cannot be after check-out date");
        }
        
        if (checkInDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past");
        }
        
        // Validate number of guests
        if (numberOfGuests > property.getMaxGuests()) {
            throw new IllegalArgumentException("Number of guests exceeds property maximum capacity");
        }
        
        // Calculate number of nights
        int numberOfNights = (int) checkInDate.until(checkOutDate).getDays();
        if (numberOfNights < property.getMinimumNights()) {
            throw new IllegalArgumentException("Booking duration is less than minimum nights required");
        }
        
        if (numberOfNights > property.getMaximumNights()) {
            throw new IllegalArgumentException("Booking duration exceeds maximum nights allowed");
        }
        
        // Calculate base price with group discount
        BigDecimal basePrice = property.getPricePerNight().multiply(BigDecimal.valueOf(numberOfNights));
        if (groupDiscount != null && groupDiscount.compareTo(BigDecimal.ZERO) > 0) {
            basePrice = basePrice.subtract(groupDiscount);
        }
        
        // Calculate total price
        BigDecimal totalPrice = basePrice;
        if (property.getCleaningFee() != null) {
            totalPrice = totalPrice.add(property.getCleaningFee());
        }
        if (property.getServiceFee() != null) {
            totalPrice = totalPrice.add(property.getServiceFee());
        }
        
        // Create booking
        Booking booking = new Booking();
        booking.setProperty(property);
        booking.setGuest(guest);
        booking.setCheckInDate(checkInDate);
        booking.setCheckOutDate(checkOutDate);
        booking.setNumberOfGuests(numberOfGuests);
        booking.setNumberOfNights(numberOfNights);
        booking.setBasePrice(basePrice);
        booking.setCleaningFee(property.getCleaningFee());
        booking.setServiceFee(property.getServiceFee());
        booking.setSecurityDeposit(property.getSecurityDeposit());
        booking.setTotalPrice(totalPrice);
        booking.setCurrency(property.getCurrency());
        booking.setStatus(BookingStatus.PENDING);
        booking.setPaymentStatus(PaymentStatus.PENDING);
        booking.setIsInstantBook(property.getInstantBook());
        booking.setBookingSource("WEB");
        booking.setCreatedAt(LocalDateTime.now());
        
        // Generate booking reference
        booking.setBookingReference(generateBookingReference());
        
        logger.info("Group booking created successfully with reference: {}", booking.getBookingReference());
        return booking;
    }

    /**
     * Generate unique booking reference
     */
    private String generateBookingReference() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "BK" + timestamp.substring(timestamp.length() - 8) + random;
    }

    /**
     * Validate booking dates
     */
    public boolean isValidBookingDates(LocalDate checkInDate, LocalDate checkOutDate, 
                                     Property property) {
        // Check if dates are valid
        if (checkInDate.isAfter(checkOutDate)) {
            return false;
        }
        
        if (checkInDate.isBefore(LocalDate.now())) {
            return false;
        }
        
        // Check minimum and maximum nights
        int numberOfNights = (int) checkInDate.until(checkOutDate).getDays();
        if (numberOfNights < property.getMinimumNights()) {
            return false;
        }
        
        if (numberOfNights > property.getMaximumNights()) {
            return false;
        }
        
        return true;
    }

    /**
     * Calculate booking total price
     */
    public BigDecimal calculateBookingTotal(Property property, int numberOfNights) {
        BigDecimal basePrice = property.getPricePerNight().multiply(BigDecimal.valueOf(numberOfNights));
        BigDecimal total = basePrice;
        
        if (property.getCleaningFee() != null) {
            total = total.add(property.getCleaningFee());
        }
        
        if (property.getServiceFee() != null) {
            total = total.add(property.getServiceFee());
        }
        
        return total;
    }

    /**
     * Calculate booking total price with discount
     */
    public BigDecimal calculateBookingTotalWithDiscount(Property property, int numberOfNights, 
                                                       BigDecimal discount) {
        BigDecimal basePrice = property.getPricePerNight().multiply(BigDecimal.valueOf(numberOfNights));
        BigDecimal total = basePrice;
        
        if (property.getCleaningFee() != null) {
            total = total.add(property.getCleaningFee());
        }
        
        if (property.getServiceFee() != null) {
            total = total.add(property.getServiceFee());
        }
        
        if (discount != null && discount.compareTo(BigDecimal.ZERO) > 0) {
            total = total.subtract(discount);
        }
        
        return total;
    }
}



package com.enterprise.airbnb.config;

import com.enterprise.airbnb.model.*;
import com.enterprise.airbnb.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Data initializer for sample data
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PropertyAvailabilityRepository propertyAvailabilityRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        logger.info("Starting data initialization...");
        
        // Check if data already exists
        if (userRepository.count() > 0) {
            logger.info("Data already exists, skipping initialization");
            return;
        }
        
        try {
            // Create sample users
            createSampleUsers();
            
            // Create sample properties
            createSampleProperties();
            
            // Create sample bookings
            createSampleBookings();
            
            // Create sample reviews
            createSampleReviews();
            
            // Create sample payments
            createSamplePayments();
            
            // Create sample property availability
            createSamplePropertyAvailability();
            
            logger.info("Data initialization completed successfully");
            
        } catch (Exception e) {
            logger.error("Error during data initialization: {}", e.getMessage());
        }
    }

    private void createSampleUsers() {
        logger.info("Creating sample users...");
        
        // Create admin user
        User admin = new User();
        admin.setEmail("admin@airbnb.com");
        admin.setPassword("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyVhHOn8V5v2oZ3L6z5v5v5v5v5v"); // "admin123"
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setRole(UserRole.ADMIN);
        admin.setAccountStatus(AccountStatus.ACTIVE);
        admin.setPhoneNumber("+1-555-0001");
        admin.setDateOfBirth(LocalDate.of(1980, 1, 1).atStartOfDay());
        admin.setCreatedAt(LocalDateTime.now());
        admin.setUpdatedAt(LocalDateTime.now());
        userRepository.save(admin);
        
        // Create host users
        User host1 = new User();
        host1.setEmail("host1@airbnb.com");
        host1.setPassword("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyVhHOn8V5v2oZ3L6z5v5v5v5v5v"); // "host123"
        host1.setFirstName("John");
        host1.setLastName("Smith");
        host1.setRole(UserRole.HOST);
        host1.setAccountStatus(AccountStatus.ACTIVE);
        host1.setPhoneNumber("+1-555-0002");
        host1.setDateOfBirth(LocalDate.of(1985, 5, 15).atStartOfDay());
        host1.setCreatedAt(LocalDateTime.now());
        host1.setUpdatedAt(LocalDateTime.now());
        userRepository.save(host1);
        
        User host2 = new User();
        host2.setEmail("host2@airbnb.com");
        host2.setPassword("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyVhHOn8V5v2oZ3L6z5v5v5v5v5v"); // "host123"
        host2.setFirstName("Sarah");
        host2.setLastName("Johnson");
        host2.setRole(UserRole.SUPER_HOST);
        host2.setAccountStatus(AccountStatus.ACTIVE);
        host2.setPhoneNumber("+1-555-0003");
        host2.setDateOfBirth(LocalDate.of(1982, 8, 22).atStartOfDay());
        host2.setCreatedAt(LocalDateTime.now());
        host2.setUpdatedAt(LocalDateTime.now());
        userRepository.save(host2);
        
        // Create guest users
        User guest1 = new User();
        guest1.setEmail("guest1@airbnb.com");
        guest1.setPassword("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyVhHOn8V5v2oZ3L6z5v5v5v5v5v"); // "guest123"
        guest1.setFirstName("Mike");
        guest1.setLastName("Wilson");
        guest1.setRole(UserRole.GUEST);
        guest1.setAccountStatus(AccountStatus.ACTIVE);
        guest1.setPhoneNumber("+1-555-0004");
        guest1.setDateOfBirth(LocalDate.of(1990, 3, 10).atStartOfDay());
        guest1.setCreatedAt(LocalDateTime.now());
        guest1.setUpdatedAt(LocalDateTime.now());
        userRepository.save(guest1);
        
        User guest2 = new User();
        guest2.setEmail("guest2@airbnb.com");
        guest2.setPassword("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyVhHOn8V5v2oZ3L6z5v5v5v5v5v"); // "guest123"
        guest2.setFirstName("Emily");
        guest2.setLastName("Brown");
        guest2.setRole(UserRole.GUEST);
        guest2.setAccountStatus(AccountStatus.ACTIVE);
        guest2.setPhoneNumber("+1-555-0005");
        guest2.setDateOfBirth(LocalDate.of(1988, 12, 5).atStartOfDay());
        guest2.setCreatedAt(LocalDateTime.now());
        guest2.setUpdatedAt(LocalDateTime.now());
        userRepository.save(guest2);
        
        logger.info("Created {} sample users", userRepository.count());
    }

    private void createSampleProperties() {
        logger.info("Creating sample properties...");
        
        // Get hosts
        User host1 = userRepository.findByEmail("host1@airbnb.com").orElse(null);
        User host2 = userRepository.findByEmail("host2@airbnb.com").orElse(null);
        
        if (host1 == null || host2 == null) {
            logger.error("Hosts not found, cannot create properties");
            return;
        }
        
        // Create property 1
        Property property1 = new Property();
        property1.setTitle("Beautiful Beach House in Malibu");
        property1.setDescription("Stunning oceanfront property with panoramic views of the Pacific Ocean. Perfect for a relaxing getaway.");
        property1.setPropertyType(PropertyType.HOUSE);
        property1.setStatus(PropertyStatus.ACTIVE);
        property1.setHost(host1);
        property1.setAddress(createAddress("123 Ocean Drive", "Malibu", "CA", "90265", "United States"));
        property1.setLatitude(34.0259);
        property1.setLongitude(-118.7798);
        property1.setPricePerNight(new BigDecimal("450.00"));
        property1.setCurrency("USD");
        property1.setMaxGuests(8);
        property1.setBedrooms(4);
        property1.setBathrooms(3);
        property1.setSquareFootage(2500);
        property1.setAmenities(Arrays.asList(Amenity.WIFI, Amenity.POOL, Amenity.PARKING, Amenity.OCEAN_VIEW));
        property1.setHouseRules("No smoking, No pets, No parties");
        property1.setCancellationPolicy("Flexible");
        property1.setMinimumStay(2);
        property1.setMaximumStay(30);
        property1.setCheckInTime("15:00");
        property1.setCheckOutTime("11:00");
        property1.setInstantBook(true);
        property1.setIsPetFriendly(false);
        property1.setIsSmokingAllowed(false);
        property1.setIsEntirePlace(true);
        property1.setCreatedAt(LocalDateTime.now());
        property1.setUpdatedAt(LocalDateTime.now());
        propertyRepository.save(property1);
        
        // Create property 2
        Property property2 = new Property();
        property2.setTitle("Cozy Downtown Apartment");
        property2.setDescription("Modern apartment in the heart of downtown with easy access to restaurants, shops, and public transportation.");
        property2.setPropertyType(PropertyType.APARTMENT);
        property2.setStatus(PropertyStatus.ACTIVE);
        property2.setHost(host2);
        property2.setAddress(createAddress("456 Main Street", "New York", "NY", "10001", "United States"));
        property2.setLatitude(40.7589);
        property2.setLongitude(-73.9851);
        property2.setPricePerNight(new BigDecimal("200.00"));
        property2.setCurrency("USD");
        property2.setMaxGuests(4);
        property2.setBedrooms(2);
        property2.setBathrooms(1);
        property2.setSquareFootage(800);
        property2.setAmenities(Arrays.asList(Amenity.WIFI, Amenity.PARKING, Amenity.GYM, Amenity.ELEVATOR));
        property2.setHouseRules("No smoking, No pets, Quiet hours after 10 PM");
        property2.setCancellationPolicy("Moderate");
        property2.setMinimumStay(1);
        property2.setMaximumStay(14);
        property2.setCheckInTime("16:00");
        property2.setCheckOutTime("10:00");
        property2.setInstantBook(false);
        property2.setIsPetFriendly(false);
        property2.setIsSmokingAllowed(false);
        property2.setIsEntirePlace(true);
        property2.setCreatedAt(LocalDateTime.now());
        property2.setUpdatedAt(LocalDateTime.now());
        propertyRepository.save(property2);
        
        // Create property 3
        Property property3 = new Property();
        property3.setTitle("Mountain Cabin Retreat");
        property3.setDescription("Rustic cabin in the mountains perfect for hiking, fishing, and enjoying nature.");
        property3.setPropertyType(PropertyType.CABIN);
        property3.setStatus(PropertyStatus.ACTIVE);
        property3.setHost(host1);
        property3.setAddress(createAddress("789 Mountain Road", "Aspen", "CO", "81611", "United States"));
        property3.setLatitude(39.1911);
        property3.setLongitude(-106.8175);
        property3.setPricePerNight(new BigDecimal("300.00"));
        property3.setCurrency("USD");
        property3.setMaxGuests(6);
        property3.setBedrooms(3);
        property3.setBathrooms(2);
        property3.setSquareFootage(1200);
        property3.setAmenities(Arrays.asList(Amenity.WIFI, Amenity.FIREPLACE, Amenity.HOT_TUB, Amenity.HIKING_TRAILS));
        property3.setHouseRules("No smoking, Pets allowed, Respect nature");
        property3.setCancellationPolicy("Strict");
        property3.setMinimumStay(3);
        property3.setMaximumStay(21);
        property3.setCheckInTime("14:00");
        property3.setCheckOutTime("12:00");
        property3.setInstantBook(true);
        property3.setIsPetFriendly(true);
        property3.setIsSmokingAllowed(false);
        property3.setIsEntirePlace(true);
        property3.setCreatedAt(LocalDateTime.now());
        property3.setUpdatedAt(LocalDateTime.now());
        propertyRepository.save(property3);
        
        logger.info("Created {} sample properties", propertyRepository.count());
    }

    private void createSampleBookings() {
        logger.info("Creating sample bookings...");
        
        // Get users and properties
        User guest1 = userRepository.findByEmail("guest1@airbnb.com").orElse(null);
        User guest2 = userRepository.findByEmail("guest2@airbnb.com").orElse(null);
        List<Property> properties = propertyRepository.findAll();
        
        if (guest1 == null || guest2 == null || properties.isEmpty()) {
            logger.error("Users or properties not found, cannot create bookings");
            return;
        }
        
        // Create booking 1
        Booking booking1 = new Booking();
        booking1.setProperty(properties.get(0));
        booking1.setGuest(guest1);
        booking1.setCheckInDate(LocalDate.now().plusDays(7));
        booking1.setCheckOutDate(LocalDate.now().plusDays(10));
        booking1.setNumberOfGuests(4);
        booking1.setStatus(BookingStatus.CONFIRMED);
        booking1.setTotalAmount(new BigDecimal("1350.00"));
        booking1.setCurrency("USD");
        booking1.setSpecialRequests("Late check-in requested");
        booking1.setCreatedAt(LocalDateTime.now());
        booking1.setUpdatedAt(LocalDateTime.now());
        bookingRepository.save(booking1);
        
        // Create booking 2
        Booking booking2 = new Booking();
        booking2.setProperty(properties.get(1));
        booking2.setGuest(guest2);
        booking2.setCheckInDate(LocalDate.now().plusDays(14));
        booking2.setCheckOutDate(LocalDate.now().plusDays(16));
        booking2.setNumberOfGuests(2);
        booking2.setStatus(BookingStatus.PENDING);
        booking2.setTotalAmount(new BigDecimal("400.00"));
        booking2.setCurrency("USD");
        booking2.setSpecialRequests("Early check-in if possible");
        booking2.setCreatedAt(LocalDateTime.now());
        booking2.setUpdatedAt(LocalDateTime.now());
        bookingRepository.save(booking2);
        
        logger.info("Created {} sample bookings", bookingRepository.count());
    }

    private void createSampleReviews() {
        logger.info("Creating sample reviews...");
        
        // Get bookings
        List<Booking> bookings = bookingRepository.findAll();
        
        if (bookings.isEmpty()) {
            logger.error("No bookings found, cannot create reviews");
            return;
        }
        
        // Create review 1
        Review review1 = new Review();
        review1.setProperty(bookings.get(0).getProperty());
        review1.setBooking(bookings.get(0));
        review1.setReviewer(bookings.get(0).getGuest());
        review1.setReviewee(bookings.get(0).getProperty().getHost());
        review1.setType(ReviewType.PROPERTY);
        review1.setRating(5);
        review1.setComment("Amazing property with breathtaking ocean views! The host was very responsive and helpful.");
        review1.setStatus(ReviewStatus.APPROVED);
        review1.setCreatedAt(LocalDateTime.now());
        review1.setUpdatedAt(LocalDateTime.now());
        reviewRepository.save(review1);
        
        // Create review 2
        Review review2 = new Review();
        review2.setProperty(bookings.get(0).getProperty());
        review2.setBooking(bookings.get(0));
        review2.setReviewer(bookings.get(0).getProperty().getHost());
        review2.setReviewee(bookings.get(0).getGuest());
        review2.setType(ReviewType.GUEST);
        review2.setRating(5);
        review2.setComment("Excellent guest! Very respectful and left the property in perfect condition.");
        review2.setStatus(ReviewStatus.APPROVED);
        review2.setCreatedAt(LocalDateTime.now());
        review2.setUpdatedAt(LocalDateTime.now());
        reviewRepository.save(review2);
        
        logger.info("Created {} sample reviews", reviewRepository.count());
    }

    private void createSamplePayments() {
        logger.info("Creating sample payments...");
        
        // Get bookings
        List<Booking> bookings = bookingRepository.findAll();
        
        if (bookings.isEmpty()) {
            logger.error("No bookings found, cannot create payments");
            return;
        }
        
        // Create payment 1
        Payment payment1 = new Payment();
        payment1.setBooking(bookings.get(0));
        payment1.setAmount(bookings.get(0).getTotalAmount());
        payment1.setCurrency(bookings.get(0).getCurrency());
        payment1.setPaymentMethod(PaymentMethod.PAYPAL);
        payment1.setPaymentType(PaymentType.BOOKING);
        payment1.setStatus(PaymentStatus.COMPLETED);
        payment1.setTransactionId("PAYPAL_" + System.currentTimeMillis());
        payment1.setProcessedAt(LocalDateTime.now());
        payment1.setCreatedAt(LocalDateTime.now());
        payment1.setUpdatedAt(LocalDateTime.now());
        paymentRepository.save(payment1);
        
        logger.info("Created {} sample payments", paymentRepository.count());
    }

    private void createSamplePropertyAvailability() {
        logger.info("Creating sample property availability...");
        
        // Get properties
        List<Property> properties = propertyRepository.findAll();
        
        if (properties.isEmpty()) {
            logger.error("No properties found, cannot create availability");
            return;
        }
        
        // Create availability for each property
        for (Property property : properties) {
            PropertyAvailability availability = new PropertyAvailability();
            availability.setProperty(property);
            availability.setDate(LocalDate.now().plusDays(1));
            availability.setStatus(AvailabilityStatus.AVAILABLE);
            availability.setPrice(property.getPricePerNight());
            availability.setCreatedAt(LocalDateTime.now());
            availability.setUpdatedAt(LocalDateTime.now());
            propertyAvailabilityRepository.save(availability);
        }
        
        logger.info("Created {} sample property availability records", propertyAvailabilityRepository.count());
    }

    private Address createAddress(String street, String city, String stateProvince, String postalCode, String country) {
        Address address = new Address();
        address.setStreet(street);
        address.setCity(city);
        address.setStateProvince(stateProvince);
        address.setPostalCode(postalCode);
        address.setCountry(country);
        return address;
    }
}

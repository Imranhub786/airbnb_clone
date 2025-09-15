package com.enterprise.airbnb.integration;

import com.enterprise.airbnb.model.Property;
import com.enterprise.airbnb.model.PropertyType;
import com.enterprise.airbnb.model.PropertyStatus;
import com.enterprise.airbnb.model.User;
import com.enterprise.airbnb.model.UserRole;
import com.enterprise.airbnb.repository.PropertyRepository;
import com.enterprise.airbnb.repository.UserRepository;
import com.enterprise.airbnb.service.PropertyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PropertyIntegrationTest {

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private UserRepository userRepository;

    private User testHost;
    private Property testProperty;

    @BeforeEach
    void setUp() {
        // Clean up existing data
        propertyRepository.deleteAll();
        userRepository.deleteAll();

        // Create test host
        testHost = new User();
        testHost.setFirstName("Jane");
        testHost.setLastName("Host");
        testHost.setEmail("jane.host@example.com");
        testHost.setPassword("password123");
        testHost.setRole(UserRole.HOST);
        testHost.setCreatedAt(LocalDateTime.now());
        testHost = userRepository.save(testHost);

        // Create test property
        testProperty = new Property();
        testProperty.setTitle("Beautiful Apartment");
        testProperty.setDescription("A lovely apartment in the city center");
        testProperty.setPropertyType(PropertyType.APARTMENT);
        testProperty.setPricePerNight(new BigDecimal("100.00"));
        testProperty.setMaxGuests(4);
        testProperty.setBedrooms(2);
        testProperty.setBathrooms(1);
        testProperty.setStatus(PropertyStatus.ACTIVE);
        testProperty.setHost(testHost);
        testProperty.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateProperty_Integration() {
        // When
        Property savedProperty = propertyService.createProperty(testProperty);

        // Then
        assertNotNull(savedProperty.getId());
        assertEquals("Beautiful Apartment", savedProperty.getTitle());
        assertEquals(PropertyType.APARTMENT, savedProperty.getPropertyType());
        assertEquals(new BigDecimal("100.00"), savedProperty.getPricePerNight());
        assertEquals(PropertyStatus.ACTIVE, savedProperty.getStatus());
        assertEquals(testHost.getId(), savedProperty.getHost().getId());

        // Verify it's saved in database
        Optional<Property> foundProperty = propertyRepository.findById(savedProperty.getId());
        assertTrue(foundProperty.isPresent());
        assertEquals(savedProperty.getTitle(), foundProperty.get().getTitle());
    }

    @Test
    void testGetPropertyById_Integration() {
        // Given
        Property savedProperty = propertyRepository.save(testProperty);

        // When
        Optional<Property> foundProperty = propertyService.getPropertyById(savedProperty.getId());

        // Then
        assertTrue(foundProperty.isPresent());
        assertEquals(savedProperty.getId(), foundProperty.get().getId());
        assertEquals("Beautiful Apartment", foundProperty.get().getTitle());
    }

    @Test
    void testUpdateProperty_Integration() {
        // Given
        Property savedProperty = propertyRepository.save(testProperty);
        Property updatedProperty = new Property();
        updatedProperty.setTitle("Updated Apartment");
        updatedProperty.setDescription("Updated description");
        updatedProperty.setPricePerNight(new BigDecimal("120.00"));
        updatedProperty.setMaxGuests(6);
        updatedProperty.setBedrooms(3);
        updatedProperty.setBathrooms(2);

        // When
        Property result = propertyService.updateProperty(savedProperty.getId(), updatedProperty);

        // Then
        assertEquals(savedProperty.getId(), result.getId());
        assertEquals("Updated Apartment", result.getTitle());
        assertEquals("Updated description", result.getDescription());
        assertEquals(new BigDecimal("120.00"), result.getPricePerNight());
        assertEquals(6, result.getMaxGuests());
        assertEquals(3, result.getBedrooms());
        assertEquals(2, result.getBathrooms());

        // Verify it's updated in database
        Optional<Property> foundProperty = propertyRepository.findById(savedProperty.getId());
        assertTrue(foundProperty.isPresent());
        assertEquals("Updated Apartment", foundProperty.get().getTitle());
    }

    @Test
    void testDeleteProperty_Integration() {
        // Given
        Property savedProperty = propertyRepository.save(testProperty);

        // When
        boolean deleted = propertyService.deleteProperty(savedProperty.getId());

        // Then
        assertTrue(deleted);

        // Verify it's deleted from database
        Optional<Property> foundProperty = propertyRepository.findById(savedProperty.getId());
        assertFalse(foundProperty.isPresent());
    }

    @Test
    void testGetAllProperties_Integration() {
        // Given
        Property property1 = propertyRepository.save(testProperty);

        Property property2 = new Property();
        property2.setTitle("Another Property");
        property2.setDescription("Another description");
        property2.setPropertyType(PropertyType.HOUSE);
        property2.setPricePerNight(new BigDecimal("200.00"));
        property2.setMaxGuests(8);
        property2.setBedrooms(4);
        property2.setBathrooms(3);
        property2.setStatus(PropertyStatus.ACTIVE);
        property2.setHost(testHost);
        property2.setCreatedAt(LocalDateTime.now());
        Property savedProperty2 = propertyRepository.save(property2);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Property> properties = propertyService.getAllProperties(pageable);

        // Then
        assertEquals(2, properties.getTotalElements());
        assertEquals(2, properties.getContent().size());
        assertTrue(properties.getContent().stream()
                .anyMatch(p -> p.getTitle().equals("Beautiful Apartment")));
        assertTrue(properties.getContent().stream()
                .anyMatch(p -> p.getTitle().equals("Another Property")));
    }

    @Test
    void testSearchProperties_Integration() {
        // Given
        Property apartment = propertyRepository.save(testProperty);

        Property house = new Property();
        house.setTitle("Beautiful House");
        house.setDescription("A lovely house in the suburbs");
        house.setPropertyType(PropertyType.HOUSE);
        house.setPricePerNight(new BigDecimal("200.00"));
        house.setMaxGuests(8);
        house.setBedrooms(4);
        house.setBathrooms(3);
        house.setStatus(PropertyStatus.ACTIVE);
        house.setHost(testHost);
        house.setCreatedAt(LocalDateTime.now());
        Property savedHouse = propertyRepository.save(house);

        Pageable pageable = PageRequest.of(0, 10);

        // When - Search by property type
        Page<Property> apartments = propertyService.searchProperties(
                null, null, PropertyType.APARTMENT, null, null, null, null, null, null, pageable);

        // Then
        assertEquals(1, apartments.getTotalElements());
        assertEquals(PropertyType.APARTMENT, apartments.getContent().get(0).getPropertyType());

        // When - Search by price range
        Page<Property> affordableProperties = propertyService.searchProperties(
                null, null, null, new BigDecimal("50"), new BigDecimal("150"), null, null, null, null, pageable);

        // Then
        assertEquals(1, affordableProperties.getTotalElements());
        assertTrue(affordableProperties.getContent().get(0).getPricePerNight()
                .compareTo(new BigDecimal("150")) <= 0);
    }

    @Test
    void testGetPropertiesByHost_Integration() {
        // Given
        Property property1 = propertyRepository.save(testProperty);

        // Create another host
        User anotherHost = new User();
        anotherHost.setFirstName("John");
        anotherHost.setLastName("Host");
        anotherHost.setEmail("john.host@example.com");
        anotherHost.setPassword("password123");
        anotherHost.setRole(UserRole.HOST);
        anotherHost.setCreatedAt(LocalDateTime.now());
        User savedAnotherHost = userRepository.save(anotherHost);

        Property property2 = new Property();
        property2.setTitle("Another Host Property");
        property2.setDescription("Property by another host");
        property2.setPropertyType(PropertyType.HOUSE);
        property2.setPricePerNight(new BigDecimal("300.00"));
        property2.setMaxGuests(10);
        property2.setBedrooms(5);
        property2.setBathrooms(4);
        property2.setStatus(PropertyStatus.ACTIVE);
        property2.setHost(savedAnotherHost);
        property2.setCreatedAt(LocalDateTime.now());
        Property savedProperty2 = propertyRepository.save(property2);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Property> hostProperties = propertyService.getPropertiesByHost(testHost.getId(), pageable);

        // Then
        assertEquals(1, hostProperties.getTotalElements());
        assertEquals(testHost.getId(), hostProperties.getContent().get(0).getHost().getId());
        assertEquals("Beautiful Apartment", hostProperties.getContent().get(0).getTitle());
    }

    @Test
    void testUpdatePropertyStatus_Integration() {
        // Given
        Property savedProperty = propertyRepository.save(testProperty);
        assertEquals(PropertyStatus.ACTIVE, savedProperty.getStatus());

        // When
        Property updatedProperty = propertyService.updatePropertyStatus(savedProperty.getId(), PropertyStatus.INACTIVE);

        // Then
        assertEquals(PropertyStatus.INACTIVE, updatedProperty.getStatus());

        // Verify it's updated in database
        Optional<Property> foundProperty = propertyRepository.findById(savedProperty.getId());
        assertTrue(foundProperty.isPresent());
        assertEquals(PropertyStatus.INACTIVE, foundProperty.get().getStatus());
    }

    @Test
    void testGetFeaturedProperties_Integration() {
        // Given
        Property property1 = propertyRepository.save(testProperty);

        Property property2 = new Property();
        property2.setTitle("Featured Property");
        property2.setDescription("A featured property");
        property2.setPropertyType(PropertyType.HOUSE);
        property2.setPricePerNight(new BigDecimal("250.00"));
        property2.setMaxGuests(6);
        property2.setBedrooms(3);
        property2.setBathrooms(2);
        property2.setStatus(PropertyStatus.ACTIVE);
        property2.setHost(testHost);
        property2.setCreatedAt(LocalDateTime.now());
        Property savedProperty2 = propertyRepository.save(property2);

        // When
        List<Property> featuredProperties = propertyService.getFeaturedProperties();

        // Then
        assertNotNull(featuredProperties);
        // Note: The actual implementation of getFeaturedProperties would determine the expected size
        // This test assumes it returns all active properties or a subset based on some criteria
    }
}




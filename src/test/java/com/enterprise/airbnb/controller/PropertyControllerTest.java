package com.enterprise.airbnb.controller;

import com.enterprise.airbnb.model.Property;
import com.enterprise.airbnb.model.PropertyType;
import com.enterprise.airbnb.model.PropertyStatus;
import com.enterprise.airbnb.model.User;
import com.enterprise.airbnb.model.UserRole;
import com.enterprise.airbnb.service.PropertyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PropertyController.class)
class PropertyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PropertyService propertyService;

    @Autowired
    private ObjectMapper objectMapper;

    private Property testProperty;
    private User testHost;

    @BeforeEach
    void setUp() {
        // Setup test host
        testHost = new User();
        testHost.setId(1L);
        testHost.setFirstName("Jane");
        testHost.setLastName("Host");
        testHost.setEmail("jane.host@example.com");
        testHost.setRole(UserRole.HOST);

        // Setup test property
        testProperty = new Property();
        testProperty.setId(1L);
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
    @WithMockUser(roles = "ADMIN")
    void testGetAllProperties_Success() throws Exception {
        // Given
        List<Property> properties = Arrays.asList(testProperty);
        Page<Property> propertyPage = new PageImpl<>(properties);
        Pageable pageable = PageRequest.of(0, 10);

        when(propertyService.getAllProperties(pageable)).thenReturn(propertyPage);

        // When & Then
        mockMvc.perform(get("/api/properties")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Beautiful Apartment"))
                .andExpect(jsonPath("$.content[0].pricePerNight").value(100.00))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @WithMockUser(roles = "GUEST")
    void testGetPropertyById_Success() throws Exception {
        // Given
        when(propertyService.getPropertyById(1L)).thenReturn(Optional.of(testProperty));

        // When & Then
        mockMvc.perform(get("/api/properties/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Beautiful Apartment"))
                .andExpect(jsonPath("$.description").value("A lovely apartment in the city center"))
                .andExpect(jsonPath("$.pricePerNight").value(100.00))
                .andExpect(jsonPath("$.maxGuests").value(4))
                .andExpect(jsonPath("$.bedrooms").value(2))
                .andExpect(jsonPath("$.bathrooms").value(1));
    }

    @Test
    @WithMockUser(roles = "GUEST")
    void testGetPropertyById_NotFound() throws Exception {
        // Given
        when(propertyService.getPropertyById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/properties/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "HOST")
    void testCreateProperty_Success() throws Exception {
        // Given
        Property newProperty = new Property();
        newProperty.setTitle("New Property");
        newProperty.setDescription("A new property");
        newProperty.setPropertyType(PropertyType.HOUSE);
        newProperty.setPricePerNight(new BigDecimal("150.00"));
        newProperty.setMaxGuests(6);
        newProperty.setBedrooms(3);
        newProperty.setBathrooms(2);

        when(propertyService.createProperty(any(Property.class))).thenReturn(testProperty);

        // When & Then
        mockMvc.perform(post("/api/properties")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProperty)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Beautiful Apartment"));
    }

    @Test
    @WithMockUser(roles = "HOST")
    void testCreateProperty_ValidationError() throws Exception {
        // Given
        Property invalidProperty = new Property();
        // Missing required fields

        // When & Then
        mockMvc.perform(post("/api/properties")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProperty)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "HOST")
    void testUpdateProperty_Success() throws Exception {
        // Given
        Property updatedProperty = new Property();
        updatedProperty.setId(1L);
        updatedProperty.setTitle("Updated Property");
        updatedProperty.setDescription("Updated description");
        updatedProperty.setPricePerNight(new BigDecimal("120.00"));

        when(propertyService.updateProperty(eq(1L), any(Property.class))).thenReturn(updatedProperty);

        // When & Then
        mockMvc.perform(put("/api/properties/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedProperty)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Updated Property"))
                .andExpect(jsonPath("$.pricePerNight").value(120.00));
    }

    @Test
    @WithMockUser(roles = "HOST")
    void testUpdateProperty_NotFound() throws Exception {
        // Given
        Property updatedProperty = new Property();
        updatedProperty.setId(999L);

        when(propertyService.updateProperty(eq(999L), any(Property.class)))
                .thenThrow(new RuntimeException("Property not found"));

        // When & Then
        mockMvc.perform(put("/api/properties/999")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedProperty)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "HOST")
    void testDeleteProperty_Success() throws Exception {
        // Given
        when(propertyService.deleteProperty(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/properties/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "HOST")
    void testDeleteProperty_NotFound() throws Exception {
        // Given
        when(propertyService.deleteProperty(999L)).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/api/properties/999")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "GUEST")
    void testSearchProperties_Success() throws Exception {
        // Given
        List<Property> properties = Arrays.asList(testProperty);
        Page<Property> propertyPage = new PageImpl<>(properties);
        Pageable pageable = PageRequest.of(0, 10);

        when(propertyService.searchProperties(anyString(), anyString(), any(), any(), any(), any(), any(), any(), any(), pageable))
                .thenReturn(propertyPage);

        // When & Then
        mockMvc.perform(get("/api/properties/search")
                .param("city", "New York")
                .param("propertyType", "APARTMENT")
                .param("minPrice", "50")
                .param("maxPrice", "200")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @WithMockUser(roles = "HOST")
    void testGetPropertiesByHost_Success() throws Exception {
        // Given
        List<Property> properties = Arrays.asList(testProperty);
        Page<Property> propertyPage = new PageImpl<>(properties);
        Pageable pageable = PageRequest.of(0, 10);

        when(propertyService.getPropertiesByHost(1L, pageable)).thenReturn(propertyPage);

        // When & Then
        mockMvc.perform(get("/api/properties/host/1")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @WithMockUser(roles = "GUEST")
    void testGetFeaturedProperties_Success() throws Exception {
        // Given
        List<Property> properties = Arrays.asList(testProperty);
        when(propertyService.getFeaturedProperties()).thenReturn(properties);

        // When & Then
        mockMvc.perform(get("/api/properties/featured")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Beautiful Apartment"));
    }

    @Test
    @WithMockUser(roles = "HOST")
    void testUpdatePropertyStatus_Success() throws Exception {
        // Given
        Property updatedProperty = new Property();
        updatedProperty.setId(1L);
        updatedProperty.setStatus(PropertyStatus.INACTIVE);

        when(propertyService.updatePropertyStatus(1L, PropertyStatus.INACTIVE)).thenReturn(updatedProperty);

        // When & Then
        mockMvc.perform(patch("/api/properties/1/status")
                .with(csrf())
                .param("status", "INACTIVE")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("INACTIVE"));
    }
}




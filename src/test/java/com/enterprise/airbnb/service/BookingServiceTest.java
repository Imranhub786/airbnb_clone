package com.enterprise.airbnb.service;

import com.enterprise.airbnb.event.BookingCreatedEvent;
import com.enterprise.airbnb.model.*;
import com.enterprise.airbnb.repository.BookingRepository;
import com.enterprise.airbnb.repository.PropertyRepository;
import com.enterprise.airbnb.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private BookingService bookingService;

    private Booking testBooking;
    private Property testProperty;
    private User testGuest;
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

        // Setup test guest
        testGuest = new User();
        testGuest.setId(2L);
        testGuest.setFirstName("John");
        testGuest.setLastName("Guest");
        testGuest.setEmail("john.guest@example.com");
        testGuest.setRole(UserRole.GUEST);

        // Setup test property
        testProperty = new Property();
        testProperty.setId(1L);
        testProperty.setTitle("Beautiful Apartment");
        testProperty.setDescription("A lovely apartment in the city center");
        testProperty.setPricePerNight(new BigDecimal("100.00"));
        testProperty.setHost(testHost);

        // Setup test booking
        testBooking = new Booking();
        testBooking.setId(1L);
        testBooking.setBookingReference("BK001");
        testBooking.setProperty(testProperty);
        testBooking.setGuest(testGuest);
        testBooking.setCheckInDate(LocalDate.now().plusDays(1));
        testBooking.setCheckOutDate(LocalDate.now().plusDays(3));
        testBooking.setNumberOfGuests(2);
        testBooking.setNumberOfNights(2);
        testBooking.setTotalAmount(new BigDecimal("200.00"));
        testBooking.setStatus(BookingStatus.PENDING);
        testBooking.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testGetAllBookings_Success() {
        // Given
        List<Booking> bookings = Arrays.asList(testBooking);
        Page<Booking> bookingPage = new PageImpl<>(bookings);
        Pageable pageable = PageRequest.of(0, 10);

        when(bookingRepository.findAll(pageable)).thenReturn(bookingPage);

        // When
        Page<Booking> result = bookingService.getAllBookings(pageable);

        // Then
        assertEquals(1, result.getContent().size());
        assertEquals(testBooking, result.getContent().get(0));
    }

    @Test
    void testGetBookingById_Success() {
        // Given
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));

        // When
        Optional<Booking> result = bookingService.getBookingById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testBooking, result.get());
    }

    @Test
    void testGetBookingById_NotFound() {
        // Given
        when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Booking> result = bookingService.getBookingById(999L);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testCreateBooking_Success() {
        // Given
        Booking newBooking = new Booking();
        newBooking.setProperty(testProperty);
        newBooking.setGuest(testGuest);
        newBooking.setCheckInDate(LocalDate.now().plusDays(1));
        newBooking.setCheckOutDate(LocalDate.now().plusDays(3));
        newBooking.setNumberOfGuests(2);

        when(propertyRepository.findById(1L)).thenReturn(Optional.of(testProperty));
        when(userRepository.findById(2L)).thenReturn(Optional.of(testGuest));
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        // When
        Booking result = bookingService.createBooking(newBooking);

        // Then
        assertNotNull(result);
        assertEquals(BookingStatus.PENDING, result.getStatus());
        verify(bookingRepository).save(any(Booking.class));
        
        // Verify event was published
        ArgumentCaptor<BookingCreatedEvent> eventCaptor = ArgumentCaptor.forClass(BookingCreatedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertEquals(testBooking, eventCaptor.getValue().getBooking());
    }

    @Test
    void testCreateBooking_PropertyNotAvailable() {
        // Given
        Booking newBooking = new Booking();
        newBooking.setProperty(testProperty);
        newBooking.setGuest(testGuest);
        newBooking.setCheckInDate(LocalDate.now().plusDays(1));
        newBooking.setCheckOutDate(LocalDate.now().plusDays(3));
        newBooking.setNumberOfGuests(2);

        when(propertyRepository.findById(1L)).thenReturn(Optional.of(testProperty));
        when(userRepository.findById(2L)).thenReturn(Optional.of(testGuest));
        when(bookingRepository.findOverlappingBookings(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Arrays.asList(testBooking));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            bookingService.createBooking(newBooking);
        });
    }

    @Test
    void testUpdateBooking_Success() {
        // Given
        Booking updatedBooking = new Booking();
        updatedBooking.setId(1L);
        updatedBooking.setStatus(BookingStatus.CONFIRMED);
        updatedBooking.setProperty(testProperty);
        updatedBooking.setGuest(testGuest);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(updatedBooking);

        // When
        Booking result = bookingService.updateBooking(1L, updatedBooking);

        // Then
        assertNotNull(result);
        assertEquals(BookingStatus.CONFIRMED, result.getStatus());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void testUpdateBooking_NotFound() {
        // Given
        Booking updatedBooking = new Booking();
        updatedBooking.setId(999L);

        when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            bookingService.updateBooking(999L, updatedBooking);
        });
    }

    @Test
    void testDeleteBooking_Success() {
        // Given
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));

        // When
        bookingService.deleteBooking(1L);

        // Then
        verify(bookingRepository).delete(testBooking);
    }

    @Test
    void testDeleteBooking_NotFound() {
        // Given
        when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            bookingService.deleteBooking(999L);
        });
    }

    @Test
    void testGetBookingsByUser_Success() {
        // Given
        List<Booking> bookings = Arrays.asList(testBooking);
        Page<Booking> bookingPage = new PageImpl<>(bookings);
        Pageable pageable = PageRequest.of(0, 10);

        when(bookingRepository.findByGuestId(2L, pageable)).thenReturn(bookingPage);

        // When
        Page<Booking> result = bookingService.getBookingsByUser(2L, pageable);

        // Then
        assertEquals(1, result.getContent().size());
        assertEquals(testBooking, result.getContent().get(0));
    }

    @Test
    void testGetBookingsByProperty_Success() {
        // Given
        List<Booking> bookings = Arrays.asList(testBooking);
        Page<Booking> bookingPage = new PageImpl<>(bookings);
        Pageable pageable = PageRequest.of(0, 10);

        when(bookingRepository.findByPropertyId(1L, pageable)).thenReturn(bookingPage);

        // When
        Page<Booking> result = bookingService.getBookingsByProperty(1L, pageable);

        // Then
        assertEquals(1, result.getContent().size());
        assertEquals(testBooking, result.getContent().get(0));
    }

    @Test
    void testGetBookingsByHost_Success() {
        // Given
        List<Booking> bookings = Arrays.asList(testBooking);
        Page<Booking> bookingPage = new PageImpl<>(bookings);
        Pageable pageable = PageRequest.of(0, 10);

        when(bookingRepository.findByPropertyHostId(1L, pageable)).thenReturn(bookingPage);

        // When
        Page<Booking> result = bookingService.getBookingsByHost(1L, pageable);

        // Then
        assertEquals(1, result.getContent().size());
        assertEquals(testBooking, result.getContent().get(0));
    }

    @Test
    void testGetUpcomingBookings_Success() {
        // Given
        List<Booking> bookings = Arrays.asList(testBooking);
        when(bookingRepository.findUpcomingBookingsByUserId(2L)).thenReturn(bookings);

        // When
        List<Booking> result = bookingService.getUpcomingBookings(2L);

        // Then
        assertEquals(1, result.size());
        assertEquals(testBooking, result.get(0));
    }

    @Test
    void testGetCurrentBookings_Success() {
        // Given
        List<Booking> bookings = Arrays.asList(testBooking);
        when(bookingRepository.findCurrentBookingsByUserId(2L)).thenReturn(bookings);

        // When
        List<Booking> result = bookingService.getCurrentBookings(2L);

        // Then
        assertEquals(1, result.size());
        assertEquals(testBooking, result.get(0));
    }

    @Test
    void testGetPastBookings_Success() {
        // Given
        List<Booking> bookings = Arrays.asList(testBooking);
        when(bookingRepository.findPastBookingsByUserId(2L)).thenReturn(bookings);

        // When
        List<Booking> result = bookingService.getPastBookings(2L);

        // Then
        assertEquals(1, result.size());
        assertEquals(testBooking, result.get(0));
    }

    @Test
    void testIsPropertyAvailable_Available() {
        // Given
        when(bookingRepository.findOverlappingBookings(1L, 
                LocalDate.now().plusDays(1), 
                LocalDate.now().plusDays(3)))
                .thenReturn(Arrays.asList());

        // When
        boolean result = bookingService.isPropertyAvailable(1L, 
                LocalDate.now().plusDays(1), 
                LocalDate.now().plusDays(3));

        // Then
        assertTrue(result);
    }

    @Test
    void testIsPropertyAvailable_NotAvailable() {
        // Given
        when(bookingRepository.findOverlappingBookings(1L, 
                LocalDate.now().plusDays(1), 
                LocalDate.now().plusDays(3)))
                .thenReturn(Arrays.asList(testBooking));

        // When
        boolean result = bookingService.isPropertyAvailable(1L, 
                LocalDate.now().plusDays(1), 
                LocalDate.now().plusDays(3));

        // Then
        assertFalse(result);
    }
}




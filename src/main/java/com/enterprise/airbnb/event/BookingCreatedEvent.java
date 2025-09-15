package com.enterprise.airbnb.event;

import com.enterprise.airbnb.model.Booking;
import org.springframework.context.ApplicationEvent;

/**
 * Event published when a new booking is created
 */
public class BookingCreatedEvent extends ApplicationEvent {
    
    private final Booking booking;
    private final String eventType;
    private final Long timestamp;
    
    public BookingCreatedEvent(Object source, Booking booking) {
        super(source);
        this.booking = booking;
        this.eventType = "BOOKING_CREATED";
        this.timestamp = System.currentTimeMillis();
    }
    
    public Booking getBooking() {
        return booking;
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public Long getEventTimestamp() {
        return timestamp;
    }
    
    @Override
    public String toString() {
        return "BookingCreatedEvent{" +
                "bookingId=" + booking.getId() +
                ", bookingReference=" + booking.getBookingReference() +
                ", propertyId=" + booking.getProperty().getId() +
                ", guestId=" + booking.getGuest().getId() +
                ", eventType='" + eventType + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}

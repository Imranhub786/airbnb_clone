package com.enterprise.airbnb.graphql.dto;

/**
 * GraphQL Input DTO for Booking
 */
public class BookingInput {
    
    private Long propertyId;
    private Long guestId;
    private java.time.LocalDate checkInDate;
    private java.time.LocalDate checkOutDate;
    private Integer numberOfGuests;
    private String specialRequests;
    private String guestMessage;

    // Constructors
    public BookingInput() {}

    // Getters and Setters
    public Long getPropertyId() { return propertyId; }
    public void setPropertyId(Long propertyId) { this.propertyId = propertyId; }

    public Long getGuestId() { return guestId; }
    public void setGuestId(Long guestId) { this.guestId = guestId; }

    public java.time.LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(java.time.LocalDate checkInDate) { this.checkInDate = checkInDate; }

    public java.time.LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(java.time.LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }

    public Integer getNumberOfGuests() { return numberOfGuests; }
    public void setNumberOfGuests(Integer numberOfGuests) { this.numberOfGuests = numberOfGuests; }

    public String getSpecialRequests() { return specialRequests; }
    public void setSpecialRequests(String specialRequests) { this.specialRequests = specialRequests; }

    public String getGuestMessage() { return guestMessage; }
    public void setGuestMessage(String guestMessage) { this.guestMessage = guestMessage; }
}




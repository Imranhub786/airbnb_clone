package com.enterprise.airbnb.graphql.dto;

import com.enterprise.airbnb.model.Booking;

/**
 * GraphQL Response DTO for Booking
 */
public class BookingResponse {
    
    private Booking booking;
    private String paymentUrl;
    private String message;

    // Constructors
    public BookingResponse() {}

    // Getters and Setters
    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }

    public String getPaymentUrl() { return paymentUrl; }
    public void setPaymentUrl(String paymentUrl) { this.paymentUrl = paymentUrl; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}




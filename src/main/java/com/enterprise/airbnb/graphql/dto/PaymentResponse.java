package com.enterprise.airbnb.graphql.dto;

import com.enterprise.airbnb.model.Payment;

/**
 * GraphQL Response DTO for Payment
 */
public class PaymentResponse {
    
    private Payment payment;
    private String paymentUrl;
    private String message;

    // Constructors
    public PaymentResponse() {}

    // Getters and Setters
    public Payment getPayment() { return payment; }
    public void setPayment(Payment payment) { this.payment = payment; }

    public String getPaymentUrl() { return paymentUrl; }
    public void setPaymentUrl(String paymentUrl) { this.paymentUrl = paymentUrl; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}




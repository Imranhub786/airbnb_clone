package com.enterprise.airbnb.graphql.dto;

import com.enterprise.airbnb.model.PaymentType;
import com.enterprise.airbnb.model.PaymentMethod;

import java.math.BigDecimal;

/**
 * GraphQL Input DTO for Payment
 */
public class PaymentInput {
    
    private Long bookingId;
    private Long userId;
    private BigDecimal amount;
    private String currency;
    private PaymentType paymentType;
    private PaymentMethod paymentMethod;
    private String description;
    private String returnUrl;
    private String cancelUrl;

    // Constructors
    public PaymentInput() {}

    // Getters and Setters
    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public PaymentType getPaymentType() { return paymentType; }
    public void setPaymentType(PaymentType paymentType) { this.paymentType = paymentType; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getReturnUrl() { return returnUrl; }
    public void setReturnUrl(String returnUrl) { this.returnUrl = returnUrl; }

    public String getCancelUrl() { return cancelUrl; }
    public void setCancelUrl(String cancelUrl) { this.cancelUrl = cancelUrl; }
}




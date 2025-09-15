package com.enterprise.airbnb.graphql.resolver;

import com.enterprise.airbnb.model.Payment;
import com.enterprise.airbnb.model.Booking;
import com.enterprise.airbnb.model.User;
import com.enterprise.airbnb.service.BookingService;
import com.enterprise.airbnb.service.UserService;
import graphql.kickstart.tools.GraphQLResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * GraphQL Resolver for Payment entity
 */
@Component
public class PaymentResolver implements GraphQLResolver<Payment> {

    private static final Logger logger = LoggerFactory.getLogger(PaymentResolver.class);

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    /**
     * Resolve booking for payment
     */
    public Booking booking(Payment payment) {
        logger.debug("Resolving booking for payment ID: {}", payment.getId());
        return payment.getBooking();
    }

    /**
     * Resolve user for payment
     */
    public User user(Payment payment) {
        logger.debug("Resolving user for payment ID: {}", payment.getId());
        return payment.getUser();
    }

    /**
     * Check if payment is partial refund
     */
    public Boolean isPartialRefund(Payment payment) {
        logger.debug("Checking if payment is partial refund for payment ID: {}", payment.getId());
        return payment.getRefundAmount() != null && 
               payment.getAmount() != null && 
               payment.getRefundAmount().compareTo(payment.getAmount()) < 0;
    }
}

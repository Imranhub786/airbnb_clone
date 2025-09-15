package com.enterprise.airbnb.service;

import com.enterprise.airbnb.model.Payment;
import com.enterprise.airbnb.model.PaymentStatus;
import com.paypal.core.PayPalHttpClient;
import com.paypal.orders.*;
import com.paypal.payments.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for PayPal payment processing
 */
@Service
public class PayPalService {

    private static final Logger logger = LoggerFactory.getLogger(PayPalService.class);

    @Autowired
    private PayPalHttpClient payPalHttpClient;

    @Value("${paypal.return.url}")
    private String returnUrl;

    @Value("${paypal.cancel.url}")
    private String cancelUrl;

    /**
     * Create PayPal order
     */
    public String createOrder(Payment payment) {
        logger.info("Creating PayPal order for payment ID: {}", payment.getId());

        try {
            OrdersCreateRequest request = new OrdersCreateRequest();
            request.prefer("return=representation");
            request.requestBody(buildOrderRequest(payment));

            Order order = payPalHttpClient.execute(request).result();
            logger.info("PayPal order created: {}", order.id());

            return order.id();

        } catch (IOException e) {
            logger.error("Error creating PayPal order: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create PayPal order", e);
        }
    }

    /**
     * Capture PayPal order
     */
    public boolean captureOrder(String orderId) {
        logger.info("Capturing PayPal order: {}", orderId);

        try {
            OrdersCaptureRequest request = new OrdersCaptureRequest(orderId);
            request.requestBody(new OrderRequest());

            Order order = payPalHttpClient.execute(request).result();
            logger.info("PayPal order captured: {}", order.id());

            return "COMPLETED".equals(order.status());

        } catch (IOException e) {
            logger.error("Error capturing PayPal order: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Refund PayPal payment
     */
    public boolean refundPayment(String captureId, BigDecimal refundAmount) {
        logger.info("Refunding PayPal payment: {} with amount: {}", captureId, refundAmount);

        try {
            RefundsRefundRequest request = new RefundsRefundRequest(captureId);
            request.requestBody(buildRefundRequest(refundAmount));

            Refund refund = payPalHttpClient.execute(request).result();
            logger.info("PayPal refund processed: {}", refund.id());

            return "COMPLETED".equals(refund.status());

        } catch (IOException e) {
            logger.error("Error refunding PayPal payment: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Get order details
     */
    public Order getOrderDetails(String orderId) {
        logger.info("Getting PayPal order details: {}", orderId);

        try {
            OrdersGetRequest request = new OrdersGetRequest(orderId);
            return payPalHttpClient.execute(request).result();

        } catch (IOException e) {
            logger.error("Error getting PayPal order details: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get order details", e);
        }
    }

    /**
     * Build order request
     */
    private OrderRequest buildOrderRequest(Payment payment) {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");

        ApplicationContext applicationContext = new ApplicationContext()
                .returnUrl(returnUrl)
                .cancelUrl(cancelUrl);
        orderRequest.applicationContext(applicationContext);

        List<PurchaseUnitRequest> purchaseUnits = new ArrayList<>();
        PurchaseUnitRequest purchaseUnitRequest = new PurchaseUnitRequest()
                .referenceId(payment.getId().toString())
                .description("Airbnb Booking Payment")
                .amountWithBreakdown(buildAmountWithBreakdown(payment))
                .items(buildItems(payment));
        purchaseUnits.add(purchaseUnitRequest);
        orderRequest.purchaseUnits(purchaseUnits);

        return orderRequest;
    }

    /**
     * Build amount with breakdown
     */
    private AmountWithBreakdown buildAmountWithBreakdown(Payment payment) {
        AmountWithBreakdown amount = new AmountWithBreakdown()
                .currencyCode(payment.getCurrency())
                .value(payment.getAmount().toString());

        AmountBreakdown breakdown = new AmountBreakdown()
                .itemTotal(new Money()
                        .currencyCode(payment.getCurrency())
                        .value(payment.getAmount().toString()));
        amount.amountBreakdown(breakdown);

        return amount;
    }

    /**
     * Build items
     */
    private List<Item> buildItems(Payment payment) {
        List<Item> items = new ArrayList<>();
        Item item = new Item()
                .name("Airbnb Booking")
                .description("Property booking payment")
                .unitAmount(new Money()
                        .currencyCode(payment.getCurrency())
                        .value(payment.getAmount().toString()))
                .quantity("1");
        items.add(item);
        return items;
    }

    /**
     * Build refund request
     */
    private RefundRequest buildRefundRequest(BigDecimal refundAmount) {
        RefundRequest refundRequest = new RefundRequest();
        Money money = new Money()
                .currencyCode("USD") // You might want to make this configurable
                .value(refundAmount.toString());
        refundRequest.amount(money);
        return refundRequest;
    }
}




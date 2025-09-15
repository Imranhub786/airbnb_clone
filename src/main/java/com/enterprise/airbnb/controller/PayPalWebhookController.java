package com.enterprise.airbnb.controller;

import com.enterprise.airbnb.model.Payment;
import com.enterprise.airbnb.model.PaymentStatus;
import com.enterprise.airbnb.service.PaymentService;
import com.enterprise.airbnb.service.PayPalWebhookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Controller for handling PayPal webhook events
 */
@RestController
@RequestMapping("/api/webhooks/paypal")
@CrossOrigin(origins = "*")
public class PayPalWebhookController {

    private static final Logger logger = LoggerFactory.getLogger(PayPalWebhookController.class);

    @Autowired
    private PayPalWebhookService payPalWebhookService;

    @Autowired
    private PaymentService paymentService;

    /**
     * Handle PayPal webhook events
     */
    @PostMapping
    public ResponseEntity<String> handleWebhook(
            @RequestBody Map<String, Object> payload,
            @RequestHeader Map<String, String> headers,
            HttpServletRequest request) {
        
        logger.info("Received PayPal webhook event");
        
        try {
            // Verify webhook signature
            if (!payPalWebhookService.verifyWebhookSignature(payload, headers, request)) {
                logger.warn("Invalid PayPal webhook signature");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid signature");
            }

            // Process webhook event
            String eventType = (String) payload.get("event_type");
            logger.info("Processing PayPal webhook event: {}", eventType);

            switch (eventType) {
                case "PAYMENT.CAPTURE.COMPLETED":
                    handlePaymentCaptureCompleted(payload);
                    break;
                case "PAYMENT.CAPTURE.DENIED":
                    handlePaymentCaptureDenied(payload);
                    break;
                case "PAYMENT.CAPTURE.REFUNDED":
                    handlePaymentRefunded(payload);
                    break;
                case "PAYMENT.CAPTURE.REVERSED":
                    handlePaymentReversed(payload);
                    break;
                case "CHECKOUT.ORDER.APPROVED":
                    handleOrderApproved(payload);
                    break;
                case "CHECKOUT.ORDER.COMPLETED":
                    handleOrderCompleted(payload);
                    break;
                case "CHECKOUT.ORDER.CANCELLED":
                    handleOrderCancelled(payload);
                    break;
                default:
                    logger.info("Unhandled PayPal webhook event type: {}", eventType);
                    break;
            }

            return ResponseEntity.ok("Webhook processed successfully");

        } catch (Exception e) {
            logger.error("Error processing PayPal webhook: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing webhook");
        }
    }

    /**
     * Handle payment capture completed event
     */
    private void handlePaymentCaptureCompleted(Map<String, Object> payload) {
        logger.info("Processing PAYMENT.CAPTURE.COMPLETED event");
        
        try {
            Map<String, Object> resource = (Map<String, Object>) payload.get("resource");
            String captureId = (String) resource.get("id");
            String orderId = (String) resource.get("custom_id");
            
            // Find payment by PayPal capture ID or order ID
            Payment payment = payPalWebhookService.findPaymentByPayPalId(captureId, orderId);
            
            if (payment != null) {
                // Update payment status
                payment.setStatus(PaymentStatus.COMPLETED);
                payment.setPaypalCaptureId(captureId);
                payment.setProcessedAt(java.time.LocalDateTime.now());
                
                // Extract amount and currency
                Map<String, Object> amount = (Map<String, Object>) resource.get("amount");
                if (amount != null) {
                    String currency = (String) amount.get("currency_code");
                    String value = (String) amount.get("value");
                    payment.setOriginalCurrency(currency);
                    payment.setOriginalAmount(new java.math.BigDecimal(value));
                }
                
                paymentService.updatePayment(payment);
                
                logger.info("Payment {} marked as completed", payment.getId());
            } else {
                logger.warn("Payment not found for PayPal capture ID: {}", captureId);
            }
            
        } catch (Exception e) {
            logger.error("Error processing payment capture completed: {}", e.getMessage(), e);
        }
    }

    /**
     * Handle payment capture denied event
     */
    private void handlePaymentCaptureDenied(Map<String, Object> payload) {
        logger.info("Processing PAYMENT.CAPTURE.DENIED event");
        
        try {
            Map<String, Object> resource = (Map<String, Object>) payload.get("resource");
            String captureId = (String) resource.get("id");
            String orderId = (String) resource.get("custom_id");
            
            Payment payment = payPalWebhookService.findPaymentByPayPalId(captureId, orderId);
            
            if (payment != null) {
                payment.setStatus(PaymentStatus.FAILED);
                payment.setFailedAt(java.time.LocalDateTime.now());
                
                // Extract failure reason
                Map<String, Object> details = (Map<String, Object>) resource.get("details");
                if (details != null) {
                    String reason = (String) details.get("description");
                    payment.setFailureReason(reason);
                }
                
                paymentService.updatePayment(payment);
                
                logger.info("Payment {} marked as failed", payment.getId());
            }
            
        } catch (Exception e) {
            logger.error("Error processing payment capture denied: {}", e.getMessage(), e);
        }
    }

    /**
     * Handle payment refunded event
     */
    private void handlePaymentRefunded(Map<String, Object> payload) {
        logger.info("Processing PAYMENT.CAPTURE.REFUNDED event");
        
        try {
            Map<String, Object> resource = (Map<String, Object>) payload.get("resource");
            String captureId = (String) resource.get("id");
            String orderId = (String) resource.get("custom_id");
            
            Payment payment = payPalWebhookService.findPaymentByPayPalId(captureId, orderId);
            
            if (payment != null) {
                // Extract refund information
                Map<String, Object> amount = (Map<String, Object>) resource.get("amount");
                if (amount != null) {
                    String value = (String) amount.get("value");
                    java.math.BigDecimal refundAmount = new java.math.BigDecimal(value);
                    
                    payment.setRefundAmount(refundAmount);
                    payment.setRefundedAt(java.time.LocalDateTime.now());
                    payment.setStatus(PaymentStatus.REFUNDED);
                    payment.setIsPartialRefund(refundAmount.compareTo(payment.getAmount()) < 0);
                }
                
                paymentService.updatePayment(payment);
                
                logger.info("Payment {} marked as refunded", payment.getId());
            }
            
        } catch (Exception e) {
            logger.error("Error processing payment refunded: {}", e.getMessage(), e);
        }
    }

    /**
     * Handle payment reversed event
     */
    private void handlePaymentReversed(Map<String, Object> payload) {
        logger.info("Processing PAYMENT.CAPTURE.REVERSED event");
        
        try {
            Map<String, Object> resource = (Map<String, Object>) payload.get("resource");
            String captureId = (String) resource.get("id");
            String orderId = (String) resource.get("custom_id");
            
            Payment payment = payPalWebhookService.findPaymentByPayPalId(captureId, orderId);
            
            if (payment != null) {
                payment.setStatus(PaymentStatus.FAILED);
                payment.setFailedAt(java.time.LocalDateTime.now());
                payment.setFailureReason("Payment reversed by PayPal");
                
                paymentService.updatePayment(payment);
                
                logger.info("Payment {} marked as reversed", payment.getId());
            }
            
        } catch (Exception e) {
            logger.error("Error processing payment reversed: {}", e.getMessage(), e);
        }
    }

    /**
     * Handle order approved event
     */
    private void handleOrderApproved(Map<String, Object> payload) {
        logger.info("Processing CHECKOUT.ORDER.APPROVED event");
        
        try {
            Map<String, Object> resource = (Map<String, Object>) payload.get("resource");
            String orderId = (String) resource.get("id");
            
            Payment payment = payPalWebhookService.findPaymentByPayPalOrderId(orderId);
            
            if (payment != null) {
                payment.setPaypalOrderId(orderId);
                payment.setStatus(PaymentStatus.PENDING);
                
                paymentService.updatePayment(payment);
                
                logger.info("Payment {} order approved", payment.getId());
            }
            
        } catch (Exception e) {
            logger.error("Error processing order approved: {}", e.getMessage(), e);
        }
    }

    /**
     * Handle order completed event
     */
    private void handleOrderCompleted(Map<String, Object> payload) {
        logger.info("Processing CHECKOUT.ORDER.COMPLETED event");
        
        try {
            Map<String, Object> resource = (Map<String, Object>) payload.get("resource");
            String orderId = (String) resource.get("id");
            
            Payment payment = payPalWebhookService.findPaymentByPayPalOrderId(orderId);
            
            if (payment != null) {
                payment.setStatus(PaymentStatus.COMPLETED);
                payment.setProcessedAt(java.time.LocalDateTime.now());
                
                paymentService.updatePayment(payment);
                
                logger.info("Payment {} order completed", payment.getId());
            }
            
        } catch (Exception e) {
            logger.error("Error processing order completed: {}", e.getMessage(), e);
        }
    }

    /**
     * Handle order cancelled event
     */
    private void handleOrderCancelled(Map<String, Object> payload) {
        logger.info("Processing CHECKOUT.ORDER.CANCELLED event");
        
        try {
            Map<String, Object> resource = (Map<String, Object>) payload.get("resource");
            String orderId = (String) resource.get("id");
            
            Payment payment = payPalWebhookService.findPaymentByPayPalOrderId(orderId);
            
            if (payment != null) {
                payment.setStatus(PaymentStatus.FAILED);
                payment.setFailedAt(java.time.LocalDateTime.now());
                payment.setFailureReason("Order cancelled by user");
                
                paymentService.updatePayment(payment);
                
                logger.info("Payment {} order cancelled", payment.getId());
            }
            
        } catch (Exception e) {
            logger.error("Error processing order cancelled: {}", e.getMessage(), e);
        }
    }

    /**
     * Health check endpoint for webhook
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("PayPal webhook endpoint is healthy");
    }
}




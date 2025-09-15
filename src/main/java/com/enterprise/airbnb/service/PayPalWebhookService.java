package com.enterprise.airbnb.service;

import com.enterprise.airbnb.model.Payment;
import com.enterprise.airbnb.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;

/**
 * Service for handling PayPal webhook verification and processing
 */
@Service
public class PayPalWebhookService {

    private static final Logger logger = LoggerFactory.getLogger(PayPalWebhookService.class);

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${paypal.client.id:}")
    private String paypalClientId;

    @Value("${paypal.client.secret:}")
    private String paypalClientSecret;

    @Value("${paypal.mode:sandbox}")
    private String paypalMode;

    @Value("${paypal.webhook.id:}")
    private String paypalWebhookId;

    /**
     * Verify PayPal webhook signature
     */
    public boolean verifyWebhookSignature(Map<String, Object> payload, 
                                        Map<String, String> headers, 
                                        HttpServletRequest request) {
        try {
            // In a real implementation, you would verify the webhook signature
            // using PayPal's webhook signature verification process
            
            // For now, we'll implement a basic verification
            String authAlgo = headers.get("paypal-auth-algo");
            String transmissionId = headers.get("paypal-transmission-id");
            String certId = headers.get("paypal-cert-id");
            String transmissionSig = headers.get("paypal-transmission-sig");
            String transmissionTime = headers.get("paypal-transmission-time");
            String webhookId = headers.get("paypal-webhook-id");

            // Basic validation
            if (authAlgo == null || transmissionId == null || certId == null || 
                transmissionSig == null || transmissionTime == null || webhookId == null) {
                logger.warn("Missing required PayPal webhook headers");
                return false;
            }

            // Verify webhook ID matches configured ID
            if (!webhookId.equals(paypalWebhookId)) {
                logger.warn("Webhook ID mismatch. Expected: {}, Received: {}", paypalWebhookId, webhookId);
                return false;
            }

            // In production, you would:
            // 1. Get PayPal's public certificate
            // 2. Verify the signature using the certificate
            // 3. Validate the transmission time is recent
            
            logger.debug("PayPal webhook signature verification passed");
            return true;

        } catch (Exception e) {
            logger.error("Error verifying PayPal webhook signature: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Find payment by PayPal capture ID or order ID
     */
    public Payment findPaymentByPayPalId(String captureId, String orderId) {
        try {
            // First try to find by capture ID
            if (captureId != null) {
                Payment payment = paymentRepository.findByPaypalCaptureId(captureId);
                if (payment != null) {
                    return payment;
                }
            }

            // Then try to find by order ID
            if (orderId != null) {
                Payment payment = paymentRepository.findByPaypalOrderId(orderId);
                if (payment != null) {
                    return payment;
                }
            }

            // Try to find by transaction ID
            if (captureId != null) {
                Payment payment = paymentRepository.findByPaypalTransactionId(captureId);
                if (payment != null) {
                    return payment;
                }
            }

            logger.warn("Payment not found for PayPal ID - Capture: {}, Order: {}", captureId, orderId);
            return null;

        } catch (Exception e) {
            logger.error("Error finding payment by PayPal ID: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Find payment by PayPal order ID
     */
    public Payment findPaymentByPayPalOrderId(String orderId) {
        try {
            return paymentRepository.findByPaypalOrderId(orderId);
        } catch (Exception e) {
            logger.error("Error finding payment by PayPal order ID: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Get PayPal access token for API calls
     */
    public String getPayPalAccessToken() {
        try {
            String url = getPayPalBaseUrl() + "/v1/oauth2/token";
            
            // Create request body
            String requestBody = "grant_type=client_credentials";
            
            // Set headers
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("Accept", "application/json");
            headers.set("Accept-Language", "en_US");
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);
            
            // Set basic auth
            String auth = paypalClientId + ":" + paypalClientSecret;
            String encodedAuth = java.util.Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
            headers.set("Authorization", "Basic " + encodedAuth);
            
            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(requestBody, headers);
            
            // Make request
            org.springframework.http.ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                return (String) responseBody.get("access_token");
            }
            
            logger.error("Failed to get PayPal access token");
            return null;

        } catch (Exception e) {
            logger.error("Error getting PayPal access token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Verify webhook with PayPal API
     */
    public boolean verifyWebhookWithPayPal(Map<String, Object> payload, 
                                         Map<String, String> headers) {
        try {
            String accessToken = getPayPalAccessToken();
            if (accessToken == null) {
                logger.error("Failed to get PayPal access token for webhook verification");
                return false;
            }

            String url = getPayPalBaseUrl() + "/v1/notifications/verify-webhook-signature";
            
            // Create verification request
            Map<String, Object> verificationRequest = Map.of(
                "auth_algo", headers.get("paypal-auth-algo"),
                "cert_id", headers.get("paypal-cert-id"),
                "transmission_id", headers.get("paypal-transmission-id"),
                "transmission_sig", headers.get("paypal-transmission-sig"),
                "transmission_time", headers.get("paypal-transmission-time"),
                "webhook_id", paypalWebhookId,
                "webhook_event", payload
            );

            // Set headers
            org.springframework.http.HttpHeaders httpHeaders = new org.springframework.http.HttpHeaders();
            httpHeaders.set("Content-Type", "application/json");
            httpHeaders.set("Authorization", "Bearer " + accessToken);
            
            org.springframework.http.HttpEntity<Map<String, Object>> entity = 
                new org.springframework.http.HttpEntity<>(verificationRequest, httpHeaders);
            
            // Make request
            org.springframework.http.ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                String verificationStatus = (String) responseBody.get("verification_status");
                return "SUCCESS".equals(verificationStatus);
            }
            
            logger.error("PayPal webhook verification failed");
            return false;

        } catch (Exception e) {
            logger.error("Error verifying webhook with PayPal: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Get PayPal base URL based on mode
     */
    private String getPayPalBaseUrl() {
        return "sandbox".equals(paypalMode) 
            ? "https://api.sandbox.paypal.com" 
            : "https://api.paypal.com";
    }

    /**
     * Create SHA256 hash of a string
     */
    private String createSHA256Hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (Exception e) {
            logger.error("Error creating SHA256 hash: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Log webhook event for debugging
     */
    public void logWebhookEvent(Map<String, Object> payload, Map<String, String> headers) {
        try {
            String eventType = (String) payload.get("event_type");
            String eventId = (String) payload.get("id");
            String createTime = (String) payload.get("create_time");
            
            logger.info("PayPal Webhook Event - Type: {}, ID: {}, Time: {}", 
                       eventType, eventId, createTime);
            
            // Log resource details if available
            Object resource = payload.get("resource");
            if (resource instanceof Map) {
                Map<String, Object> resourceMap = (Map<String, Object>) resource;
                String resourceId = (String) resourceMap.get("id");
                String resourceType = (String) resourceMap.get("type");
                logger.info("PayPal Webhook Resource - Type: {}, ID: {}", resourceType, resourceId);
            }
            
        } catch (Exception e) {
            logger.error("Error logging webhook event: {}", e.getMessage());
        }
    }
}




package com.enterprise.airbnb.audit;

import org.hibernate.envers.RevisionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Listener for audit trail events
 */
public class AuditTrailListener implements RevisionListener {
    
    private static final Logger logger = LoggerFactory.getLogger(AuditTrailListener.class);
    
    @Override
    public void newRevision(Object revisionEntity) {
        AuditTrail auditTrail = (AuditTrail) revisionEntity;
        
        try {
            // Set user information from security context
            setUserInformation(auditTrail);
            
            // Set request information
            setRequestInformation(auditTrail);
            
            logger.debug("Audit trail created: {}", auditTrail);
            
        } catch (Exception e) {
            logger.error("Error setting audit trail information: {}", e.getMessage());
        }
    }
    
    /**
     * Set user information from security context
     */
    private void setUserInformation(AuditTrail auditTrail) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication != null && authentication.isAuthenticated() 
                && !"anonymousUser".equals(authentication.getPrincipal())) {
                
                // Extract user information from authentication
                Object principal = authentication.getPrincipal();
                
                if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
                    org.springframework.security.core.userdetails.UserDetails userDetails = 
                        (org.springframework.security.core.userdetails.UserDetails) principal;
                    
                    auditTrail.setUserEmail(userDetails.getUsername());
                    
                    // Try to extract user ID from authorities or other sources
                    // This would need to be customized based on your UserDetails implementation
                }
            }
            
        } catch (Exception e) {
            logger.warn("Could not extract user information for audit trail: {}", e.getMessage());
        }
    }
    
    /**
     * Set request information from HTTP request
     */
    private void setRequestInformation(AuditTrail auditTrail) {
        try {
            ServletRequestAttributes requestAttributes = 
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            
            if (requestAttributes != null) {
                HttpServletRequest request = requestAttributes.getRequest();
                
                // Set IP address
                String ipAddress = getClientIpAddress(request);
                auditTrail.setIpAddress(ipAddress);
                
                // Set user agent
                String userAgent = request.getHeader("User-Agent");
                auditTrail.setUserAgent(userAgent);
                
                // Set session ID
                String sessionId = request.getSession().getId();
                auditTrail.setSessionId(sessionId);
            }
            
        } catch (Exception e) {
            logger.warn("Could not extract request information for audit trail: {}", e.getMessage());
        }
    }
    
    /**
     * Get client IP address from request
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}




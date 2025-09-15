package com.enterprise.airbnb.controller;

import com.enterprise.airbnb.model.User;
import com.enterprise.airbnb.model.UserRole;
import com.enterprise.airbnb.model.AccountStatus;
import com.enterprise.airbnb.service.UserService;
import com.enterprise.airbnb.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller for Authentication operations
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * User registration endpoint
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        logger.info("User registration attempt for email: {}", registerRequest.getEmail());
        
        try {
            // Check if user already exists
            if (userService.getUserByEmail(registerRequest.getEmail()).isPresent()) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("Email already exists", "EMAIL_EXISTS"));
            }

            // Create new user
            User user = new User();
            user.setFirstName(registerRequest.getFirstName());
            user.setLastName(registerRequest.getLastName());
            user.setEmail(registerRequest.getEmail());
            user.setPassword(registerRequest.getPassword());
            user.setPhoneNumber(registerRequest.getPhoneNumber());
            user.setRole(UserRole.GUEST); // Default role
            user.setStatus(AccountStatus.ACTIVE);
            user.setPreferredCurrency(registerRequest.getPreferredCurrency() != null ? 
                registerRequest.getPreferredCurrency() : "USD");
            user.setPreferredLanguage(registerRequest.getPreferredLanguage() != null ? 
                registerRequest.getPreferredLanguage() : "en");
            user.setTotalReviews(0);
            user.setIsEmailVerified(false);
            user.setIsPhoneVerified(false);

            User savedUser = userService.createUser(user);
            
            // Generate JWT token
            final String token = jwtUtil.generateToken(savedUser.getEmail());
            
            logger.info("User registered successfully with ID: {}", savedUser.getId());
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(createAuthResponse(savedUser, token, "User registered successfully"));

        } catch (Exception e) {
            logger.error("Error during user registration: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(createErrorResponse("Registration failed: " + e.getMessage(), "REGISTRATION_FAILED"));
        }
    }

    /**
     * User login endpoint
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("User login attempt for email: {}", loginRequest.getEmail());
        
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
                )
            );

            // Get user details
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Optional<User> user = userService.getUserByEmail(userDetails.getUsername());
            
            if (user.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("User not found", "USER_NOT_FOUND"));
            }

            User userEntity = user.get();
            
            // Check if account is active
            if (userEntity.getStatus() != AccountStatus.ACTIVE) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("Account is not active", "ACCOUNT_INACTIVE"));
            }

            // Update last login
            userEntity.setLastLogin(LocalDateTime.now());
            userService.updateUser(userEntity);

            // Generate JWT token
            final String token = jwtUtil.generateToken(userEntity.getEmail());
            
            logger.info("User logged in successfully with ID: {}", userEntity.getId());
            
            return ResponseEntity.ok(createAuthResponse(userEntity, token, "Login successful"));

        } catch (DisabledException e) {
            logger.error("User account is disabled: {}", loginRequest.getEmail());
            return ResponseEntity.badRequest()
                .body(createErrorResponse("User account is disabled", "ACCOUNT_DISABLED"));
        } catch (BadCredentialsException e) {
            logger.error("Invalid credentials for email: {}", loginRequest.getEmail());
            return ResponseEntity.badRequest()
                .body(createErrorResponse("Invalid email or password", "INVALID_CREDENTIALS"));
        } catch (Exception e) {
            logger.error("Error during login: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(createErrorResponse("Login failed: " + e.getMessage(), "LOGIN_FAILED"));
        }
    }

    /**
     * User logout endpoint
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        logger.info("User logout request");
        
        try {
            // Clear security context
            SecurityContextHolder.clearContext();
            
            return ResponseEntity.ok(createSuccessResponse("Logout successful"));
        } catch (Exception e) {
            logger.error("Error during logout: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(createErrorResponse("Logout failed: " + e.getMessage(), "LOGOUT_FAILED"));
        }
    }

    /**
     * Refresh JWT token endpoint
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String authHeader) {
        logger.info("Token refresh request");
        
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("Invalid authorization header", "INVALID_HEADER"));
            }

            String token = authHeader.substring(7);
            
            // Validate current token
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("Invalid or expired token", "INVALID_TOKEN"));
            }

            // Extract username from token
            String email = jwtUtil.getUsernameFromToken(token);
            Optional<User> user = userService.getUserByEmail(email);
            
            if (user.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("User not found", "USER_NOT_FOUND"));
            }

            User userEntity = user.get();
            
            // Check if account is still active
            if (userEntity.getStatus() != AccountStatus.ACTIVE) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("Account is not active", "ACCOUNT_INACTIVE"));
            }

            // Generate new token
            String newToken = jwtUtil.generateToken(userEntity.getEmail());
            
            logger.info("Token refreshed successfully for user ID: {}", userEntity.getId());
            
            return ResponseEntity.ok(createAuthResponse(userEntity, newToken, "Token refreshed successfully"));

        } catch (Exception e) {
            logger.error("Error during token refresh: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(createErrorResponse("Token refresh failed: " + e.getMessage(), "REFRESH_FAILED"));
        }
    }

    /**
     * Get current user info endpoint
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        logger.info("Get current user request");
        
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("User not authenticated", "NOT_AUTHENTICATED"));
            }

            String email = authentication.getName();
            Optional<User> user = userService.getUserByEmail(email);
            
            if (user.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("User not found", "USER_NOT_FOUND"));
            }

            return ResponseEntity.ok(createUserResponse(user.get()));

        } catch (Exception e) {
            logger.error("Error getting current user: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(createErrorResponse("Failed to get user info: " + e.getMessage(), "GET_USER_FAILED"));
        }
    }

    /**
     * Change password endpoint
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        logger.info("Change password request");
        
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            Optional<User> user = userService.getUserByEmail(email);
            if (user.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("User not found", "USER_NOT_FOUND"));
            }

            User userEntity = user.get();
            
            // Verify current password
            Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, changePasswordRequest.getCurrentPassword())
            );

            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("Current password is incorrect", "INVALID_CURRENT_PASSWORD"));
            }

            // Update password
            userEntity.setPassword(changePasswordRequest.getNewPassword());
            userService.updateUser(userEntity);
            
            logger.info("Password changed successfully for user ID: {}", userEntity.getId());
            
            return ResponseEntity.ok(createSuccessResponse("Password changed successfully"));

        } catch (Exception e) {
            logger.error("Error changing password: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(createErrorResponse("Password change failed: " + e.getMessage(), "PASSWORD_CHANGE_FAILED"));
        }
    }

    /**
     * Verify email endpoint
     */
    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        logger.info("Email verification request");
        
        try {
            // TODO: Implement email verification logic with token validation
            // For now, just return success
            return ResponseEntity.ok(createSuccessResponse("Email verification not implemented yet"));

        } catch (Exception e) {
            logger.error("Error verifying email: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(createErrorResponse("Email verification failed: " + e.getMessage(), "VERIFICATION_FAILED"));
        }
    }

    // Helper methods for creating response objects

    private Map<String, Object> createAuthResponse(User user, String token, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("user", createUserResponse(user));
        response.put("token", token);
        response.put("message", message);
        response.put("success", true);
        return response;
    }

    private Map<String, Object> createUserResponse(User user) {
        Map<String, Object> userResponse = new HashMap<>();
        userResponse.put("id", user.getId());
        userResponse.put("firstName", user.getFirstName());
        userResponse.put("lastName", user.getLastName());
        userResponse.put("email", user.getEmail());
        userResponse.put("phoneNumber", user.getPhoneNumber());
        userResponse.put("role", user.getRole());
        userResponse.put("status", user.getStatus());
        userResponse.put("profileImageUrl", user.getProfileImageUrl());
        userResponse.put("bio", user.getBio());
        userResponse.put("dateOfBirth", user.getDateOfBirth());
        userResponse.put("isEmailVerified", user.getIsEmailVerified());
        userResponse.put("isPhoneVerified", user.getIsPhoneVerified());
        userResponse.put("preferredCurrency", user.getPreferredCurrency());
        userResponse.put("preferredLanguage", user.getPreferredLanguage());
        userResponse.put("hostRating", user.getHostRating());
        userResponse.put("guestRating", user.getGuestRating());
        userResponse.put("totalReviews", user.getTotalReviews());
        userResponse.put("lastLogin", user.getLastLogin());
        userResponse.put("createdAt", user.getCreatedAt());
        userResponse.put("updatedAt", user.getUpdatedAt());
        return userResponse;
    }

    private Map<String, Object> createSuccessResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("success", true);
        return response;
    }

    private Map<String, Object> createErrorResponse(String message, String errorCode) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("errorCode", errorCode);
        response.put("success", false);
        return response;
    }

    // Request DTOs

    public static class RegisterRequest {
        private String firstName;
        private String lastName;
        private String email;
        private String password;
        private String phoneNumber;
        private String preferredCurrency;
        private String preferredLanguage;

        // Constructors
        public RegisterRequest() {}

        // Getters and Setters
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }

        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

        public String getPreferredCurrency() { return preferredCurrency; }
        public void setPreferredCurrency(String preferredCurrency) { this.preferredCurrency = preferredCurrency; }

        public String getPreferredLanguage() { return preferredLanguage; }
        public void setPreferredLanguage(String preferredLanguage) { this.preferredLanguage = preferredLanguage; }
    }

    public static class LoginRequest {
        private String email;
        private String password;

        // Constructors
        public LoginRequest() {}

        // Getters and Setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class ChangePasswordRequest {
        private String currentPassword;
        private String newPassword;

        // Constructors
        public ChangePasswordRequest() {}

        // Getters and Setters
        public String getCurrentPassword() { return currentPassword; }
        public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }

        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}

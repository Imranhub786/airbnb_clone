package com.enterprise.airbnb.service;

import com.enterprise.airbnb.model.User;
import com.enterprise.airbnb.model.UserRole;
import com.enterprise.airbnb.model.AccountStatus;
import com.enterprise.airbnb.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service for User entity operations
 */
@Service
@Transactional
public class UserService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailAndStatus(email, AccountStatus.ACTIVE)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getStatus() == AccountStatus.ACTIVE,
                true, // accountNonExpired
                true, // credentialsNonExpired
                !user.getStatus().isRestricted(), // accountNonLocked
                authorities
        );
    }

    /**
     * Create a new user
     */
    @CacheEvict(value = "users", allEntries = true)
    public User createUser(User user) {
        logger.info("Creating new user with email: {}", user.getEmail());
        
        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + user.getEmail());
        }

        // Check if phone number already exists
        if (user.getPhoneNumber() != null && userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone number already exists: " + user.getPhoneNumber());
        }

        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Set default values
        if (user.getRole() == null) {
            user.setRole(UserRole.GUEST);
        }
        if (user.getStatus() == null) {
            user.setStatus(AccountStatus.ACTIVE);
        }
        if (user.getPreferredCurrency() == null) {
            user.setPreferredCurrency("USD");
        }
        if (user.getPreferredLanguage() == null) {
            user.setPreferredLanguage("en");
        }
        if (user.getTotalReviews() == null) {
            user.setTotalReviews(0);
        }

        User savedUser = userRepository.save(user);
        logger.info("User created successfully with ID: {}", savedUser.getId());
        return savedUser;
    }

    /**
     * Get user by ID
     */
    @Cacheable(value = "users", key = "#id")
    public Optional<User> getUserById(Long id) {
        logger.debug("Fetching user by ID: {}", id);
        return userRepository.findById(id);
    }

    /**
     * Get user by email
     */
    @Cacheable(value = "users", key = "#email")
    public Optional<User> getUserByEmail(String email) {
        logger.debug("Fetching user by email: {}", email);
        return userRepository.findByEmail(email);
    }

    /**
     * Get user by email and status
     */
    @Cacheable(value = "users", key = "#email + '_' + #status")
    public Optional<User> getUserByEmailAndStatus(String email, AccountStatus status) {
        logger.debug("Fetching user by email and status: {} - {}", email, status);
        return userRepository.findByEmailAndStatus(email, status);
    }

    /**
     * Get user by phone number
     */
    @Cacheable(value = "users", key = "#phoneNumber")
    public Optional<User> getUserByPhoneNumber(String phoneNumber) {
        logger.debug("Fetching user by phone number: {}", phoneNumber);
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    /**
     * Update user
     */
    @CachePut(value = "users", key = "#user.id")
    @CacheEvict(value = "users", allEntries = true)
    public User updateUser(User user) {
        logger.info("Updating user with ID: {}", user.getId());
        
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + user.getId()));

        // Check if email is being changed and if it already exists
        if (!existingUser.getEmail().equals(user.getEmail()) && userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + user.getEmail());
        }

        // Check if phone number is being changed and if it already exists
        if (user.getPhoneNumber() != null && 
            !user.getPhoneNumber().equals(existingUser.getPhoneNumber()) && 
            userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone number already exists: " + user.getPhoneNumber());
        }

        // Don't update password here - use changePassword method
        user.setPassword(existingUser.getPassword());
        
        User updatedUser = userRepository.save(user);
        logger.info("User updated successfully with ID: {}", updatedUser.getId());
        return updatedUser;
    }

    /**
     * Change user password
     */
    @CacheEvict(value = "users", key = "#userId")
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        logger.info("Changing password for user ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        // Verify old password
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        // Encode new password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        logger.info("Password changed successfully for user ID: {}", userId);
    }

    /**
     * Reset user password
     */
    @CacheEvict(value = "users", key = "#userId")
    public void resetPassword(Long userId, String newPassword) {
        logger.info("Resetting password for user ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        // Encode new password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        logger.info("Password reset successfully for user ID: {}", userId);
    }

    /**
     * Delete user
     */
    @CacheEvict(value = "users", allEntries = true)
    public void deleteUser(Long id) {
        logger.info("Deleting user with ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));

        userRepository.delete(user);
        logger.info("User deleted successfully with ID: {}", id);
    }

    /**
     * Deactivate user
     */
    @CacheEvict(value = "users", key = "#id")
    public void deactivateUser(Long id) {
        logger.info("Deactivating user with ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));

        user.setStatus(AccountStatus.INACTIVE);
        userRepository.save(user);
        
        logger.info("User deactivated successfully with ID: {}", id);
    }

    /**
     * Activate user
     */
    @CacheEvict(value = "users", key = "#id")
    public void activateUser(Long id) {
        logger.info("Activating user with ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));

        user.setStatus(AccountStatus.ACTIVE);
        userRepository.save(user);
        
        logger.info("User activated successfully with ID: {}", id);
    }

    /**
     * Suspend user
     */
    @CacheEvict(value = "users", key = "#id")
    public void suspendUser(Long id) {
        logger.info("Suspending user with ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));

        user.setStatus(AccountStatus.SUSPENDED);
        userRepository.save(user);
        
        logger.info("User suspended successfully with ID: {}", id);
    }

    /**
     * Update user role
     */
    @CacheEvict(value = "users", key = "#id")
    public void updateUserRole(Long id, UserRole role) {
        logger.info("Updating user role for ID: {} to {}", id, role);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));

        user.setRole(role);
        userRepository.save(user);
        
        logger.info("User role updated successfully for ID: {}", id);
    }

    /**
     * Update last login
     */
    @CacheEvict(value = "users", key = "#id")
    public void updateLastLogin(Long id) {
        logger.debug("Updating last login for user ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
    }

    /**
     * Verify email
     */
    @CacheEvict(value = "users", key = "#id")
    public void verifyEmail(Long id) {
        logger.info("Verifying email for user ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));

        user.setIsEmailVerified(true);
        if (user.getStatus() == AccountStatus.PENDING_VERIFICATION) {
            user.setStatus(AccountStatus.ACTIVE);
        }
        userRepository.save(user);
        
        logger.info("Email verified successfully for user ID: {}", id);
    }

    /**
     * Verify phone
     */
    @CacheEvict(value = "users", key = "#id")
    public void verifyPhone(Long id) {
        logger.info("Verifying phone for user ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));

        user.setIsPhoneVerified(true);
        userRepository.save(user);
        
        logger.info("Phone verified successfully for user ID: {}", id);
    }

    /**
     * Get all users
     */
    public List<User> getAllUsers() {
        logger.debug("Fetching all users");
        return userRepository.findAll();
    }

    /**
     * Get all users with pagination
     */
    public Page<User> getAllUsers(Pageable pageable) {
        logger.debug("Fetching all users with pagination");
        return userRepository.findAll(pageable);
    }

    /**
     * Get users by role
     */
    @Cacheable(value = "users", key = "#role")
    public List<User> getUsersByRole(UserRole role) {
        logger.debug("Fetching users by role: {}", role);
        return userRepository.findByRole(role);
    }

    /**
     * Get users by role with pagination
     */
    @Cacheable(value = "users", key = "#role + '_page'")
    public Page<User> getUsersByRole(UserRole role, Pageable pageable) {
        logger.debug("Fetching users by role with pagination: {}", role);
        return userRepository.findByRole(role, pageable);
    }

    /**
     * Get users by status
     */
    @Cacheable(value = "users", key = "#status")
    public List<User> getUsersByStatus(AccountStatus status) {
        logger.debug("Fetching users by status: {}", status);
        return userRepository.findByStatus(status);
    }

    /**
     * Get users by status with pagination
     */
    @Cacheable(value = "users", key = "#status + '_page'")
    public Page<User> getUsersByStatus(AccountStatus status, Pageable pageable) {
        logger.debug("Fetching users by status with pagination: {}", status);
        return userRepository.findByStatus(status, pageable);
    }

    /**
     * Get users by role and status
     */
    @Cacheable(value = "users", key = "#role + '_' + #status")
    public List<User> getUsersByRoleAndStatus(UserRole role, AccountStatus status) {
        logger.debug("Fetching users by role and status: {} - {}", role, status);
        return userRepository.findByRoleAndStatus(role, status);
    }

    /**
     * Get users by role and status with pagination
     */
    @Cacheable(value = "users", key = "#role + '_' + #status + '_page'")
    public Page<User> getUsersByRoleAndStatus(UserRole role, AccountStatus status, Pageable pageable) {
        logger.debug("Fetching users by role and status with pagination: {} - {}", role, status);
        return userRepository.findByRoleAndStatus(role, status, pageable);
    }

    /**
     * Search users
     */
    @Cacheable(value = "users", key = "#query")
    public List<User> searchUsers(String query) {
        logger.debug("Searching users with query: {}", query);
        return userRepository.searchUsers(query);
    }

    /**
     * Search users with pagination
     */
    @Cacheable(value = "users", key = "#query + '_page'")
    public Page<User> searchUsers(String query, Pageable pageable) {
        logger.debug("Searching users with query and pagination: {}", query);
        return userRepository.searchUsers(query, pageable);
    }

    /**
     * Get active users
     */
    @Cacheable(value = "users", key = "'active'")
    public List<User> getActiveUsers() {
        logger.debug("Fetching active users");
        return userRepository.findActiveUsers();
    }

    /**
     * Get hosts with properties
     */
    @Cacheable(value = "users", key = "'hosts_with_properties'")
    public List<User> getHostsWithProperties() {
        logger.debug("Fetching hosts with properties");
        return userRepository.findHostsWithProperties();
    }

    /**
     * Get hosts without properties
     */
    @Cacheable(value = "users", key = "'hosts_without_properties'")
    public List<User> getHostsWithoutProperties() {
        logger.debug("Fetching hosts without properties");
        return userRepository.findHostsWithoutProperties();
    }

    /**
     * Get users with bookings
     */
    @Cacheable(value = "users", key = "'users_with_bookings'")
    public List<User> getUsersWithBookings() {
        logger.debug("Fetching users with bookings");
        return userRepository.findUsersWithBookings();
    }

    /**
     * Get users without bookings
     */
    @Cacheable(value = "users", key = "'users_without_bookings'")
    public List<User> getUsersWithoutBookings() {
        logger.debug("Fetching users without bookings");
        return userRepository.findUsersWithoutBookings();
    }

    /**
     * Count users by role
     */
    @Cacheable(value = "stats", key = "'user_count_' + #role")
    public long countUsersByRole(UserRole role) {
        logger.debug("Counting users by role: {}", role);
        return userRepository.countByRole(role);
    }

    /**
     * Count users by status
     */
    @Cacheable(value = "stats", key = "'user_count_' + #status")
    public long countUsersByStatus(AccountStatus status) {
        logger.debug("Counting users by status: {}", status);
        return userRepository.countByStatus(status);
    }

    /**
     * Count users by role and status
     */
    @Cacheable(value = "stats", key = "'user_count_' + #role + '_' + #status")
    public long countUsersByRoleAndStatus(UserRole role, AccountStatus status) {
        logger.debug("Counting users by role and status: {} - {}", role, status);
        return userRepository.countByRoleAndStatus(role, status);
    }

    /**
     * Check if email exists
     */
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Check if phone number exists
     */
    public boolean phoneNumberExists(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

}



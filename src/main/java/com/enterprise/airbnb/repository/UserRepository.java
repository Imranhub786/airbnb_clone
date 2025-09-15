package com.enterprise.airbnb.repository;

import com.enterprise.airbnb.model.User;
import com.enterprise.airbnb.model.UserRole;
import com.enterprise.airbnb.model.AccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for User entity operations
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Find user by email and status
     */
    Optional<User> findByEmailAndStatus(String email, AccountStatus status);
    
    /**
     * Find users by role
     */
    List<User> findByRole(UserRole role);
    
    /**
     * Find users by status
     */
    List<User> findByStatus(AccountStatus status);
    
    /**
     * Find users by role and status
     */
    List<User> findByRoleAndStatus(UserRole role, AccountStatus status);
    
    /**
     * Find users by role with pagination
     */
    Page<User> findByRole(UserRole role, Pageable pageable);
    
    /**
     * Find users by status with pagination
     */
    Page<User> findByStatus(AccountStatus status, Pageable pageable);
    
    /**
     * Find users by role and status with pagination
     */
    Page<User> findByRoleAndStatus(UserRole role, AccountStatus status, Pageable pageable);
    
    /**
     * Find users by preferred currency
     */
    List<User> findByPreferredCurrency(String currency);
    
    /**
     * Find users by preferred language
     */
    List<User> findByPreferredLanguage(String language);
    
    /**
     * Find users created after a specific date
     */
    List<User> findByCreatedAtAfter(LocalDateTime date);
    
    /**
     * Find users created between dates
     */
    List<User> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find users with last login after a specific date
     */
    List<User> findByLastLoginAfter(LocalDateTime date);
    
    /**
     * Find users with email verification status
     */
    List<User> findByIsEmailVerified(Boolean isEmailVerified);
    
    /**
     * Find users with phone verification status
     */
    List<User> findByIsPhoneVerified(Boolean isPhoneVerified);
    
    /**
     * Find users by host rating range
     */
    List<User> findByHostRatingBetween(Double minRating, Double maxRating);
    
    /**
     * Find users by guest rating range
     */
    List<User> findByGuestRatingBetween(Double minRating, Double maxRating);
    
    /**
     * Find users with minimum total reviews
     */
    List<User> findByTotalReviewsGreaterThanEqual(Integer minReviews);
    
    /**
     * Find users by first name containing (case insensitive)
     */
    List<User> findByFirstNameContainingIgnoreCase(String firstName);
    
    /**
     * Find users by last name containing (case insensitive)
     */
    List<User> findByLastNameContainingIgnoreCase(String lastName);
    
    /**
     * Find users by full name containing (case insensitive)
     */
    @Query("SELECT u FROM User u WHERE LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<User> findByFullNameContainingIgnoreCase(@Param("name") String name);
    
    /**
     * Find users by email containing (case insensitive)
     */
    List<User> findByEmailContainingIgnoreCase(String email);
    
    /**
     * Find users by phone number
     */
    Optional<User> findByPhoneNumber(String phoneNumber);
    
    /**
     * Find users by phone number containing
     */
    List<User> findByPhoneNumberContaining(String phoneNumber);
    
    /**
     * Find active users
     */
    @Query("SELECT u FROM User u WHERE u.status = 'ACTIVE'")
    List<User> findActiveUsers();
    
    /**
     * Find hosts with properties
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.properties p WHERE u.role IN ('HOST', 'SUPER_HOST')")
    List<User> findHostsWithProperties();
    
    /**
     * Find hosts without properties
     */
    @Query("SELECT u FROM User u WHERE u.role IN ('HOST', 'SUPER_HOST') AND u.properties IS EMPTY")
    List<User> findHostsWithoutProperties();
    
    /**
     * Find users with bookings
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.bookings b")
    List<User> findUsersWithBookings();
    
    /**
     * Find users without bookings
     */
    @Query("SELECT u FROM User u WHERE u.bookings IS EMPTY")
    List<User> findUsersWithoutBookings();
    
    /**
     * Find users by location (city)
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.properties p WHERE p.address.city = :city")
    List<User> findUsersByCity(@Param("city") String city);
    
    /**
     * Find users by location (country)
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.properties p WHERE p.address.country = :country")
    List<User> findUsersByCountry(@Param("country") String country);
    
    /**
     * Count users by role
     */
    long countByRole(UserRole role);
    
    /**
     * Count users by status
     */
    long countByStatus(AccountStatus status);
    
    /**
     * Count users by role and status
     */
    long countByRoleAndStatus(UserRole role, AccountStatus status);
    
    /**
     * Count users created after a specific date
     */
    long countByCreatedAtAfter(LocalDateTime date);
    
    /**
     * Count users with email verification
     */
    long countByIsEmailVerified(Boolean isEmailVerified);
    
    /**
     * Count users with phone verification
     */
    long countByIsPhoneVerified(Boolean isPhoneVerified);
    
    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);
    
    /**
     * Check if phone number exists
     */
    boolean existsByPhoneNumber(String phoneNumber);
    
    /**
     * Find users with similar names (for search)
     */
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<User> searchUsers(@Param("query") String query);
    
    /**
     * Find users with similar names with pagination
     */
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<User> searchUsers(@Param("query") String query, Pageable pageable);
    
    // Missing method for compilation
    Page<User> findByRoleIn(List<String> roles, Pageable pageable);
}


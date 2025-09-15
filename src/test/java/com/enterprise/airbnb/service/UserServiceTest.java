package com.enterprise.airbnb.service;

import com.enterprise.airbnb.model.User;
import com.enterprise.airbnb.model.UserRole;
import com.enterprise.airbnb.model.AccountStatus;
import com.enterprise.airbnb.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private User testHost;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole(UserRole.GUEST);
        testUser.setStatus(AccountStatus.ACTIVE);
        testUser.setPhoneNumber("+1234567890");
        testUser.setCreatedAt(LocalDateTime.now());

        testHost = new User();
        testHost.setId(2L);
        testHost.setFirstName("Jane");
        testHost.setLastName("Smith");
        testHost.setEmail("jane.smith@example.com");
        testHost.setPassword("encodedPassword");
        testHost.setRole(UserRole.HOST);
        testHost.setStatus(AccountStatus.ACTIVE);
        testHost.setPhoneNumber("+1234567891");
        testHost.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testLoadUserByUsername_Success() {
        // Given
        when(userRepository.findByEmailAndStatus("john.doe@example.com", AccountStatus.ACTIVE))
                .thenReturn(Optional.of(testUser));

        // When
        UserDetails userDetails = userService.loadUserByUsername("john.doe@example.com");

        // Then
        assertNotNull(userDetails);
        assertEquals("john.doe@example.com", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_GUEST")));
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        // Given
        when(userRepository.findByEmailAndStatus("nonexistent@example.com", AccountStatus.ACTIVE))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("nonexistent@example.com");
        });
    }

    @Test
    void testCreateUser_Success() {
        // Given
        User newUser = new User();
        newUser.setFirstName("New");
        newUser.setLastName("User");
        newUser.setEmail("new.user@example.com");
        newUser.setPassword("rawPassword");
        newUser.setRole(UserRole.GUEST);

        when(userRepository.existsByEmail("new.user@example.com")).thenReturn(false);
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.createUser(newUser);

        // Then
        assertNotNull(result);
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("rawPassword");
    }

    @Test
    void testCreateUser_EmailAlreadyExists() {
        // Given
        User newUser = new User();
        newUser.setEmail("john.doe@example.com");

        when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            userService.createUser(newUser);
        });
    }

    @Test
    void testGetUserById_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.getUserById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
    }

    @Test
    void testGetUserById_NotFound() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.getUserById(999L);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testGetUserByEmail_Success() {
        // Given
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.getUserByEmail("john.doe@example.com");

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
    }

    @Test
    void testGetAllUsers_Success() {
        // Given
        List<User> users = Arrays.asList(testUser, testHost);
        when(userRepository.findAll()).thenReturn(users);

        // When
        List<User> result = userService.getAllUsers();

        // Then
        assertEquals(2, result.size());
        assertTrue(result.contains(testUser));
        assertTrue(result.contains(testHost));
    }

    @Test
    void testGetAllUsersWithPagination_Success() {
        // Given
        List<User> users = Arrays.asList(testUser, testHost);
        Page<User> userPage = new PageImpl<>(users);
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findAll(pageable)).thenReturn(userPage);

        // When
        Page<User> result = userService.getAllUsers(pageable);

        // Then
        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());
    }

    @Test
    void testUpdateUser_Success() {
        // Given
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setFirstName("Updated");
        updatedUser.setLastName("Name");
        updatedUser.setEmail("john.doe@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // When
        User result = userService.updateUser(1L, updatedUser);

        // Then
        assertNotNull(result);
        assertEquals("Updated", result.getFirstName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testUpdateUser_UserNotFound() {
        // Given
        User updatedUser = new User();
        updatedUser.setId(999L);

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            userService.updateUser(999L, updatedUser);
        });
    }

    @Test
    void testDeleteUser_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        userService.deleteUser(1L);

        // Then
        verify(userRepository).delete(testUser);
    }

    @Test
    void testDeleteUser_UserNotFound() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            userService.deleteUser(999L);
        });
    }

    @Test
    void testGetUsersByRole_Success() {
        // Given
        List<User> hosts = Arrays.asList(testHost);
        when(userRepository.findByRole(UserRole.HOST)).thenReturn(hosts);

        // When
        List<User> result = userService.getUsersByRole(UserRole.HOST);

        // Then
        assertEquals(1, result.size());
        assertEquals(UserRole.HOST, result.get(0).getRole());
    }

    @Test
    void testEmailExists_True() {
        // Given
        when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

        // When
        boolean result = userService.emailExists("john.doe@example.com");

        // Then
        assertTrue(result);
    }

    @Test
    void testEmailExists_False() {
        // Given
        when(userRepository.existsByEmail("nonexistent@example.com")).thenReturn(false);

        // When
        boolean result = userService.emailExists("nonexistent@example.com");

        // Then
        assertFalse(result);
    }

    @Test
    void testPhoneNumberExists_True() {
        // Given
        when(userRepository.existsByPhoneNumber("+1234567890")).thenReturn(true);

        // When
        boolean result = userService.phoneNumberExists("+1234567890");

        // Then
        assertTrue(result);
    }

    @Test
    void testPhoneNumberExists_False() {
        // Given
        when(userRepository.existsByPhoneNumber("+9999999999")).thenReturn(false);

        // When
        boolean result = userService.phoneNumberExists("+9999999999");

        // Then
        assertFalse(result);
    }
}




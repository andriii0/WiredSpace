package org.main.wiredspaceapi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.main.wiredspaceapi.business.impl.UserServiceImpl;
import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.domain.enums.UserRole;
import org.main.wiredspaceapi.persistence.UserRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        user = new User("TestUser", "test@example.com", "encodedPassword", UserRole.STANDARD_USER);
    }

    @Test
    void createUser_ShouldReturnSavedUser() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");
        when(userRepository.createUser("TestUser", "test@example.com", "encodedPassword123", UserRole.STANDARD_USER))
                .thenReturn(user);

        User createdUser = userService.createUser("TestUser", "test@example.com", "password123", UserRole.STANDARD_USER);

        assertNotNull(createdUser);
        assertEquals("TestUser", createdUser.getName());
        verify(passwordEncoder).encode("password123");
        verify(userRepository).createUser("TestUser", "test@example.com", "encodedPassword123", UserRole.STANDARD_USER);
    }

    @Test
    void createUser_ShouldThrowException_WhenEmailExists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.createUser("TestUser", "test@example.com", "password123", UserRole.STANDARD_USER));

        assertEquals("Email test@example.com is already in use", exception.getMessage());
        verify(userRepository, never()).createUser(any(), any(), any(), any());
    }

    @Test
    void getUserById_ShouldReturnUser_WhenExists() {
        when(userRepository.getUserById(userId)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(userId);

        assertTrue(result.isPresent());
        assertEquals("TestUser", result.get().getName());
        verify(userRepository).getUserById(userId);
    }

    @Test
    void getUserById_ShouldReturnEmpty_WhenNotFound() {
        when(userRepository.getUserById(userId)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(userId);

        assertTrue(result.isEmpty());
        verify(userRepository).getUserById(userId);
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() {
        when(userRepository.getAllUsers()).thenReturn(List.of(user));

        List<User> users = userService.getAllUsers();

        assertEquals(1, users.size());
        assertEquals("TestUser", users.get(0).getName());
        verify(userRepository).getAllUsers();
    }

    @Test
    void updateUserByEmail_ShouldReturnUpdatedUser() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(userRepository.updateUser(userId, "NewName", "new@example.com", "encodedNewPassword"))
                .thenReturn(Optional.of(user));

        Optional<User> updatedUser = userService.updateUserByEmail("test@example.com", "NewName", "new@example.com", "newPassword");

        assertTrue(updatedUser.isPresent());
        verify(userRepository).updateUser(userId, "NewName", "new@example.com", "encodedNewPassword");
    }

    @Test
    void updateUserByEmail_ShouldReturnEmpty_WhenUserNotFound() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        Optional<User> updatedUser = userService.updateUserByEmail("notfound@example.com", "NewName", null, null);

        assertTrue(updatedUser.isEmpty());
        verify(userRepository, never()).updateUser(any(), any(), any(), any());
    }

    @Test
    void deleteUserByEmail_ShouldCallRepository() {
        doNothing().when(userRepository).deleteUserByEmail("test@example.com");

        userService.deleteUserByEmail("test@example.com");

        verify(userRepository).deleteUserByEmail("test@example.com");
    }

    @Test
    void deleteUser_ShouldCallRepository() {
        doNothing().when(userRepository).deleteUser(userId);

        userService.deleteUser(userId);

        verify(userRepository).deleteUser(userId);
    }

    @Test
    void findByEmail_ShouldReturnUser_WhenFound() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByEmail("test@example.com");

        assertTrue(result.isPresent());
        assertEquals("TestUser", result.get().getName());
    }

    @Test
    void findByEmail_ShouldReturnEmpty_WhenNotFound() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        Optional<User> result = userService.findByEmail("notfound@example.com");

        assertTrue(result.isEmpty());
    }
}

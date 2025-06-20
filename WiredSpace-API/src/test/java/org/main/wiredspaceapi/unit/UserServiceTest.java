package org.main.wiredspaceapi.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.main.wiredspaceapi.business.impl.UserServiceImpl;
import org.main.wiredspaceapi.controller.exceptions.AccountAlreadyExistsException;
import org.main.wiredspaceapi.controller.exceptions.UserNotFoundException;
import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.domain.enums.UserRole;
import org.main.wiredspaceapi.persistence.UserRepository;
import org.main.wiredspaceapi.security.util.AuthenticatedUserProvider;
import org.main.wiredspaceapi.business.impl.UserDeletionService;
import org.main.wiredspaceapi.business.impl.MessageServiceImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticatedUserProvider userProvider;
    @Mock private UserDeletionService userDeletionService;
    @Mock private MessageServiceImpl messageService;

    @InjectMocks private UserServiceImpl userService;

    private User user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new User("TestUser", "test@example.com", "encodedPassword", UserRole.STANDARD_USER);
        user.setId(userId);
    }

    @Test
    void createUser_ShouldReturnSavedUser() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");
        when(userRepository.createUser(any(), any(), any(), any(), any())).thenReturn(user);

        User createdUser = userService.createUser("TestUser", "test@example.com", "password123", UserRole.STANDARD_USER);

        assertNotNull(createdUser);
        assertEquals("TestUser", createdUser.getName());
        verify(passwordEncoder).encode("password123");
        verify(userRepository).createUser(any(), any(), any(), any(), any());
    }

    @Test
    void createUser_ShouldThrowException_WhenEmailExists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        AccountAlreadyExistsException ex = assertThrows(AccountAlreadyExistsException.class, () ->
                userService.createUser("TestUser", "test@example.com", "password123", UserRole.STANDARD_USER));

        assertEquals("Email test@example.com is already in use", ex.getMessage());
    }

    @Test
    void getUserById_ShouldReturnUser_WhenExists() {
        when(userRepository.getUserById(userId)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(userId);

        assertTrue(result.isPresent());
        assertEquals("TestUser", result.get().getName());
    }

    @Test
    void getUserById_ShouldThrow_WhenNotFound() {
        when(userRepository.getUserById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() {
        when(userRepository.getAllUsers()).thenReturn(List.of(user));

        List<User> users = userService.getAllUsers();

        assertEquals(1, users.size());
    }

    @Test
    void updateUserById_ShouldUpdate_WhenValidInput() {
        when(userRepository.getUserById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(userRepository.updateUser(eq(userId), eq("NewName"), eq("new@example.com"), eq("encodedNewPassword")))
                .thenReturn(Optional.of(user));

        Optional<User> updated = userService.updateUserById(userId, "NewName", "new@example.com", "newPassword");

        assertTrue(updated.isPresent());
        verify(userProvider).validateCurrentUserAccess(userId);
    }

    @Test
    void updateUserById_ShouldUseOldValues_WhenInputsNullOrEmpty() {
        when(userRepository.getUserById(userId)).thenReturn(Optional.of(user));
        when(userRepository.updateUser(userId, user.getName(), user.getEmail(), user.getPassword()))
                .thenReturn(Optional.of(user));

        Optional<User> updated = userService.updateUserById(userId, "", null, "");

        assertTrue(updated.isPresent());
    }

    @Test
    void updateUserById_ShouldThrow_WhenUserNotFound() {
        when(userRepository.getUserById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                userService.updateUserById(userId, "a", "b", "c"));
    }

    @Test
    void deleteUserByEmail_ShouldCallDeletion_WhenFound() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        userService.deleteUserByEmail("test@example.com");

        verify(userProvider).validateCurrentUserAccess("test@example.com");
        verify(userDeletionService).deleteUserCompletely(userId);
    }

    @Test
    void deleteUserByEmail_ShouldThrow_WhenNotFound() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUserByEmail("test@example.com"));
    }

    @Test
    void deleteUserById_ShouldCallDeletion_WhenFound() {
        when(userRepository.getUserById(userId)).thenReturn(Optional.of(user));

        userService.deleteUserById(userId);

        verify(userProvider).validateCurrentUserAccess(userId);
        verify(userDeletionService).deleteUserCompletely(userId);
    }

    @Test
    void deleteUserById_ShouldThrow_WhenNotFound() {
        when(userRepository.getUserById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUserById(userId));
    }

    @Test
    void findByEmail_ShouldReturnUser_WhenFound() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByEmail("test@example.com");

        assertTrue(result.isPresent());
    }

    @Test
    void findByEmail_ShouldReturnEmpty_WhenNotFound() {
        when(userRepository.findByEmail("nope@example.com")).thenReturn(Optional.empty());

        Optional<User> result = userService.findByEmail("nope@example.com");

        assertTrue(result.isEmpty());
    }

    @Test
    void searchUsers_ShouldReturnResults() {
        String query = "test";
        int offset = 0;
        int limit = 10;

        when(userProvider.getCurrentUserId()).thenReturn(userId);
        when(userRepository.searchUsers(query, offset, limit, userId)).thenReturn(List.of(user));

        List<User> result = userService.searchUsers(query, offset, limit);

        assertEquals(1, result.size());
    }

    @Test
    void searchUsers_ShouldThrow_WhenInvalidOffset() {
        assertThrows(IllegalArgumentException.class, () -> userService.searchUsers("q", -1, 10));
    }

    @Test
    void searchUsers_ShouldThrow_WhenInvalidLimit() {
        assertThrows(IllegalArgumentException.class, () -> userService.searchUsers("q", 0, 0));
    }

    @Test
    void searchUsers_ShouldThrow_WhenOffsetNotMultipleOfLimit() {
        assertThrows(IllegalArgumentException.class, () -> userService.searchUsers("q", 5, 2));
    }

    @Test
    void countSearchUsers_ShouldReturnCount() {
        when(userRepository.countSearchUsers("abc")).thenReturn(3L);

        long count = userService.countSearchUsers("abc");

        assertEquals(3L, count);
    }
}

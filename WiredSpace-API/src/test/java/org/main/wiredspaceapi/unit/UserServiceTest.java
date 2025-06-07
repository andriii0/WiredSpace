package org.main.wiredspaceapi.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.main.wiredspaceapi.business.impl.UserServiceImpl;
import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.domain.enums.UserRole;
import org.main.wiredspaceapi.persistence.UserRepository;
import org.main.wiredspaceapi.security.util.AuthenticatedUserProvider;
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

    @Mock
    private AuthenticatedUserProvider userProvider;

    @InjectMocks
    private UserServiceImpl userService;

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
    void updateUserById_ShouldReturnUpdatedUser() {
        when(userRepository.getUserById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(userRepository.updateUser(userId, "NewName", "new@example.com", "encodedNewPassword"))
                .thenReturn(Optional.of(user));

        Optional<User> updatedUser = userService.updateUserById(userId, "NewName", "new@example.com", "newPassword");

        assertTrue(updatedUser.isPresent());
        verify(userRepository).updateUser(userId, "NewName", "new@example.com", "encodedNewPassword");
    }

    @Test
    void deleteUserByEmail_ShouldCallRepository() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        doNothing().when(userProvider).validateCurrentUserAccess("test@example.com");
        doNothing().when(userRepository).deleteUser(userId);

        userService.deleteUserByEmail("test@example.com");

        verify(userProvider).validateCurrentUserAccess("test@example.com");
        verify(userRepository).deleteUser(userId);
    }

    @Test
    void deleteUser_ShouldCallRepository() {
        doNothing().when(userProvider).validateCurrentUserAccess(userId);
        doNothing().when(userRepository).deleteUser(userId);

        userService.deleteUser(userId);

        verify(userProvider).validateCurrentUserAccess(userId);
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
    @Test
    void searchUsers_ShouldReturnMatchingUsers() {
        String query = "test";
        int offset = 0;
        int limit = 10;

        when(userRepository.searchUsers(query, offset, limit)).thenReturn(List.of(user));

        List<User> result = userService.searchUsers(query, offset, limit);

        assertEquals(1, result.size());
        assertEquals("TestUser", result.get(0).getName());
        verify(userRepository).searchUsers(query, offset, limit);
    }

    @Test
    void countSearchUsers_ShouldReturnCorrectCount() {
        String query = "test";

        when(userRepository.countSearchUsers(query)).thenReturn(3L);

        long count = userService.countSearchUsers(query);

        assertEquals(3L, count);
        verify(userRepository).countSearchUsers(query);
    }
}

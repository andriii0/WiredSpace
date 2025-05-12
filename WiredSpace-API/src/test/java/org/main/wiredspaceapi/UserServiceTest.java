package org.main.wiredspaceapi;

import jakarta.persistence.EntityNotFoundException;
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

import java.util.List;
import java.util.Optional;

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

    @BeforeEach
    void setUp() {
        user = new User("TestUser", "password123", UserRole.STANDARD_USER);
    }

    @Test
    void createUser_ShouldReturnSavedUser() {
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");
        when(userRepository.createUser("TestUser", "encodedPassword123", UserRole.STANDARD_USER))
                .thenReturn(user);

        User createdUser = userService.createUser("TestUser", "password123", UserRole.STANDARD_USER);

        assertNotNull(createdUser);
        assertEquals("TestUser", createdUser.getName());

        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).createUser("TestUser", "encodedPassword123", UserRole.STANDARD_USER);
    }

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        when(userRepository.getUserById(1L)).thenReturn(Optional.of(user));

        User foundUser = userService.getUserById(1L);

        assertNotNull(foundUser);
        assertEquals("TestUser", foundUser.getName());
        verify(userRepository, times(1)).getUserById(1L);
    }

    @Test
    void getUserById_ShouldThrowEntityNotFoundException_WhenUserNotExists() {
        when(userRepository.getUserById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.getUserById(1L));
        verify(userRepository, times(1)).getUserById(1L);
    }

    @Test
    void getAllUsers_ShouldReturnUserList() {
        when(userRepository.getAllUsers()).thenReturn(List.of(user));

        List<User> users = userService.getAllUsers();

        assertFalse(users.isEmpty());
        assertEquals(1, users.size());
        verify(userRepository, times(1)).getAllUsers();
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() {
        when(userRepository.updateUser(1L, "NewName", "newPass")).thenReturn(Optional.ofNullable(user));

        Optional<User> updatedUser = userService.updateUser(1L, "NewName", "newPass");

        assertNotNull(updatedUser);
        verify(userRepository, times(1)).updateUser(1L, "NewName", "newPass");
    }

    @Test
    void deleteUser_ShouldCallRepository() {
        doNothing().when(userRepository).deleteUser(1L);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteUser(1L);
    }
}
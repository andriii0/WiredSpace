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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("TestUser", "password123", UserRole.STANDARD_USER);
    }

    @Test
    void createUser_ShouldReturnSavedUser() {
        when(userRepository.createUser("TestUser", "password123", UserRole.STANDARD_USER)).thenReturn(user);

        User createdUser = userService.createUser("TestUser", "password123", UserRole.STANDARD_USER);

        assertNotNull(createdUser);
        assertEquals("TestUser", createdUser.getName());
        verify(userRepository, times(1)).createUser("TestUser", "password123", UserRole.STANDARD_USER);
    }

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        when(userRepository.getUserById(1L)).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.getUserById(1L);

        assertTrue(foundUser.isPresent());
        assertEquals("TestUser", foundUser.get().getName());
        verify(userRepository, times(1)).getUserById(1L);
    }

    @Test
    void getUserById_ShouldReturnEmpty_WhenUserNotExists() {
        when(userRepository.getUserById(1L)).thenReturn(Optional.empty());

        Optional<User> foundUser = userService.getUserById(1L);

        assertFalse(foundUser.isPresent());
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
        when(userRepository.updateUser(1L, "NewName", "newPass")).thenReturn(user);

        User updatedUser = userService.updateUser(1L, "NewName", "newPass");

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
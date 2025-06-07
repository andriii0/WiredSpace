package org.main.wiredspaceapi.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.main.wiredspaceapi.business.impl.AdminServiceImpl;
import org.main.wiredspaceapi.domain.Admin;
import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.domain.enums.AdminRole;
import org.main.wiredspaceapi.domain.enums.UserRole;
import org.main.wiredspaceapi.persistence.AdminRepository;
import org.main.wiredspaceapi.persistence.UserRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminServiceTest {

    private UserRepository userRepository;
    private AdminRepository adminRepository;
    private AdminServiceImpl adminService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        adminRepository = mock(AdminRepository.class);
        adminService = new AdminServiceImpl(userRepository, adminRepository);
    }

    @Test
    void createAdmin_shouldReturnAdmin() {
        String name = "Alice";
        String email = "alice@example.com";
        String password = "secure";
        AdminRole role = AdminRole.ADMIN;

        Admin admin = new Admin();
        admin.setId(UUID.randomUUID());
        admin.setName(name);
        admin.setEmail(email);
        admin.setPassword(password);
        admin.setRole(role);

        when(adminRepository.createAdmin(name, email, password, role)).thenReturn(admin);

        Admin result = adminService.createAdmin(name, email, password, role);

        assertNotNull(result);
        assertEquals(name, result.getName());
        assertEquals(email, result.getEmail());
        verify(adminRepository).createAdmin(name, email, password, role);
    }

    @Test
    void promoteUserToAdmin_shouldReturnAdmin_whenUserExists() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setName("Bob");
        user.setEmail("bob@example.com");
        user.setPassword("12345");

        Admin promoted = new Admin();
        promoted.setId(UUID.randomUUID());
        promoted.setName(user.getName());
        promoted.setEmail(user.getEmail());
        promoted.setPassword(user.getPassword());
        promoted.setRole(AdminRole.ADMIN);

        when(userRepository.getUserById(userId)).thenReturn(Optional.of(user));
        when(adminRepository.createAdmin(user.getName(), user.getEmail(), user.getPassword(), AdminRole.ADMIN)).thenReturn(promoted);

        Optional<Admin> result = adminService.promoteUserToAdmin(userId, AdminRole.ADMIN);

        assertTrue(result.isPresent());
        assertEquals(promoted.getEmail(), result.get().getEmail());

        verify(userRepository).deleteUser(userId);
        verify(adminRepository).createAdmin(user.getName(), user.getEmail(), user.getPassword(), AdminRole.ADMIN);
    }

    @Test
    void promoteUserToAdmin_shouldReturnEmpty_whenUserDoesNotExist() {
        UUID userId = UUID.randomUUID();

        when(userRepository.getUserById(userId)).thenReturn(Optional.empty());

        Optional<Admin> result = adminService.promoteUserToAdmin(userId, AdminRole.SUPPORT);

        assertTrue(result.isEmpty());
        verify(userRepository, never()).deleteUser(userId);
        verify(adminRepository, never()).createAdmin(any(), any(), any(), any());
    }


    @Test
    void demoteAdminToUser_shouldReturnUser_whenAdminExists() {
        UUID adminId = UUID.randomUUID();
        Admin admin = new Admin();
        admin.setId(adminId);
        admin.setName("Carol");
        admin.setEmail("carol@example.com");
        admin.setPassword("pass");

        User demoted = new User();
        demoted.setId(UUID.randomUUID());
        demoted.setName(admin.getName());
        demoted.setEmail(admin.getEmail());
        demoted.setPassword(admin.getPassword());

        when(adminRepository.getAdminById(adminId)).thenReturn(Optional.of(admin));
        when(userRepository.createUser(admin.getName(), admin.getEmail(), admin.getPassword(), UserRole.STANDARD_USER)).thenReturn(demoted);

        Optional<User> result = adminService.demoteAdminToUser(adminId, UserRole.STANDARD_USER);

        assertTrue(result.isPresent());
        assertEquals(demoted.getEmail(), result.get().getEmail());

        verify(adminRepository).deleteAdmin(adminId);
        verify(userRepository).createUser(admin.getName(), admin.getEmail(), admin.getPassword(), UserRole.STANDARD_USER);
    }

    @Test
    void demoteAdminToUser_shouldReturnEmpty_whenAdminNotFound() {
        UUID adminId = UUID.randomUUID();

        when(adminRepository.getAdminById(adminId)).thenReturn(Optional.empty());

        Optional<User> result = adminService.demoteAdminToUser(adminId, UserRole.STANDARD_USER);

        assertTrue(result.isEmpty());
        verify(adminRepository, never()).deleteAdmin(adminId);
        verify(userRepository, never()).createUser(any(), any(), any(), any());
    }

    @Test
    void findAdminByEmail_shouldReturnAdminIfExists() {
        String email = "admin@example.com";
        Admin admin = new Admin();
        admin.setEmail(email);

        when(adminRepository.findByEmail(email)).thenReturn(Optional.of(admin));

        Optional<Admin> result = adminService.findAdminByEmail(email);

        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());
        verify(adminRepository).findByEmail(email);
    }

    @Test
    void getUserById_shouldReturnUserIfExists() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);

        when(userRepository.getUserById(userId)).thenReturn(Optional.of(user));

        Optional<User> result = adminService.getUserById(userId);

        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getId());
        verify(userRepository).getUserById(userId);
    }

    @Test
    void getUserByEmail_shouldReturnUserIfExists() {
        String email = "user@example.com";
        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        Optional<User> result = adminService.getUserByEmail(email);

        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());
        verify(userRepository).findByEmail(email);
    }

    @Test
    void deleteUser_shouldReturnTrue_whenUserExists() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);

        when(userRepository.getUserById(userId)).thenReturn(Optional.of(user));

        boolean result = adminService.deleteUser(userId);

        assertTrue(result);
        verify(userRepository).deleteUser(userId);
    }

    @Test
    void deleteUser_shouldReturnFalse_whenUserNotFound() {
        UUID userId = UUID.randomUUID();

        when(userRepository.getUserById(userId)).thenReturn(Optional.empty());

        boolean result = adminService.deleteUser(userId);

        assertFalse(result);
        verify(userRepository, never()).deleteUser(userId);
    }
}

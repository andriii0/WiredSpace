package org.main.wiredspaceapi.unit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.main.wiredspaceapi.business.impl.AdminServiceImpl;
import org.main.wiredspaceapi.controller.exceptions.AdminNotFoundException;
import org.main.wiredspaceapi.controller.exceptions.UserNotFoundException;
import org.main.wiredspaceapi.controller.exceptions.UserPromotionException;
import org.main.wiredspaceapi.domain.Admin;
import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.domain.enums.AdminRole;
import org.main.wiredspaceapi.domain.enums.UserRole;
import org.main.wiredspaceapi.persistence.AdminRepository;
import org.main.wiredspaceapi.persistence.UserRepository;
import org.mockito.Incubating;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AdminRepository adminRepository;

    @InjectMocks
    private AdminServiceImpl adminService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void createAdmin_shouldReturnAdmin() {
        Admin admin = new Admin("Alice", "alice@example.com", "encodedPassword", AdminRole.ADMIN);
        admin.setId(UUID.randomUUID());

        when(passwordEncoder.encode("secure")).thenReturn("encodedPassword");

        when(adminRepository.createAdmin(any(), any(), eq("encodedPassword"), any())).thenReturn(admin);

        Admin result = adminService.createAdmin("Alice", "alice@example.com", "secure", AdminRole.ADMIN);

        assertEquals(admin.getEmail(), result.getEmail());
        verify(adminRepository).createAdmin("Alice", "alice@example.com", "encodedPassword", AdminRole.ADMIN);
    }


    @Test
    void promoteUserToAdmin_shouldPromote_whenUserExists() {
        UUID userId = UUID.randomUUID();
        User user = new User("Bob", "bob@example.com", "12345", UserRole.STANDARD_USER);
        user.setId(userId);

        Admin promoted = new Admin("Bob", "bob@example.com", "12345", AdminRole.ADMIN);
        promoted.setId(UUID.randomUUID());

        when(userRepository.getUserById(userId)).thenReturn(Optional.of(user));
        when(adminRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(adminRepository.createAdmin(any(), any(), any(), any())).thenReturn(promoted);

        Optional<Admin> result = adminService.promoteUserToAdmin(userId, AdminRole.ADMIN);

        assertTrue(result.isPresent());
        verify(userRepository).deleteUser(userId);
        verify(adminRepository).createAdmin(user.getName(), user.getEmail(), user.getPassword(), AdminRole.ADMIN);
    }

    @Test
    void promoteUserToAdmin_shouldThrow_whenUserNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.getUserById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> adminService.promoteUserToAdmin(userId, AdminRole.ADMIN));
    }

    @Test
    void promoteUserToAdmin_shouldThrow_whenUserIsAlreadyAdmin() {
        UUID userId = UUID.randomUUID();
        User user = new User("Bob", "bob@example.com", "12345", UserRole.STANDARD_USER);
        user.setId(userId);

        when(userRepository.getUserById(userId)).thenReturn(Optional.of(user));
        when(adminRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(new Admin()));

        assertThrows(UserPromotionException.class, () -> adminService.promoteUserToAdmin(userId, AdminRole.ADMIN));
    }

    @Test
    void promoteSupportToAdmin_shouldPromote_whenSupportExists() {
        UUID adminId = UUID.randomUUID();
        Admin support = new Admin("Support", "support@example.com", "pass", AdminRole.SUPPORT);
        support.setId(adminId);

        when(adminRepository.findAdminById(adminId)).thenReturn(Optional.of(support));
        when(adminRepository.updateAdmin(support)).thenReturn(support);

        Optional<Admin> result = adminService.promoteSupportToAdmin(adminId);

        assertTrue(result.isPresent());
        assertEquals(AdminRole.ADMIN, result.get().getRole());
        verify(adminRepository).updateAdmin(support);
    }

    @Test
    void promoteSupportToAdmin_shouldReturnEmpty_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(adminRepository.findAdminById(id)).thenReturn(Optional.empty());

        Optional<Admin> result = adminService.promoteSupportToAdmin(id);
        assertTrue(result.isEmpty());
    }

    @Test
    void promoteSupportToAdmin_shouldThrow_whenNotSupport() {
        UUID id = UUID.randomUUID();
        Admin admin = new Admin("Admin", "admin@example.com", "pass", AdminRole.ADMIN);
        admin.setId(id);

        when(adminRepository.findAdminById(id)).thenReturn(Optional.of(admin));

        assertThrows(IllegalArgumentException.class, () -> adminService.promoteSupportToAdmin(id));
    }

    @Test
    void demoteAdminToUser_shouldReturnUser_whenAdminExists() {
        UUID id = UUID.randomUUID();
        Admin admin = new Admin("Carol", "carol@example.com", "pass", AdminRole.ADMIN);
        admin.setId(id);

        User demoted = new User("Carol", "carol@example.com", "pass", UserRole.STANDARD_USER);
        demoted.setId(UUID.randomUUID());

        when(adminRepository.findAdminById(id)).thenReturn(Optional.of(admin));
        when(userRepository.createUser(
                eq(admin.getName()),
                eq(admin.getEmail()),
                eq(admin.getPassword()),
                eq(UserRole.STANDARD_USER),
                any(LocalDateTime.class)
        )).thenReturn(demoted);

        Optional<User> result = adminService.demoteAdminToUser(id, UserRole.STANDARD_USER);

        assertTrue(result.isPresent());
        verify(adminRepository).deleteAdmin(id);
        verify(userRepository).createUser(
                eq(admin.getName()),
                eq(admin.getEmail()),
                eq(admin.getPassword()),
                eq(UserRole.STANDARD_USER),
                any(LocalDateTime.class)
        );
    }

    @Test
    void demoteAdminToUser_shouldThrow_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(adminRepository.findAdminById(id)).thenReturn(Optional.empty());

        assertThrows(AdminNotFoundException.class, () -> adminService.demoteAdminToUser(id, UserRole.STANDARD_USER));
    }

    @Test
    void findAdminByEmail_shouldReturnAdmin() {
        String email = "admin@example.com";
        Admin admin = new Admin();
        admin.setEmail(email);

        when(adminRepository.findByEmail(email)).thenReturn(Optional.of(admin));

        Optional<Admin> result = adminService.findAdminByEmail(email);
        assertTrue(result.isPresent());
        verify(adminRepository).findByEmail(email);
    }

    @Test
    void getAdminById_shouldReturnAdmin() {
        UUID id = UUID.randomUUID();
        Admin admin = new Admin();
        admin.setId(id);

        when(adminRepository.findAdminById(id)).thenReturn(Optional.of(admin));

        Optional<Admin> result = adminService.getAdminById(id);
        assertTrue(result.isPresent());
    }

    @Test
    void getAllAdmins_shouldReturnList() {
        Admin a1 = new Admin(); Admin a2 = new Admin();
        when(adminRepository.getAllAdmins()).thenReturn(List.of(a1, a2));

        List<Admin> admins = adminService.getAllAdmins();

        assertEquals(2, admins.size());
        verify(adminRepository).getAllAdmins();
    }

    @Test
    void updateAdmin_shouldReturnUpdatedAdmin() {
        Admin admin = new Admin("X", "pass", "x@example.com", AdminRole.SUPPORT);
        when(adminRepository.updateAdmin(admin)).thenReturn(admin);

        Admin updated = adminService.updateAdmin(admin);

        assertEquals("x@example.com", updated.getEmail());
        verify(adminRepository).updateAdmin(admin);
    }

    @Test
    void deleteAdmin_shouldCallRepo() {
        UUID id = UUID.randomUUID();

        adminService.deleteAdmin(id);

        verify(adminRepository).deleteAdmin(id);
    }
}

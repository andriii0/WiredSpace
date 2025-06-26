package org.main.wiredspaceapi.business.impl;

import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.business.AdminService;
import org.main.wiredspaceapi.business.MessageService;
import org.main.wiredspaceapi.controller.exceptions.*;
import org.main.wiredspaceapi.domain.Admin;
import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.domain.enums.AdminRole;
import org.main.wiredspaceapi.domain.enums.UserRole;
import org.main.wiredspaceapi.persistence.AdminRepository;
import org.main.wiredspaceapi.persistence.UserRepository;
import org.main.wiredspaceapi.persistence.impl.message.MessageRepositoryImpl;
import org.main.wiredspaceapi.security.util.AuthenticatedUserProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticatedUserProvider authenticatedUserProvider;


    public Admin createAdmin(String name, String email, String password, AdminRole role) {
        String encodedPassword = passwordEncoder.encode(password);
        return adminRepository.createAdmin(name, email, encodedPassword, role);
    }

    public Optional<Admin> getAdminById(UUID id) {
        return adminRepository.findAdminById(id);
    }

    public List<Admin> getAllAdmins() {
        return adminRepository.getAllAdmins();
    }

    public Admin updateAdmin(Admin admin) {
        UUID currentId = authenticatedUserProvider.getCurrentUserId();

        if (!admin.getId().equals(currentId) && !authenticatedUserProvider.hasAdminRole()) {
            throw new UnauthorizedException("You can update only your own profile or be ADMIN.");
        }

        return adminRepository.updateAdmin(admin);
    }


    public void deleteAdmin(UUID id) {
        UUID currentId = authenticatedUserProvider.getCurrentUserId();

        if (id.equals(currentId)) {
            throw new SelfDemotionException("You cannot delete yourself.");
        }

        if (!authenticatedUserProvider.hasAdminRole()) {
            throw new UnauthorizedException("Only ADMIN can delete other admins.");
        }

        adminRepository.deleteAdmin(id);
    }

    public Optional<Admin> promoteUserToAdmin(UUID userId, AdminRole adminRole) {
        if (!authenticatedUserProvider.hasAdminRole()) {
            throw new UnauthorizedException("Only ADMIN can promote users.");
        }

        Optional<User> userOpt = userRepository.getUserById(userId);
        if (userOpt.isEmpty()) {
            throw new UserNotFoundException("User with ID " + userId + " not found.");
        }

        User user = userOpt.get();

        if (adminRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new UserPromotionException("User is already an admin.");
        }

        userRepository.deleteUser(userId);
        return Optional.of(adminRepository.createAdmin(
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                adminRole
        ));
    }
    @Override
    public Optional<Admin> promoteSupportToAdmin(UUID userId) {
        if (!authenticatedUserProvider.hasAdminRole()) {
            throw new UnauthorizedException("Only ADMIN can promote SUPPORT to ADMIN.");
        }

        Optional<Admin> supportOpt = adminRepository.findAdminById(userId);
        if (supportOpt.isEmpty()) return Optional.empty();

        Admin support = supportOpt.get();

        if (support.getRole() != AdminRole.SUPPORT) {
            throw new IllegalArgumentException("User is not SUPPORT");
        }

        support.setRole(AdminRole.ADMIN);
        return Optional.of(adminRepository.updateAdmin(support));
    }


    public Optional<User> demoteAdminToUser(UUID adminId, UserRole userRole) {
        Optional<Admin> adminOpt = adminRepository.findAdminById(adminId);
        if (adminOpt.isEmpty()) {
            throw new AdminNotFoundException("Admin with ID " + adminId + " not found.");
        }

        Admin admin = adminOpt.get();
        adminRepository.deleteAdmin(adminId);
        return Optional.of(userRepository.createUser(
                admin.getName(),
                admin.getEmail(),
                admin.getPassword(),
                userRole,
                LocalDateTime.now()
        ));
    }

    // Find Admin by Email
    public Optional<Admin> findAdminByEmail(String email) {
        return adminRepository.findByEmail(email);
    }
}

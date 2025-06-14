package org.main.wiredspaceapi.business.impl;

import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.business.AdminService;
import org.main.wiredspaceapi.controller.exceptions.*;
import org.main.wiredspaceapi.domain.Admin;
import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.domain.enums.AdminRole;
import org.main.wiredspaceapi.domain.enums.UserRole;
import org.main.wiredspaceapi.persistence.AdminRepository;
import org.main.wiredspaceapi.persistence.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    // CRUD Part
    public Admin createAdmin(String name, String email, String password, AdminRole role) {
        return adminRepository.createAdmin(name, email, password, role);
    }

    public Optional<Admin> promoteUserToAdmin(UUID userId, AdminRole adminRole) {
        Optional<User> userOpt = userRepository.getUserById(userId);
        if (userOpt.isEmpty()) {
            throw new UserNotFoundException("User with ID " + userId + " not found.");
        }

        User user = userOpt.get();

        // Optional: check if user is already admin
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

    public Optional<User> demoteAdminToUser(UUID adminId, UserRole userRole) {
        Optional<Admin> adminOpt = adminRepository.getAdminById(adminId);
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

    public Optional<Admin> findAdminByEmail(String email) {
        return adminRepository.findByEmail(email);
    }

    @Override
    public Optional<User> getUserById(UUID id) {
        return userRepository.getUserById(id)
                .or(() -> {
                    throw new UserNotFoundException("User with ID " + id + " not found.");
                });
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .or(() -> {
                    throw new UserNotFoundException("User with email " + email + " not found.");
                });
    }

    @Override
    public boolean deleteUser(UUID uuid) {
        Optional<User> user = userRepository.getUserById(uuid);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User with ID " + uuid + " not found.");
        }

        userRepository.deleteUser(uuid);
        return true;
    }
}

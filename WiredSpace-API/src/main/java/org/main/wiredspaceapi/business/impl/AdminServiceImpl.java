package org.main.wiredspaceapi.business.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.business.AdminService;
import org.main.wiredspaceapi.domain.Admin;
import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.domain.enums.AdminRole;
import org.main.wiredspaceapi.domain.enums.UserRole;
import org.main.wiredspaceapi.persistence.AdminRepository;
import org.main.wiredspaceapi.persistence.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    //CRUD Part
    public Admin createAdmin(String name, String email, String password, AdminRole role) {
        return adminRepository.createAdmin(name, email, password, role);
    }

    public Optional<Admin> promoteUserToAdmin(UUID userId, AdminRole adminRole) {
        Optional<User> userOpt = userRepository.getUserById(userId);
        if (userOpt.isEmpty()) return Optional.empty();

        User user = userOpt.get();
        userRepository.deleteUser(userId);
        return Optional.of(
                adminRepository.createAdmin(user.getName(), user.getEmail(), user.getPassword(), adminRole)
        );
    }

    public Optional<User> demoteAdminToUser(UUID adminId, UserRole userRole) {
        Optional<Admin> adminOpt = adminRepository.getAdminById(adminId);
        if (adminOpt.isEmpty()) return Optional.empty();

        Admin admin = adminOpt.get();
        adminRepository.deleteAdmin(adminId);
        return Optional.of(
                userRepository.createUser(admin.getName(), admin.getEmail(), admin.getPassword(), userRole)
        );
    }

    public Optional<Admin> findAdminByEmail(String email){
        return adminRepository.findByEmail(email);
    }

    //Service Part
    @Override
    public Optional<User> getUserById(UUID id) {
        return userRepository.getUserById(id);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean deleteUser(UUID uuid) {
        Optional<User> user = userRepository.getUserById(uuid);
        if (user.isEmpty()) return false;

        userRepository.deleteUser(uuid);
        return true;
    }
}

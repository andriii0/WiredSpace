package org.main.wiredspaceapi.business;

import org.main.wiredspaceapi.domain.Admin;
import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.domain.enums.AdminRole;
import org.main.wiredspaceapi.domain.enums.UserRole;

import java.util.Optional;
import java.util.UUID;

public interface AdminService {
    //Admin CRUD

    Admin createAdmin(String name, String email, String password, AdminRole role);
    Optional<Admin> promoteUserToAdmin(UUID userId, AdminRole adminRole);
    Optional<User> demoteAdminToUser(UUID adminId, UserRole userRole);
    Optional<Admin> findAdminByEmail(String email);

    //Service Part
    Optional<User> getUserById(UUID id);
    Optional<User> getUserByEmail(String email);
    Optional<User> updateUser(UUID id, String name, String email);
    Optional<User> updateUser(UUID id, String name, String email, UserRole userRole);
    boolean deleteUser(UUID uuid);
}

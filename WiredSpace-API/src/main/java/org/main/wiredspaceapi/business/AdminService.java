package org.main.wiredspaceapi.business;

import org.main.wiredspaceapi.domain.Admin;
import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.domain.enums.AdminRole;
import org.main.wiredspaceapi.domain.enums.UserRole;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface AdminService {

    Admin createAdmin(String name, String email, String password, AdminRole role);
    Admin updateAdmin(Admin admin);
    void deleteAdmin(UUID id);
    Optional<Admin> getAdminById(UUID id);
    List<Admin> getAllAdmins();
    Optional<Admin> findAdminByEmail(String email);

    Optional<Admin> promoteUserToAdmin(UUID userId, AdminRole adminRole);
    Optional<User> demoteAdminToUser(UUID adminId, UserRole userRole);
}

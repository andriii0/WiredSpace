package org.main.wiredspaceapi.persistence;

import org.main.wiredspaceapi.domain.Admin;
import org.main.wiredspaceapi.domain.enums.AdminRole;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AdminRepository {
    Admin createAdmin(String name, String email, String password, AdminRole role);
    Optional<Admin> findAdminById(UUID id);
    List<Admin> getAllAdmins();
    Optional<Admin> findByEmail(String email);
    Admin updateAdmin(Admin admin);
    void deleteAdmin(UUID id);
}

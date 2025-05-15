package org.main.wiredspaceapi.persistence;

import org.main.wiredspaceapi.domain.Admin;
import org.main.wiredspaceapi.domain.enums.AdminRole;

import java.util.Optional;
import java.util.UUID;

public interface AdminRepository {
    Admin createAdmin(String name, String email, String password, AdminRole role);
    Optional<Admin> getAdminById(UUID id);
    Optional<Admin> findByEmail(String email);
    boolean deleteAdmin(UUID id);
}

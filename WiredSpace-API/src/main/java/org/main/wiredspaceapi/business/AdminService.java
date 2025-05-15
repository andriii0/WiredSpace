package org.main.wiredspaceapi.business;

import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.domain.enums.UserRole;

import java.util.Optional;
import java.util.UUID;

public interface AdminService {
    Optional<User> getUserById(UUID id);
    Optional<User> getUserByEmail(String email);
    Optional<User> updateUser(UUID id, String name, String email);
    Optional<User> updateUser(UUID id, String name, String email, UserRole userRole);
    boolean deleteUser(UUID uuid);
}

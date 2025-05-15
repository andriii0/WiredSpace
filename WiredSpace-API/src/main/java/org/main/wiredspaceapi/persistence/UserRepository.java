package org.main.wiredspaceapi.persistence;

import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.domain.enums.UserRole;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User createUser(String name, String email, String password, UserRole userRole);
    Optional<User> getUserById(UUID id);
    List<User> getAllUsers();
    Optional<User> findByEmail(String email);
    Optional<User> updateUser(UUID id, String name, String email, String password);
    Optional<User> updateUser(UUID id, String name, String email, String password, UserRole userRole);
    void deleteUser(UUID id);
}

package org.main.wiredspaceapi.business;

import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.domain.enums.UserRole;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    User createUser(String name, String email, String password);
    User createUser(String name, String email, String password, UserRole userRole);
    User getUserById(UUID id);
    User findUserByEmail(String email);
    List<User> getAllUsers();
    Optional<User> updateUser(UUID id, String name, String email, String password);
    Optional<User> updateUser(UUID id, String name, String email, String password, UserRole userRole);
    void deleteUser(UUID id);
}

package org.main.wiredspaceapi.business;

import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.domain.enums.UserRole;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    User createUser(String name, String email, String password, UserRole userRole);
    Optional<User>  getUserById(UUID id);
    Optional<User> findByEmail(String email);
    List<User> getAllUsers();
    Optional<User> updateUserById(UUID userId, String newName, String newEmail, String newPassword);
    void deleteUserByEmail(String email); //TODO boolean not void

    List<User> searchUsers(String query, int offset, int limit);
    long countSearchUsers(String query);

}

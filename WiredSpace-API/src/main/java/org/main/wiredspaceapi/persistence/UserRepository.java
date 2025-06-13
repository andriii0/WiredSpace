package org.main.wiredspaceapi.persistence;

import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.domain.enums.UserRole;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User createUser(String name, String email, String password, UserRole userRole, LocalDateTime registerTime);
    Optional<User> getUserById(UUID id);
    List<User> getAllUsers();
    Optional<User> findByEmail(String email);
    Optional<User> updateUser(UUID id, String name, String email, String password);
    void deleteUser(UUID id);
    void updateStatistics(User user);

    //pagination
    List<User> searchUsers(String query, int offset, int limit);
    long countSearchUsers(String query);
}

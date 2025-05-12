package org.main.wiredspaceapi.business;

import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.domain.enums.UserRole;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(String name, String password, UserRole userRole);
    User getUserById(Long id);
    User findUserByName(String name);
    List<User> getAllUsers();
    Optional<User> updateUser(Long id, String name, String password);
    Optional<User> updateUser(Long id, String name, String password, UserRole userRole);
    void deleteUser(Long id);
}

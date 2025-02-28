package org.main.wiredspaceapi.business;

import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.domain.enums.UserRole;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(String name, String password);
    User createUser(String name, String password, UserRole userRole);
    Optional<User> getUserById(Long id);
    List<User> getAllUsers();
    User updateUser(Long id, String name, String password);
    User updateUser(Long id, String name, String password, UserRole userRole);
    void deleteUser(Long id);
}

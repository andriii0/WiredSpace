package org.main.wiredspaceapi.business;

import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.domain.enums.UserRole;

import java.util.List;

public interface UserService {
    User createUser(String name, String password, UserRole userRole);
    User getUserById(Long id);
    List<User> getAllUsers();
    User updateUser(Long id, String name, String password);
    User updateUser(Long id, String name, String password, UserRole userRole);
    void deleteUser(Long id);
}

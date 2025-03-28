package org.main.wiredspaceapi.persistence.impl;

import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.domain.enums.UserRole;
import org.main.wiredspaceapi.persistence.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserDB userDB;

    @Override
    public User createUser(String name, String password) {
        return userDB.save(new User(name, password, UserRole.STANDARD_USER));
    }

    @Override
    public User createUser(String name, String password, UserRole userRole) {
        return userDB.save(new User(name, password, userRole));
    }
    @Override
    public Optional<User> findByName(String name) {
        return userDB.findByName(name);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userDB.findById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userDB.findAll();
    }

    @Override
    public User updateUser(Long id, String name, String password) {
        return updateUser(id, name, password, null);
    }

    @Override
    public User updateUser(Long id, String name, String password, UserRole userRole) {
        return userDB.findById(id)
                .map(user -> {
                    user.setName(name);
                    user.setPassword(password);
                    if (userRole != null) {
                        user.setRole(userRole);
                    }
                    return userDB.save(user);
                })
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public void deleteUser(Long id) {
        userDB.deleteById(id);
    }
}

package org.main.wiredspaceapi.business.impl;

import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.business.UserService;
import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.domain.enums.UserRole;
import org.main.wiredspaceapi.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User createUser(String name, String password) {
        User user = new User(name, password, UserRole.STANDARD_USER); //standart role
        return userRepository.save(user);
    }
    @Override
    public User createUser(String name, String password, UserRole userRole) {
        User user = new User(name, password, userRole);
        return userRepository.save(user);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(Long id, String name, String password) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setName(name);
                    user.setPassword(password);
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    @Override
    public User updateUser(Long id, String name, String password, UserRole userRole) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setName(name);
                    user.setPassword(password);
                    user.setRole(userRole);
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
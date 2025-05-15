package org.main.wiredspaceapi.business.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.business.UserService;
import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.domain.enums.UserRole;
import org.main.wiredspaceapi.persistence.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User createUser(String name, String email, String password) {
        String encodedPassword = passwordEncoder.encode(password);
        return userRepository.createUser(name, email, encodedPassword, UserRole.STANDARD_USER);
    }

    @Override
    public User createUser(String name, String email, String password, UserRole userRole) {
        String encodedPassword = passwordEncoder.encode(password);
        return userRepository.createUser(name, email, encodedPassword, userRole);
    }

    @Override
    public User getUserById(UUID id) {
        return userRepository.getUserById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    @Override
    public Optional<User> updateUser(UUID id, String name, String email, String password) {
        String encodedPassword = passwordEncoder.encode(password);
        return userRepository.updateUser(id, name, email, encodedPassword);
    }

    @Override
    public Optional<User> updateUser(UUID id, String name, String email, String password, UserRole userRole) {
        String encodedPassword = passwordEncoder.encode(password);
        return userRepository.updateUser(id, name, email, encodedPassword, userRole);
    }

    @Override
    public void deleteUser(UUID id) {
        userRepository.deleteUser(id);
    }
}

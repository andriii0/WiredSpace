package org.main.wiredspaceapi.business.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.business.AdminService;
import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.domain.enums.UserRole;
import org.main.wiredspaceapi.persistence.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;

    @Override
    public Optional<User> getUserById(UUID id) {
        return userRepository.getUserById(id);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> updateUser(UUID id, String name, String email) {
        Optional<User> existingUserOpt = userRepository.getUserById(id);
        if (existingUserOpt.isEmpty()) return Optional.empty();

        User existingUser = existingUserOpt.get();
        return userRepository.updateUser(id, name, email, existingUser.getPassword());
    }

    @Override
    public Optional<User> updateUser(UUID id, String name, String email, UserRole userRole) {
        Optional<User> existingUserOpt = userRepository.getUserById(id);
        if (existingUserOpt.isEmpty()) return Optional.empty();

        User existingUser = existingUserOpt.get();
        return userRepository.updateUser(id, name, email, existingUser.getPassword(), userRole);
    }

    @Override
    public boolean deleteUser(UUID uuid) {
        Optional<User> user = userRepository.getUserById(uuid);
        if (user.isEmpty()) return false;

        userRepository.deleteUser(uuid);
        return true;
    }
}

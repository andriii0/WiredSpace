package org.main.wiredspaceapi.business.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.business.UserService;
import org.main.wiredspaceapi.controller.exceptions.AccountAlreadyExistsException;
import org.main.wiredspaceapi.controller.exceptions.UserNotFoundException;
import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.domain.enums.UserRole;
import org.main.wiredspaceapi.persistence.UserRepository;
import org.main.wiredspaceapi.security.util.AuthenticatedUserProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticatedUserProvider userProvider;
    private final MessageServiceImpl messageService;
    private final UserDeletionService userDeletionService;

    @Override
    public User createUser(String name, String email, String password, UserRole userRole) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            throw new AccountAlreadyExistsException("Email " + email + " is already in use");
        }
        String encodedPassword = passwordEncoder.encode(password);
        return userRepository.createUser(name, email, encodedPassword, userRole, LocalDateTime.now());
    }


    @Override
    public Optional<User> getUserById(UUID id) {
        return userRepository.getUserById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    @Override
    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    @Override
    public Optional<User> updateUserById(UUID userId, String newName, String newEmail, String newPassword) {
        User user = userRepository.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found"));

        String nameToSet = (newName != null && !newName.trim().isEmpty()) ? newName : user.getName();
        String emailToSet = (newEmail != null && !newEmail.trim().isEmpty()) ? newEmail : user.getEmail();
        String passwordToSet = (newPassword != null && !newPassword.trim().isEmpty())
                ? passwordEncoder.encode(newPassword) : user.getPassword();

        return userRepository.updateUser(user.getId(), nameToSet, emailToSet, passwordToSet);
    }

    public void deleteUser(UUID id) {
        userProvider.validateCurrentUserAccess(id);

        userDeletionService.deleteUserCompletely(id);
    }

    @Override
    public void deleteUserByEmail(String targetEmail) {
        User user = findByEmail(targetEmail)
                .orElseThrow(() -> new UserNotFoundException("User with email " + targetEmail + " not found"));

        userProvider.validateCurrentUserAccess(targetEmail);
        userDeletionService.deleteUserCompletely(user.getId());
    }

    @Override
    public List<User> searchUsers(String query, int offset, int limit) {
        UUID currentUserId = userProvider.getCurrentUserId();
        return userRepository.searchUsers(query, offset, limit, currentUserId);
    }


    @Override
    public long countSearchUsers(String query) {
        return userRepository.countSearchUsers(query);
    }
}

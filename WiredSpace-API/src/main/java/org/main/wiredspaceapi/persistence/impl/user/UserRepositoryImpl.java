package org.main.wiredspaceapi.persistence.impl.user;

import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.domain.enums.UserRole;
import org.main.wiredspaceapi.persistence.UserRepository;
import org.main.wiredspaceapi.persistence.entity.UserEntity;
import org.main.wiredspaceapi.persistence.mapper.UserEntityMapper;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserDB userDB;
    private final UserEntityMapper accountMapper;

    @Override
    public User createUser(String name, String email, String password, UserRole userRole, LocalDateTime registerTime) {
        User user = User.builder()
                .name(name)
                .email(email)
                .password(password)
                .role(userRole)
                .registeredAt(LocalDateTime.now())
                .friendsCount(0)
                .commentsCount(0)
                .likesGiven(0)
                .build();
        UserEntity entity = accountMapper.toEntity(user);
        UserEntity saved = userDB.save(entity);
        return accountMapper.toDomain(saved);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userDB.findByEmail(email)
                .map(accountMapper::toDomain);
    }

    @Override
    public Optional<User> getUserById(UUID id) {
        return userDB.findById(id)
                .map(accountMapper::toDomain);
    }

    @Override
    public List<User> getAllUsers() {
        return userDB.findAll().stream()
                .map(accountMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<User> updateUser(UUID id, String name, String email, String password) {
        return userDB.findById(id).map(entity -> {
            entity.setName(name);
            entity.setEmail(email);
            entity.setPassword(password);
            UserEntity updated = userDB.save(entity);
            return accountMapper.toDomain(updated);
        });
    }

    @Override
    public void deleteUser(UUID id) {
        userDB.findById(id).ifPresent(user -> {
            userDB.delete(user);
        });
    }

    @Override
    public void updateStatistics(User user) {
        userDB.findById(user.getId()).ifPresent(entity -> {
            entity.setFriendsCount(user.getFriendsCount());
            entity.setCommentsCount(user.getCommentsCount());

            userDB.save(entity);
        });
    }

    @Override
    public List<User> searchUsers(String query, int offset, int limit) {
        Pageable pageable = PageRequest.of(offset / limit, limit); // page = offset / limit
        List<UserEntity> entities = userDB.searchByNameOrEmail(query, pageable);
        return entities.stream()
                .map(accountMapper::toDomain)
                .toList();
    }

    @Override
    public long countSearchUsers(String query) {
        return userDB.countByNameOrEmail(query);
    }
}

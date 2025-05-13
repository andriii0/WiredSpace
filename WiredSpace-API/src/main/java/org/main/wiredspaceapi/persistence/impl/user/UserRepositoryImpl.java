package org.main.wiredspaceapi.persistence.impl.user;

import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.domain.enums.UserRole;
import org.main.wiredspaceapi.persistence.UserRepository;
import org.main.wiredspaceapi.persistence.entity.UserEntity;
import org.main.wiredspaceapi.persistence.mapper.AccountEntityMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserDB userDB;
    private final AccountEntityMapper accountMapper;

    @Override
    public User createUser(String name, String email, String password, UserRole userRole) {
        User user = new User(name, email, password, userRole);
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
    public Optional<User> getUserById(Long id) {
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
    public Optional<User> updateUser(Long id, String name, String email, String password) {
        return updateUser(id, name, email, password, null);
    }

    @Override
    public Optional<User> updateUser(Long id, String name, String email, String password, UserRole userRole) {
        return userDB.findById(id).map(entity -> {
            entity.setName(name);
            entity.setEmail(email);
            entity.setPassword(password);
            if (userRole != null) {
                entity.setRole(userRole);
            }
            UserEntity updated = userDB.save(entity);
            return accountMapper.toDomain(updated);
        });
    }

    @Override
    public void deleteUser(Long id) {
        userDB.deleteById(id);
    }
}

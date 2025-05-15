package org.main.wiredspaceapi.persistence.impl.user;

import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserDB extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmail(String email);
}

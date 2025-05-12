package org.main.wiredspaceapi.persistence.impl.user;

import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserDB extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByName(String name);
}

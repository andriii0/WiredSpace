package org.main.wiredspaceapi.persistence.impl.user;

import org.main.wiredspaceapi.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserDB extends JpaRepository<User, Long> {
    Optional<User> findByName(String name);
}

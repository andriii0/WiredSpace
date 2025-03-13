package org.main.wiredspaceapi.persistence.impl;

import org.main.wiredspaceapi.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDB extends JpaRepository<User, Long> {
}

package org.main.wiredspaceapi.persistence.impl.admin;


import org.main.wiredspaceapi.persistence.entity.AdminEntity;
import org.main.wiredspaceapi.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AdminDB extends JpaRepository<AdminEntity, UUID> {
    Optional<AdminEntity> findByEmail(String email);
}

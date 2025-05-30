package org.main.wiredspaceapi.persistence.impl.message;

import org.main.wiredspaceapi.persistence.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MessageDB extends JpaRepository<MessageEntity, UUID> {
    List<MessageEntity> findByFromUserOrToUser(String fromUser, String toUser);
}

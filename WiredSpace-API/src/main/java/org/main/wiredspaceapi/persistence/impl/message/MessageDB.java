package org.main.wiredspaceapi.persistence.impl.message;

import org.main.wiredspaceapi.persistence.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MessageDB extends JpaRepository<MessageEntity, UUID> {
    @Query("SELECT m FROM MessageEntity m WHERE " +
            "(LOWER(m.sender) = LOWER(:user1) AND LOWER(m.recipient) = LOWER(:user2)) " +
            "OR (LOWER(m.sender) = LOWER(:user2) AND LOWER(m.recipient) = LOWER(:user1))")
    List<MessageEntity> findConversation(@Param("user1") String user1, @Param("user2") String user2);}

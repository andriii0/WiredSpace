package org.main.wiredspaceapi.persistence.impl.message;

import org.main.wiredspaceapi.persistence.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageDB extends JpaRepository<MessageEntity, UUID> {
    //sender and recipient = emails not user entity
    @Query("SELECT m FROM MessageEntity m WHERE " +
            "(LOWER(m.sender) = LOWER(:user1) AND LOWER(m.recipient) = LOWER(:user2)) " +
            "OR (LOWER(m.sender) = LOWER(:user2) AND LOWER(m.recipient) = LOWER(:user1))")
    List<MessageEntity> findConversation(@Param("user1") String user1, @Param("user2") String user2);
    @Query(value = """
    SELECT
        CASE
            WHEN LOWER(sender) = LOWER(:userEmail) THEN LOWER(recipient)
            ELSE LOWER(sender)
        END AS chat_partner,
        MAX(timestamp) AS latest_time
    FROM messages
    WHERE LOWER(sender) = LOWER(:userEmail) OR LOWER(recipient) = LOWER(:userEmail)
    GROUP BY chat_partner
    """, nativeQuery = true)
    List<Object[]> findChatPartnersWithLatestTimestamp(@Param("userEmail") String userEmail);
    @Query("""
    SELECT m FROM MessageEntity m 
    WHERE 
        (
            (LOWER(m.sender) = LOWER(:userEmail) AND LOWER(m.recipient) = LOWER(:partnerEmail)) 
            OR 
            (LOWER(m.sender) = LOWER(:partnerEmail) AND LOWER(m.recipient) = LOWER(:userEmail))
        )
        AND m.timestamp = :timestamp
    """)
    Optional<MessageEntity> findByParticipantsAndTimestamp(
            @Param("userEmail") String userEmail,
            @Param("partnerEmail") String partnerEmail,
            @Param("timestamp") LocalDateTime timestamp
    );
    @Modifying
    @Query("""
    DELETE FROM MessageEntity m
    WHERE LOWER(m.sender) = LOWER(:email)
       OR LOWER(m.recipient) = LOWER(:email)
    """)
    void deleteAllConversationsByEmail(@Param("email") String email);
}


package org.main.wiredspaceapi.persistence.impl.message;

import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.domain.Message;
import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.persistence.MessageRepository;
import org.main.wiredspaceapi.persistence.UserRepository;
import org.main.wiredspaceapi.persistence.entity.MessageEntity;
import org.main.wiredspaceapi.persistence.mapper.MessageEntityMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MessageRepositoryImpl implements MessageRepository {

    private final MessageDB messageDB;
    private final MessageEntityMapper mapper;
    private final UserRepository userRepository;

    @Override
    public Map<User, Message> getLatestMessagesPerChat(String userEmail) {
        List<Object[]> partnerRows = messageDB.findChatPartnersWithLatestTimestamp(userEmail);

        Map<User, Message> result = new HashMap<>();

        for (Object[] row : partnerRows) {
            String partnerEmail = (String) row[0];
            LocalDateTime latestTime = ((Timestamp) row[1]).toLocalDateTime();

            Optional<MessageEntity> messageOpt = messageDB.findByParticipantsAndTimestamp(userEmail, partnerEmail, latestTime);
            if (messageOpt.isEmpty()) continue;

            MessageEntity msg = messageOpt.get();

            Optional<User> userOpt = userRepository.findByEmail(partnerEmail);
            if (userOpt.isEmpty()) continue;

            result.put(userOpt.get(), mapper.toDomain(msg));
        }

        return result;
    }
    @Override
    public Message saveMessage(Message message) {
        var entity = mapper.toEntity(message);
        var saved = messageDB.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<Message> getMessagesBetween(String user1, String user2) {
        return messageDB
                .findConversation(user1, user2)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteAllConversationsForUser(String email) {
        messageDB.deleteAllConversationsByEmail(email);
    }

}

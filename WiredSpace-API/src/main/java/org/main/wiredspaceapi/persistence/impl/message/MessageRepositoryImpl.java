package org.main.wiredspaceapi.persistence.impl.message;

import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.domain.Message;
import org.main.wiredspaceapi.persistence.MessageRepository;
import org.main.wiredspaceapi.persistence.mapper.MessageEntityMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MessageRepositoryImpl implements MessageRepository {

    private final MessageDB messageDB;
    private final MessageEntityMapper mapper;

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
}

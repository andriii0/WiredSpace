package org.main.wiredspaceapi.business.impl;

import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.business.MessageService;
import org.main.wiredspaceapi.domain.Message;
import org.main.wiredspaceapi.persistence.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;

    @Override
    public void saveMessage(Message message) {
        messageRepository.saveMessage(message);
    }

    @Override
    public List<Message> getMessagesBetween(String user1, String user2) {
        return messageRepository.getMessagesBetween(user1, user2);
    }
}

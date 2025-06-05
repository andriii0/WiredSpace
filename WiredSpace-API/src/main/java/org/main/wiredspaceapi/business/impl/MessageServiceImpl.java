package org.main.wiredspaceapi.business.impl;

import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.business.MessageService;
import org.main.wiredspaceapi.controller.dto.message.MessageDTO;
import org.main.wiredspaceapi.domain.Message;
import org.main.wiredspaceapi.persistence.MessageRepository;
import org.springframework.stereotype.Service;
import org.main.wiredspaceapi.controller.mapper.MessageMapper;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public MessageDTO sendPrivateMessage(String sender, MessageDTO messageDTO) {
        String recipient = messageDTO.getTo();

        Message message = messageMapper.toDomain(messageDTO);
        message.setFromUser(sender);
        message.setTimestamp(LocalDateTime.now());

        Message saved = messageRepository.saveMessage(message);

        MessageDTO savedDTO = messageMapper.toDTO(saved);
        savedDTO.setFrom(sender);

        messagingTemplate.convertAndSendToUser(recipient, "/queue/messages", savedDTO);
        messagingTemplate.convertAndSendToUser(sender, "/queue/messages", savedDTO);

        return savedDTO;
    }


    @Override
    public List<Message> getMessagesBetween(String user1, String user2) {
        return messageRepository.getMessagesBetween(user1, user2);
    }
}

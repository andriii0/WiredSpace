package org.main.wiredspaceapi.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.main.wiredspaceapi.business.impl.MessageServiceImpl;
import org.main.wiredspaceapi.controller.dto.message.MessageDTO;
import org.main.wiredspaceapi.controller.mapper.MessageMapper;
import org.main.wiredspaceapi.domain.Message;
import org.main.wiredspaceapi.persistence.MessageRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private MessageMapper messageMapper;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private MessageServiceImpl messageService;

    private MessageDTO messageDTO;
    private Message message;

    @BeforeEach
    void setUp() {
        messageDTO = new MessageDTO();
        messageDTO.setTo("userB");
        messageDTO.setText("Hello");

        message = new Message();
        message.setFromUser("userA");
        message.setToUser("userB");
        message.setText("Hello");
        message.setTimestamp(LocalDateTime.now());
    }

    @Test
    void sendPrivateMessage_ShouldSendAndReturnMessageDTO() {
        when(messageMapper.toDomain(messageDTO)).thenReturn(message);
        when(messageRepository.saveMessage(any())).thenReturn(message);
        when(messageMapper.toDTO(any())).thenReturn(messageDTO);

        MessageDTO result = messageService.sendPrivateMessage("userA", messageDTO);

        assertNotNull(result);
        assertEquals("userA", result.getFrom());
        verify(messagingTemplate, times(1)).convertAndSendToUser(eq("userB"), eq("/queue/messages"), eq(result));
        verify(messagingTemplate, times(1)).convertAndSendToUser(eq("userA"), eq("/queue/messages"), eq(result));
    }

    @Test
    void getMessagesBetween_ShouldReturnMessageList() {
        Message m1 = new Message();
        m1.setFromUser("userA");
        m1.setToUser("userB");
        m1.setText("Hi");

        when(messageRepository.getMessagesBetween("userA", "userB")).thenReturn(List.of(m1));

        List<Message> result = messageService.getMessagesBetween("userA", "userB");

        assertEquals(1, result.size());
        assertEquals("Hi", result.get(0).getText());
    }
}

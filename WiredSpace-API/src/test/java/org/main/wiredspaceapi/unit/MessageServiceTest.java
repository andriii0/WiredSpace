package org.main.wiredspaceapi.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.main.wiredspaceapi.business.impl.MessageServiceImpl;
import org.main.wiredspaceapi.controller.dto.message.MessageDTO;
import org.main.wiredspaceapi.controller.exceptions.UnauthorizedException;
import org.main.wiredspaceapi.controller.mapper.MessageMapper;
import org.main.wiredspaceapi.domain.Message;
import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.persistence.MessageRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock private MessageRepository messageRepository;
    @Mock private MessageMapper messageMapper;
    @Mock private SimpMessagingTemplate messagingTemplate;

    @InjectMocks private MessageServiceImpl messageService;

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
        when(messageMapper.toDomain(messageDTO)).thenReturn(new Message());
        when(messageRepository.saveMessage(any())).thenReturn(message);
        when(messageMapper.toDTO(any())).thenReturn(messageDTO);

        MessageDTO result = messageService.sendPrivateMessage("userA", messageDTO);

        assertNotNull(result);
        assertEquals("userA", result.getFrom());
        verify(messagingTemplate).convertAndSendToUser(eq("userB"), eq("/queue/messages"), eq(result));
        verify(messagingTemplate).convertAndSendToUser(eq("userA"), eq("/queue/messages"), eq(result));
    }

    @Test
    void sendPrivateMessage_ShouldThrow_WhenSendingToSelf() {
        messageDTO.setTo("userA");

        assertThrows(UnauthorizedException.class, () ->
                messageService.sendPrivateMessage("userA", messageDTO)
        );

        verify(messageRepository, never()).saveMessage(any());
        verify(messagingTemplate, never()).convertAndSendToUser(any(), any(), any());
    }

    @Test
    void sendPrivateMessage_ShouldSetSenderAndTimestamp() {
        Message emptyMsg = new Message();
        when(messageMapper.toDomain(messageDTO)).thenReturn(emptyMsg);
        when(messageRepository.saveMessage(any())).thenReturn(message);
        when(messageMapper.toDTO(any())).thenReturn(messageDTO);

        messageService.sendPrivateMessage("userA", messageDTO);

        assertEquals("userA", emptyMsg.getFromUser());
        assertNotNull(emptyMsg.getTimestamp());
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

    @Test
    void getMessagesBetween_ShouldReturnEmptyList_WhenNoMessages() {
        when(messageRepository.getMessagesBetween("userA", "userB")).thenReturn(Collections.emptyList());

        List<Message> result = messageService.getMessagesBetween("userA", "userB");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getLatestMessagesPerChat_ShouldReturnSortedMessages() {
        User user1 = new User();
        user1.setEmail("user1@example.com");
        User user2 = new User();
        user2.setEmail("user2@example.com");

        Message msg1 = new Message();
        msg1.setText("First");
        msg1.setTimestamp(LocalDateTime.now().minusMinutes(10));

        Message msg2 = new Message();
        msg2.setText("Second");
        msg2.setTimestamp(LocalDateTime.now());

        Map<User, Message> unsortedMap = new HashMap<>();
        unsortedMap.put(user1, msg1);
        unsortedMap.put(user2, msg2);

        when(messageRepository.getLatestMessagesPerChat("userA")).thenReturn(unsortedMap);

        Map<User, Message> result = messageService.getLatestMessagesPerChat("userA");

        assertEquals(2, result.size());
        List<Message> values = new ArrayList<>(result.values());
        assertEquals("Second", values.get(0).getText());
        assertEquals("First", values.get(1).getText());
    }

    @Test
    void getLatestMessagesPerChat_ShouldReturnEmptyMap_WhenNoMessages() {
        when(messageRepository.getLatestMessagesPerChat("userA")).thenReturn(Collections.emptyMap());

        Map<User, Message> result = messageService.getLatestMessagesPerChat("userA");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}

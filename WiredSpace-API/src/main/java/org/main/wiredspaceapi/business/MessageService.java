package org.main.wiredspaceapi.business;


import org.main.wiredspaceapi.controller.dto.message.MessageDTO;
import org.main.wiredspaceapi.domain.Message;
import org.main.wiredspaceapi.domain.User;

import java.util.List;
import java.util.Map;

public interface MessageService {
    Map<User, Message> getLatestMessagesPerChat(String userEmail);
    MessageDTO sendPrivateMessage(String sender, MessageDTO messageDTO);
    List<Message> getMessagesBetween(String user1, String user2);
}
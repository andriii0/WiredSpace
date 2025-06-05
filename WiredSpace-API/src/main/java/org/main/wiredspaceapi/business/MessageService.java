package org.main.wiredspaceapi.business;


import org.main.wiredspaceapi.controller.dto.message.MessageDTO;
import org.main.wiredspaceapi.domain.Message;

import java.util.List;

public interface MessageService {
    MessageDTO sendPrivateMessage(String sender, MessageDTO messageDTO);
    List<Message> getMessagesBetween(String user1, String user2);
}
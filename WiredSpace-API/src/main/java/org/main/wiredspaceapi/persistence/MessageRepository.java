package org.main.wiredspaceapi.persistence;

import org.main.wiredspaceapi.domain.Message;

import java.util.List;
import java.util.UUID;

public interface MessageRepository {
    Message saveMessage(Message message);
    List<Message> getMessagesBetween(String user1, String user2);
}

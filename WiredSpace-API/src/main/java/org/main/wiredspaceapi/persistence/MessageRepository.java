package org.main.wiredspaceapi.persistence;

import org.main.wiredspaceapi.domain.Message;
import org.main.wiredspaceapi.domain.User;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface MessageRepository {
    Map<User, Message> getLatestMessagesPerChat(String userEmail);
    Message saveMessage(Message message);
    List<Message> getMessagesBetween(String user1, String user2);
    void deleteAllConversationsForUser(String email);
}

package org.main.wiredspaceapi.business;


import org.main.wiredspaceapi.domain.Message;

import java.util.List;

public interface MessageService {
    void saveMessage(Message message);
    List<Message> getMessagesBetween(String user1, String user2);
}
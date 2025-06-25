package org.main.wiredspaceapi.business;

import java.util.Map;

public interface EmailService {
    void sendEmail(String to, String subject, String templateName, Map<String, Object> variables);
    void sendAccountRegisterConfirmation(String email);
    void sendAccountDeleteConfirmation(String email);
    void sendNewFriendRequestConfirmation(String email);
}

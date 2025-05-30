package org.main.wiredspaceapi.controller;

import lombok.AllArgsConstructor;
import org.main.wiredspaceapi.business.UserService;
import org.main.wiredspaceapi.controller.dto.message.NotificationMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@AllArgsConstructor
@RequestMapping("notifications")
public class NotificationsController {
    private final SimpMessagingTemplate messagingTemplate;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<Void> sendNotificationToUsers(@RequestBody NotificationMessage message) {
        messagingTemplate.convertAndSend("/topic/publicmessages", message);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/private")
    public ResponseEntity<Void> sendPrivateMessage(@RequestBody NotificationMessage message, Principal principal) {
        message.setFrom(principal.getName());

        // Проверяем, что получатель существует
        boolean recipientExists = userService.findByEmail(message.getTo()).isPresent();
        if (!recipientExists) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        messagingTemplate.convertAndSendToUser(
                message.getTo(),
                "/queue/messages",
                message
        );
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}

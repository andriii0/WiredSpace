package org.main.wiredspaceapi.controller;

import lombok.AllArgsConstructor;
import org.main.wiredspaceapi.business.UserService;
import org.main.wiredspaceapi.domain.Message;
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
public class MessageController {
    private final SimpMessagingTemplate messagingTemplate;
    private final UserService userService;


    @PostMapping("/private")
    public ResponseEntity<Void> sendPrivateMessage(@RequestBody Message message, Principal principal) {
        if (principal == null || principal.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        message.setFrom(principal.getName());

        boolean recipientExists = userService.findByEmail(message.getTo()).isPresent();
        if (!recipientExists) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        messagingTemplate.convertAndSendToUser(message.getTo(), "/queue/messages", message);
        messageService.saveMessage(message); // сохраняем личное сообщение
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}

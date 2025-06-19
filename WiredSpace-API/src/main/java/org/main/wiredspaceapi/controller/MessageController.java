package org.main.wiredspaceapi.controller;

import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.business.MessageService;
import org.main.wiredspaceapi.business.UserService;
import org.main.wiredspaceapi.controller.dto.message.ChatPreviewDTO;
import org.main.wiredspaceapi.controller.dto.message.MessageDTO;
import org.main.wiredspaceapi.controller.exceptions.UnauthorizedException;
import org.main.wiredspaceapi.controller.mapper.MessageMapper;
import org.main.wiredspaceapi.controller.mapper.UserMapper;
import org.main.wiredspaceapi.controller.mapper.UserMapperImpl;
import org.main.wiredspaceapi.domain.Message;
import org.main.wiredspaceapi.domain.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
@PreAuthorize("isAuthenticated()")
public class MessageController {

    private final SimpMessagingTemplate messagingTemplate;
    private final UserService userService;
    private final MessageService messageService;
    private final MessageMapper messageMapper;
    private final UserMapper userMapper;

    @GetMapping("/chats")
    public ResponseEntity<List<ChatPreviewDTO>> getChats(Principal principal) {
        if (principal == null || principal.getName() == null) {
            throw new UnauthorizedException("Authentication required to view chats.");
        }

        String currentUserEmail = principal.getName();
        Map<User, Message> chats = messageService.getLatestMessagesPerChat(currentUserEmail);

        List<ChatPreviewDTO> response = chats.entrySet().stream()
                .map(entry -> new ChatPreviewDTO(
                        userMapper.userToUserDTO(entry.getKey()),
                        messageMapper.toDTO(entry.getValue())
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/private")
    public ResponseEntity<MessageDTO> sendPrivateMessage(@RequestBody MessageDTO messageDTO, Principal principal) {
        if (principal == null || principal.getName() == null) {
            throw new UnauthorizedException("Authentication required to send messages.");
        }

        String sender = principal.getName();
        String recipient = messageDTO.getTo();

        userService.findByEmail(recipient)
                .orElseThrow(() -> new UnauthorizedException("Recipient does not exist."));

        MessageDTO sent = messageService.sendPrivateMessage(sender, messageDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(sent);
    }

    @GetMapping("/conversation/{userEmail}")
    public ResponseEntity<List<MessageDTO>> getMessagesBetween(@PathVariable String userEmail, Principal principal) {
        if (principal == null || principal.getName() == null) {
            throw new UnauthorizedException("Authentication required to view messages.");
        }

        String currentUser = principal.getName();
        List<Message> messages = messageService.getMessagesBetween(currentUser, userEmail);
        List<MessageDTO> messageDTOs = messages.stream()
                .map(messageMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(messageDTOs);
    }
}

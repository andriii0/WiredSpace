package org.main.wiredspaceapi.controller.dto.message;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class MessageDTO {
    private UUID id;
    private String from;
    private String to;
    private String text;
    private LocalDateTime timestamp;
}

package org.main.wiredspaceapi.domain;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
    private UUID id;
    private String fromUser;
    private String toUser;
    private String text;
    private LocalDateTime timestamp;
}

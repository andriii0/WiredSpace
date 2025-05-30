package org.main.wiredspaceapi.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private String sender;

    private String recipient;

    private String text;

    private LocalDateTime timestamp;
}

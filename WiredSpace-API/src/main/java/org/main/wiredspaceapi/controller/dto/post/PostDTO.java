package org.main.wiredspaceapi.controller.dto.post;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PostDTO {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private UUID authorId;
}

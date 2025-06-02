package org.main.wiredspaceapi.controller.dto.post;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CommentDTO {
    private Long id;
    private UUID authorId;
    private Long postId;
    private String content;
    private LocalDateTime createdAt;
}

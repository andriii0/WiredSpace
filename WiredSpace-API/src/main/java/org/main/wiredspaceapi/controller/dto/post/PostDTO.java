package org.main.wiredspaceapi.controller.dto.post;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PostDTO {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private String authorName;
}

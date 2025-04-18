package org.main.wiredspaceapi.controller.dto.post;

import lombok.Data;

@Data
public class PostCreateDTO {
    private String content;
    private Long authorId;
}

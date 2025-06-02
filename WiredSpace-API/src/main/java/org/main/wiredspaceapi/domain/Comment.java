package org.main.wiredspaceapi.domain;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
    private Long id;
    private UUID authorId;
    private Long postId;
    private String content;
    private LocalDateTime createdAt;
}

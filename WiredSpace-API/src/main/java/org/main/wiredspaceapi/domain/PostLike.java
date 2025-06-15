package org.main.wiredspaceapi.domain;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostLike {
    private Long postId;
    private UUID userId;
    private LocalDateTime likedAt;
}

package org.main.wiredspaceapi.controller.dto.post;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportDTO {
    private Long id;
    private UUID reporterId;
    private Long postId;
    private String reason;
    private LocalDateTime reportedAt;
}

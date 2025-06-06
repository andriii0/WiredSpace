package org.main.wiredspaceapi.controller.dto.friendship;

import lombok.*;

import java.util.UUID;

@Data
@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class FriendshipStatusResponseDTO {
    private String status;
    private UUID userId;
    private UUID friendId;
}

package org.main.wiredspaceapi.controller.dto.friendship;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendshipResponseDTO {
    private UUID id;
    private UUID userId;
    private UUID friendId;
    private boolean accepted;
}

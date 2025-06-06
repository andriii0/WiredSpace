package org.main.wiredspaceapi.controller.dto.friendship;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.main.wiredspaceapi.controller.dto.user.UserDTO;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendshipResponseDTO {
    private UUID id;
    private UserDTO user;
    private UserDTO friend;
    private boolean accepted;
}

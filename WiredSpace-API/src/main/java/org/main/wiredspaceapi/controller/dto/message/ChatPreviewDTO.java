package org.main.wiredspaceapi.controller.dto.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.main.wiredspaceapi.controller.dto.user.UserDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatPreviewDTO {
    private UserDTO user;
    private MessageDTO message;
}

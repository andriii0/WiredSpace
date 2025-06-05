package org.main.wiredspaceapi.controller.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class PagedUserResponse {
    private List<UserDTO> users;
    private long total;
}

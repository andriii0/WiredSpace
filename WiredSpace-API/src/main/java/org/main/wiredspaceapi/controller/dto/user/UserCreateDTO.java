package org.main.wiredspaceapi.controller.dto.user;

import jakarta.persistence.Enumerated;
import lombok.Data;
import org.main.wiredspaceapi.domain.enums.UserRole;

@Data
public class UserCreateDTO {
    private String name;
    private String email;
    private String password;
    private UserRole role = UserRole.STANDARD_USER;
}

package org.main.wiredspaceapi.dto;

import jakarta.persistence.Enumerated;
import lombok.Data;
import org.main.wiredspaceapi.domain.enums.UserRole;

@Data
public class UserCreateDTO {
    private String name;
    private String password;
    @Enumerated
    private UserRole role = UserRole.STANDARD_USER;
}

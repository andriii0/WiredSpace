package org.main.wiredspaceapi.dto;

import lombok.Data;
import org.main.wiredspaceapi.domain.enums.UserRole;

@Data
public class UserCreateDTO {
    private String name;
    private String password;
    private UserRole userRole;
}

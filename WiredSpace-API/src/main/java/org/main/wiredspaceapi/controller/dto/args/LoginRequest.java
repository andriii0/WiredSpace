package org.main.wiredspaceapi.controller.dto.args;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}

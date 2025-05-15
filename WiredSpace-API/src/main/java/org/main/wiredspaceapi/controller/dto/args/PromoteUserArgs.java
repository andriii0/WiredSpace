package org.main.wiredspaceapi.controller.dto.args;

import lombok.Data;
import org.main.wiredspaceapi.domain.enums.AdminRole;

import java.util.UUID;

@Data
public class PromoteUserArgs {
    private UUID userId;
    private AdminRole adminRole;
}

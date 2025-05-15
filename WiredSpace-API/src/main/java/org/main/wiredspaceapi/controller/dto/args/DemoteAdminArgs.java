package org.main.wiredspaceapi.controller.dto.args;

import lombok.Data;
import org.main.wiredspaceapi.domain.enums.UserRole;

import java.util.UUID;

@Data
public class DemoteAdminArgs {
    private UUID adminId;
    private UserRole userRole;
}

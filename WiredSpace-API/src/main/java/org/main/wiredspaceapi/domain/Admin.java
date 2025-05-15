package org.main.wiredspaceapi.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.main.wiredspaceapi.domain.enums.AdminRole;

@Getter
@Setter
@NoArgsConstructor
public class Admin extends Account {

    private AdminRole role;

    public Admin(String name, String password, String email, AdminRole role) {
        super(null, name, email, password);
        this.role = role;
    }

    @Override
    public String getRoleAsString() {
        return "ROLE_" + role.name();
    }
}

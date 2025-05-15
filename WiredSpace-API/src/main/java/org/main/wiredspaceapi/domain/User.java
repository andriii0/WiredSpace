package org.main.wiredspaceapi.domain;

import jakarta.persistence.*;
import lombok.*;
import org.main.wiredspaceapi.domain.enums.UserRole;

@Getter
@Setter
@NoArgsConstructor
public class User extends Account {

    @Enumerated
    private UserRole role;

    public User(String name, String email, String password, UserRole role) {
        super(null, name, email, password);
        this.role = role;
    }

    @Override
    public String getRoleAsString() {
        return "ROLE_" + role.name();
    }
}
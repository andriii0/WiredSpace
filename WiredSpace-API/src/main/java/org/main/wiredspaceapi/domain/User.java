package org.main.wiredspaceapi.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.main.wiredspaceapi.domain.enums.UserRole;

import java.time.LocalDateTime;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class User extends Account {

    @Enumerated
    private UserRole role;

    private int friendsCount;
    private int commentsCount;
    private int likesGiven;
    private LocalDateTime registeredAt;

    public User(String name, String email, String password, UserRole role) {
        super(null, name, email, password);
        this.role = role;
    }

    @Override
    public String getRoleAsString() {
        return "ROLE_" + role.name();
    }
}

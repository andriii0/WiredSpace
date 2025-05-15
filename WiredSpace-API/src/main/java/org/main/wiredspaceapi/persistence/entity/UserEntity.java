package org.main.wiredspaceapi.persistence.entity;

import jakarta.persistence.*;
import jdk.jfr.Name;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;
import org.main.wiredspaceapi.domain.enums.UserRole;

import java.util.UUID;

@Entity
@Table(name = "user_accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    private String name;

    @NotNull
    private String email;

    @NotNull
    private String password;

    @Enumerated
    private UserRole role;
}

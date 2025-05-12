package org.main.wiredspaceapi.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.main.wiredspaceapi.domain.enums.UserRole;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;
}

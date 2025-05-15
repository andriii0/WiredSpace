package org.main.wiredspaceapi.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.main.wiredspaceapi.domain.enums.AdminRole;

@Entity
@Table(name = "admins")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminEntity extends AccountEntity {

    @Enumerated(EnumType.STRING)
    private AdminRole role;
}

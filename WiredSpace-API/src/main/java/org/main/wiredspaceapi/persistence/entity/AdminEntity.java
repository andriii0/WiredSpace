package org.main.wiredspaceapi.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.main.wiredspaceapi.domain.enums.AdminRole;

@Entity
@DiscriminatorValue("ADMIN")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminEntity extends AccountEntity {

    @Enumerated
    private AdminRole role;
}

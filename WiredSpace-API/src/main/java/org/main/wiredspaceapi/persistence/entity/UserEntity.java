package org.main.wiredspaceapi.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.main.wiredspaceapi.domain.enums.UserRole;

@Entity
@DiscriminatorValue("USER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity extends AccountEntity {

    @Enumerated
    private UserRole role;
}

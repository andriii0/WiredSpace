package org.main.wiredspaceapi.domain;


import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.main.wiredspaceapi.domain.enums.AdminRole;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Admin extends Account {

    @Enumerated(EnumType.STRING)
    private AdminRole role;

    public Admin(String name, String password, AdminRole role) {
        super(null, name, password);
        this.role = role;
    }
}

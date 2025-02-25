package org.main.wiredspaceapi.Domain;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@MappedSuperclass
@AllArgsConstructor
@NoArgsConstructor
public abstract class Account {
    @Id
    private Long id;
    private String name;
    private String password;
}

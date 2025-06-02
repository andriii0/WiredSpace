package org.main.wiredspaceapi.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.main.wiredspaceapi.domain.enums.UserRole;
import java.util.HashSet;
import java.util.Set;

@Entity
@DiscriminatorValue("USER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity extends AccountEntity {

    @Enumerated
    private UserRole role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PostLikeEntity> likedPosts = new HashSet<>();
}

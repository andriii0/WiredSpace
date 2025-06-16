package org.main.wiredspaceapi.persistence.entity;

import jakarta.persistence.*;
import jdk.jshell.Snippet;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.main.wiredspaceapi.domain.enums.UserRole;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@SuperBuilder
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

    @Column(nullable = true)
    private int commentsCount = 0;

    @Column(nullable = true)
    private int friendsCount = 0;

    public int getLikesGiven() {
        return likedPosts == null ? 0 : likedPosts.size();
    }

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReportEntity> reports = new HashSet<>();
}

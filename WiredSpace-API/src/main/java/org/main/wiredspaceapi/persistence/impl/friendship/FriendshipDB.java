package org.main.wiredspaceapi.persistence.impl.friendship;

import org.main.wiredspaceapi.persistence.entity.FriendshipEntity;
import org.main.wiredspaceapi.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FriendshipDB extends JpaRepository<FriendshipEntity, UUID> {
    List<FriendshipEntity> findByUserOrFriend(UserEntity user, UserEntity friend);
    Optional<FriendshipEntity> findByUserAndFriend(UserEntity user, UserEntity friend);
}

package org.main.wiredspaceapi.persistence;

import org.main.wiredspaceapi.domain.Friendship;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FriendshipRepository {
    Friendship save(Friendship friendship);
    void delete(UUID id);
    Optional<Friendship> findById(UUID id);
    Optional<Friendship> findByUserAndFriend(UUID userId, UUID friendId);
    List<Friendship> findAllByUser(UUID userId);
}

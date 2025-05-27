package org.main.wiredspaceapi.persistence.impl.friendship;

import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.domain.Friendship;
import org.main.wiredspaceapi.persistence.FriendshipRepository;
import org.main.wiredspaceapi.persistence.entity.FriendshipEntity;
import org.main.wiredspaceapi.persistence.entity.UserEntity;
import org.main.wiredspaceapi.persistence.impl.user.UserDB;
import org.main.wiredspaceapi.persistence.mapper.FriendshipEntityMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class FriendshipRepositoryImpl implements FriendshipRepository {

    private final FriendshipDB friendshipDB;
    private final UserDB userDB;
    private final FriendshipEntityMapper mapper;

    @Override
    public Friendship save(Friendship friendship) {
        UserEntity user = userDB.findById(friendship.getUserId()).orElseThrow();
        UserEntity friend = userDB.findById(friendship.getFriendId()).orElseThrow();

        FriendshipEntity entity = new FriendshipEntity(
                friendship.getId(),
                user,
                friend,
                friendship.isAccepted()
        );

        FriendshipEntity saved = friendshipDB.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public void delete(UUID id) {
        friendshipDB.deleteById(id);
    }

    @Override
    public Optional<Friendship> findByUserAndFriend(UUID userId, UUID friendId) {
        UserEntity user = userDB.findById(userId).orElseThrow();
        UserEntity friend = userDB.findById(friendId).orElseThrow();

        return friendshipDB.findByUserAndFriend(user, friend).map(mapper::toDomain);
    }

    @Override
    public List<Friendship> findAllByUser(UUID userId) {
        UserEntity user = userDB.findById(userId).orElseThrow();
        return friendshipDB.findByUserOrFriend(user, user).stream()
                .map(mapper::toDomain)
                .toList();
    }
}

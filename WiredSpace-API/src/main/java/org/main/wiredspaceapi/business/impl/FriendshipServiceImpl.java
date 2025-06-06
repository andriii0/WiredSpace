package org.main.wiredspaceapi.business.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.main.wiredspaceapi.business.FriendshipService;
import org.main.wiredspaceapi.domain.Friendship;
import org.main.wiredspaceapi.persistence.FriendshipRepository;
import org.main.wiredspaceapi.persistence.UserRepository;
import org.main.wiredspaceapi.security.util.AuthenticatedUserProvider;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendshipServiceImpl implements FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Override
    public Friendship sendFriendRequest(UUID userId, UUID friendId) {
        validateUserExistence(userId);
        validateUserExistence(friendId);

        if (userId.equals(friendId)) {
            throw new IllegalArgumentException("You cannot send a friend request to yourself.");
        }

        Friendship friendship = new Friendship(null, userId, friendId, false); //is not accepted by default
        log.info("Sending friend request from {} to {}", userId, friendId);

        return friendshipRepository.save(friendship);
    }

    @Override
    public Friendship acceptFriendRequest(UUID friendshipId) {
        Friendship friendship = findFriendshipOrThrow(friendshipId);

        if (friendship.isAccepted()) {
            throw new IllegalStateException("Friend request already accepted.");
        }

        Friendship accepted = new Friendship(
                friendship.getId(),
                friendship.getUserId(),
                friendship.getFriendId(),
                true
        );

        log.info("Accepted friend request with ID {}", friendshipId);
        return friendshipRepository.save(accepted);
    }

    @Override
    public void deleteFriendship(UUID friendshipId) {
        Friendship friendship = findFriendshipOrThrow(friendshipId);
        UUID currentId = authenticatedUserProvider.getCurrentUserId();
        if (friendship.getUserId().equals(currentId) || friendship.getFriendId().equals(currentId)) {
            friendshipRepository.delete(friendshipId);
            log.info("Deleted friendship between {} and {}", friendship.getUserId(), friendship.getFriendId());
        } else {
            throw new IllegalArgumentException("You cannot delete a friendship.");
        }

    }

    @Override
    public List<Friendship> getFriendsOfUser(UUID userId) {
        validateUserExistence(userId);
        return friendshipRepository.findAllByUser(userId).stream()
                .toList();
    }

    @Override
    public Friendship updateFriendship(UUID friendshipId, boolean accepted) {
        Friendship friendship = findFriendshipOrThrow(friendshipId);

        Friendship updated = new Friendship(
                friendship.getId(),
                friendship.getUserId(),
                friendship.getFriendId(),
                accepted
        );

        log.info("Updated friendship {} to accepted={}", friendshipId, accepted);
        return friendshipRepository.save(updated);
    }

    private void validateUserExistence(UUID userId) {
        userRepository.getUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found"));
    }

    private Friendship findFriendshipOrThrow(UUID id) {
        return friendshipRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Friendship with ID " + id + " not found"));
    }
}

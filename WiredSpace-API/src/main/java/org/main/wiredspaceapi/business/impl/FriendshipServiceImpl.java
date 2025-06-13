package org.main.wiredspaceapi.business.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.main.wiredspaceapi.business.FriendshipService;
import org.main.wiredspaceapi.business.impl.UserStatisticsService;
import org.main.wiredspaceapi.domain.Friendship;
import org.main.wiredspaceapi.persistence.FriendshipRepository;
import org.main.wiredspaceapi.persistence.UserRepository;
import org.main.wiredspaceapi.security.util.AuthenticatedUserProvider;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FriendshipServiceImpl implements FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final UserStatisticsService userStatisticsService;

    @Override
    public Friendship sendFriendRequest(UUID userId, UUID friendId) {
        validateUserExistence(userId);
        validateUserExistence(friendId);

        if (userId.equals(friendId)) {
            throw new IllegalArgumentException("You cannot send a friend request to yourself.");
        }

        Optional<Friendship> friendshipOptional = friendshipRepository.findByUserAndFriend(userId, friendId);

        if (friendshipOptional.isPresent()) {
            throw new IllegalArgumentException("You cannot send a friend request to your friend.");
        }

        Friendship friendship = new Friendship(null, userId, friendId, false); // is not accepted by default

        return friendshipRepository.save(friendship);
    }

    @Override
    public Friendship acceptFriendRequest(UUID friendshipId) {
        Friendship friendship = findFriendshipById(friendshipId);

        if (friendship.isAccepted()) {
            throw new IllegalStateException("Friend request already accepted.");
        }

        Friendship accepted = new Friendship(
                friendship.getId(),
                friendship.getUserId(),
                friendship.getFriendId(),
                true
        );

        Friendship saved = friendshipRepository.save(accepted);

        userStatisticsService.incrementFriends(friendship.getUserId());
        userStatisticsService.incrementFriends(friendship.getFriendId());

        return saved;
    }

    @Override
    public void deleteFriendship(UUID friendshipId) {
        Friendship friendship = findFriendshipById(friendshipId);
        UUID currentId = authenticatedUserProvider.getCurrentUserId();
        if (friendship.getUserId().equals(currentId) || friendship.getFriendId().equals(currentId)) {
            friendshipRepository.delete(friendshipId);

            if (friendship.isAccepted()) {
                userStatisticsService.decrementFriends(friendship.getUserId());
                userStatisticsService.decrementFriends(friendship.getFriendId());
            }
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
        Friendship friendship = findFriendshipById(friendshipId);

        boolean wasAccepted = friendship.isAccepted();

        Friendship updated = new Friendship(
                friendship.getId(),
                friendship.getUserId(),
                friendship.getFriendId(),
                accepted
        );

        Friendship saved = friendshipRepository.save(updated);

        if (!wasAccepted && accepted) {
            userStatisticsService.incrementFriends(friendship.getUserId());
            userStatisticsService.incrementFriends(friendship.getFriendId());
        } else if (wasAccepted && !accepted) {
            userStatisticsService.decrementFriends(friendship.getUserId());
            userStatisticsService.decrementFriends(friendship.getFriendId());
        }

        return saved;
    }

    private void validateUserExistence(UUID userId) {
        userRepository.getUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found"));
    }

    public Friendship findFriendshipById(UUID id) {
        return friendshipRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Friendship with ID " + id + " not found"));
    }

    @Override
    public String getFriendshipStatus(UUID friendId) {
        UUID currentUserId = authenticatedUserProvider.getCurrentUserId();
        Optional<Friendship> friendshipOptional = friendshipRepository.findByUserAndFriend(currentUserId, friendId);

        if (friendshipOptional.isEmpty()) {
            return "none";
        }

        Friendship friendship = friendshipOptional.get();

        if (friendship.isAccepted()) {
            return "accepted";
        } else {
            if (friendship.getUserId().equals(currentUserId)) {
                return "sent";
            } else {
                return "received";
            }
        }
    }

}

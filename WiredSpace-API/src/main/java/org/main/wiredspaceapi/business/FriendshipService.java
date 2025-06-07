package org.main.wiredspaceapi.business;

import org.main.wiredspaceapi.domain.Friendship;

import java.util.List;
import java.util.UUID;

public interface FriendshipService {
    Friendship sendFriendRequest(UUID userId, UUID friendId);
    Friendship acceptFriendRequest(UUID friendshipId);
    void deleteFriendship(UUID friendshipId);
    List<Friendship> getFriendsOfUser(UUID userId);
    Friendship updateFriendship(UUID friendshipId, boolean accepted);
    Friendship findFriendshipById(UUID id);
    String getFriendshipStatus(UUID friendId);
}

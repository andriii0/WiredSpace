package org.main.wiredspaceapi.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.main.wiredspaceapi.business.impl.FriendshipServiceImpl;
import org.main.wiredspaceapi.domain.Friendship;
import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.persistence.FriendshipRepository;
import org.main.wiredspaceapi.persistence.UserRepository;
import org.main.wiredspaceapi.security.util.AuthenticatedUserProvider;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FriendshipServiceTest {

    @Mock
    private FriendshipRepository friendshipRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticatedUserProvider authenticatedUserProvider;

    @InjectMocks
    private FriendshipServiceImpl friendshipService;

    private UUID userId;
    private UUID friendId;
    private Friendship friendship;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        friendId = UUID.randomUUID();
        friendship = new Friendship(UUID.randomUUID(), userId, friendId, false);
    }

    @Test
    void sendFriendRequest_ShouldReturnSavedFriendship() {
        when(userRepository.getUserById(userId)).thenReturn(Optional.of(mock(User.class)));
        when(userRepository.getUserById(friendId)).thenReturn(Optional.of(mock(User.class)));
        when(friendshipRepository.findByUserAndFriend(userId, friendId)).thenReturn(Optional.empty());
        when(friendshipRepository.save(any())).thenReturn(friendship);

        Friendship result = friendshipService.sendFriendRequest(userId, friendId);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(friendId, result.getFriendId());
        assertFalse(result.isAccepted());
    }

    @Test
    void sendFriendRequest_ShouldThrowException_WhenUserSendsToSelf() {
        assertThrows(IllegalArgumentException.class,
                () -> friendshipService.sendFriendRequest(userId, userId));
    }

    @Test
    void sendFriendRequest_ShouldThrowException_WhenFriendshipExists() {
        when(userRepository.getUserById(userId)).thenReturn(Optional.of(mock(User.class)));
        when(userRepository.getUserById(friendId)).thenReturn(Optional.of(mock(User.class)));
        when(friendshipRepository.findByUserAndFriend(userId, friendId)).thenReturn(Optional.of(friendship));

        assertThrows(IllegalArgumentException.class,
                () -> friendshipService.sendFriendRequest(userId, friendId));
    }

    @Test
    void acceptFriendRequest_ShouldUpdateFriendshipToAccepted() {
        UUID friendshipId = friendship.getId();
        when(friendshipRepository.findById(friendshipId)).thenReturn(Optional.of(friendship));
        when(friendshipRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Friendship result = friendshipService.acceptFriendRequest(friendshipId);

        assertTrue(result.isAccepted());
    }

    @Test
    void acceptFriendRequest_ShouldThrowException_WhenAlreadyAccepted() {
        friendship = new Friendship(friendship.getId(), userId, friendId, true);
        when(friendshipRepository.findById(friendship.getId())).thenReturn(Optional.of(friendship));

        assertThrows(IllegalStateException.class,
                () -> friendshipService.acceptFriendRequest(friendship.getId()));
    }

    @Test
    void deleteFriendship_ShouldDelete_WhenCurrentUserIsInvolved() {
        UUID currentUserId = userId;
        when(friendshipRepository.findById(friendship.getId())).thenReturn(Optional.of(friendship));
        when(authenticatedUserProvider.getCurrentUserId()).thenReturn(currentUserId);

        friendshipService.deleteFriendship(friendship.getId());

        verify(friendshipRepository).delete(friendship.getId());
    }

    @Test
    void deleteFriendship_ShouldThrow_WhenCurrentUserNotInvolved() {
        when(friendshipRepository.findById(friendship.getId())).thenReturn(Optional.of(friendship));
        when(authenticatedUserProvider.getCurrentUserId()).thenReturn(UUID.randomUUID());

        assertThrows(IllegalArgumentException.class,
                () -> friendshipService.deleteFriendship(friendship.getId()));
    }

    @Test
    void getFriendsOfUser_ShouldReturnListOfFriends() {
        when(userRepository.getUserById(userId)).thenReturn(Optional.of(mock(User.class)));
        when(friendshipRepository.findAllByUser(userId)).thenReturn(List.of(friendship));

        List<Friendship> result = friendshipService.getFriendsOfUser(userId);

        assertEquals(1, result.size());
        assertEquals(friendship, result.get(0));
    }

    @Test
    void updateFriendship_ShouldUpdateAcceptedStatus() {
        when(friendshipRepository.findById(friendship.getId())).thenReturn(Optional.of(friendship));
        when(friendshipRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Friendship result = friendshipService.updateFriendship(friendship.getId(), true);

        assertTrue(result.isAccepted());
    }

    @Test
    void findFriendshipById_ShouldReturnFriendship_WhenExists() {
        when(friendshipRepository.findById(friendship.getId())).thenReturn(Optional.of(friendship));

        Friendship result = friendshipService.findFriendshipById(friendship.getId());

        assertNotNull(result);
        assertEquals(friendship.getId(), result.getId());
    }

    @Test
    void findFriendshipById_ShouldThrow_WhenNotFound() {
        UUID id = UUID.randomUUID();
        when(friendshipRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> friendshipService.findFriendshipById(id));
    }

    @Test
    void getFriendshipStatus_ShouldReturnNone_WhenNoFriendship() {
        when(authenticatedUserProvider.getCurrentUserId()).thenReturn(userId);
        when(friendshipRepository.findByUserAndFriend(userId, friendId)).thenReturn(Optional.empty());

        String status = friendshipService.getFriendshipStatus(friendId);

        assertEquals("none", status);
    }

    @Test
    void getFriendshipStatus_ShouldReturnAccepted() {
        when(authenticatedUserProvider.getCurrentUserId()).thenReturn(userId);
        friendship = new Friendship(friendship.getId(), userId, friendId, true);
        when(friendshipRepository.findByUserAndFriend(userId, friendId)).thenReturn(Optional.of(friendship));

        String status = friendshipService.getFriendshipStatus(friendId);

        assertEquals("accepted", status);
    }

    @Test
    void getFriendshipStatus_ShouldReturnSent_WhenCurrentUserIsSender() {
        when(authenticatedUserProvider.getCurrentUserId()).thenReturn(userId);
        when(friendshipRepository.findByUserAndFriend(userId, friendId)).thenReturn(Optional.of(friendship));

        String status = friendshipService.getFriendshipStatus(friendId);

        assertEquals("sent", status);
    }

    @Test
    void getFriendshipStatus_ShouldReturnReceived_WhenCurrentUserIsReceiver() {
        when(authenticatedUserProvider.getCurrentUserId()).thenReturn(friendId);
        when(friendshipRepository.findByUserAndFriend(friendId, userId)).thenReturn(Optional.of(friendship));

        String status = friendshipService.getFriendshipStatus(userId);

        assertEquals("received", status);
    }
}

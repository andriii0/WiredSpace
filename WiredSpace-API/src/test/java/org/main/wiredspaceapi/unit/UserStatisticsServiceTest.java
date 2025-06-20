package org.main.wiredspaceapi.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.main.wiredspaceapi.business.impl.UserStatisticsService;
import org.main.wiredspaceapi.controller.dto.user.UserStatisticsDTO;
import org.main.wiredspaceapi.domain.Post;
import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.persistence.PostRepository;
import org.main.wiredspaceapi.persistence.UserRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserStatisticsServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private UserStatisticsService userStatisticsService;

    private UUID userId;
    private User user;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        user = new User();
        user.setCommentsCount(2);
        user.setLikesGiven(3);
        user.setFriendsCount(4);
    }

    @Test
    void incrementComments_shouldIncreaseCommentsCount() {
        when(userRepository.getUserById(userId)).thenReturn(Optional.of(user));

        userStatisticsService.incrementComments(userId);

        assertEquals(3, user.getCommentsCount());
        verify(userRepository).updateStatistics(user);
    }

    @Test
    void decrementComments_shouldDecreaseCommentsCount_notBelowZero() {
        user.setCommentsCount(1);
        when(userRepository.getUserById(userId)).thenReturn(Optional.of(user));

        userStatisticsService.decrementComments(userId);

        assertEquals(0, user.getCommentsCount());
        verify(userRepository).updateStatistics(user);

        // Test that it does not go below zero
        userStatisticsService.decrementComments(userId);
        assertEquals(0, user.getCommentsCount());
    }

    @Test
    void incrementLikes_shouldIncreaseLikesGiven() {
        when(userRepository.getUserById(userId)).thenReturn(Optional.of(user));

        userStatisticsService.incrementLikes(userId);

        assertEquals(4, user.getLikesGiven());
        verify(userRepository).updateStatistics(user);
    }

    @Test
    void decrementLikes_shouldDecreaseLikesGiven_notBelowZero() {
        user.setLikesGiven(1);
        when(userRepository.getUserById(userId)).thenReturn(Optional.of(user));

        userStatisticsService.decrementLikes(userId);

        assertEquals(0, user.getLikesGiven());
        verify(userRepository).updateStatistics(user);

        userStatisticsService.decrementLikes(userId);
        assertEquals(0, user.getLikesGiven());
    }

    @Test
    void incrementFriends_shouldIncreaseFriendsCount() {
        when(userRepository.getUserById(userId)).thenReturn(Optional.of(user));

        userStatisticsService.incrementFriends(userId);

        assertEquals(5, user.getFriendsCount());
        verify(userRepository).updateStatistics(user);
    }

    @Test
    void decrementFriends_shouldDecreaseFriendsCount_notBelowZero() {
        user.setFriendsCount(1);
        when(userRepository.getUserById(userId)).thenReturn(Optional.of(user));

        userStatisticsService.decrementFriends(userId);

        assertEquals(0, user.getFriendsCount());
        verify(userRepository).updateStatistics(user);

        userStatisticsService.decrementFriends(userId);
        assertEquals(0, user.getFriendsCount());
    }

    @Test
    void getUserStatistics_shouldReturnCorrectDto() {
        when(userRepository.getUserById(userId)).thenReturn(Optional.of(user));
        Post post1 = new Post();
        Post post2 = new Post();
        when(postRepository.getPostsByUserId(userId)).thenReturn(List.of(post1, post2));

        UserStatisticsDTO stats = userStatisticsService.getUserStatistics(userId);

        assertEquals(user.getCommentsCount(), stats.getCommentsCount());
        assertEquals(user.getLikesGiven(), stats.getLikesGiven());
        assertEquals(user.getFriendsCount(), stats.getFriendsCount());
        assertEquals(2, stats.getPostsCount());
    }

    @Test
    void getUserStatistics_shouldThrowIfUserNotFound() {
        when(userRepository.getUserById(userId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userStatisticsService.getUserStatistics(userId));
    }
}

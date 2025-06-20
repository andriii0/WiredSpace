package org.main.wiredspaceapi.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.main.wiredspaceapi.business.impl.PostLikeServiceImpl;
import org.main.wiredspaceapi.business.impl.UserStatisticsService;
import org.main.wiredspaceapi.controller.dto.user.UserDTO;
import org.main.wiredspaceapi.controller.mapper.UserMapper;
import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.persistence.PostLikeRepository;
import org.main.wiredspaceapi.persistence.PostRepository;
import org.main.wiredspaceapi.persistence.UserRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostLikeServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;
    @Mock private UserStatisticsService userStatisticsService;
    @Mock private PostLikeRepository postLikeRepository;
    @Mock private PostRepository postRepository;

    @InjectMocks
    private PostLikeServiceImpl postLikeService;

    private UUID userId;
    private Long postId;
    private User user;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        postId = 123L;

        user = new User();
        user.setId(userId);
        user.setName("TestUser");
    }

    @Test
    void likeOrUnlikePost_shouldLikePost_whenNotLikedBefore() {
        when(postRepository.existsById(postId)).thenReturn(true);
        when(userRepository.getUserById(userId)).thenReturn(Optional.of(user));
        when(postLikeRepository.hasUserLikedPost(postId, userId)).thenReturn(false);

        postLikeService.likeOrUnlikePost(postId, userId);

        verify(postLikeRepository).likePost(postId, userId);
        verify(userStatisticsService).incrementLikes(userId);
    }

    @Test
    void likeOrUnlikePost_shouldUnlikePost_whenAlreadyLiked() {
        when(postRepository.existsById(postId)).thenReturn(true);
        when(userRepository.getUserById(userId)).thenReturn(Optional.of(user));
        when(postLikeRepository.hasUserLikedPost(postId, userId)).thenReturn(true);

        postLikeService.likeOrUnlikePost(postId, userId);

        verify(postLikeRepository).unlikePost(postId, userId);
        verify(userStatisticsService).decrementLikes(userId);
    }

    @Test
    void likeOrUnlikePost_shouldThrow_whenPostNotFound() {
        when(postRepository.existsById(postId)).thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> postLikeService.likeOrUnlikePost(postId, userId));
    }

    @Test
    void likeOrUnlikePost_shouldThrow_whenUserNotFound() {
        when(postRepository.existsById(postId)).thenReturn(true);
        when(userRepository.getUserById(userId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> postLikeService.likeOrUnlikePost(postId, userId));
    }

    @Test
    void getUsersWhoLikedPost_shouldReturnUserDTOList() {
        UUID user1Id = UUID.randomUUID();
        UUID user2Id = UUID.randomUUID();

        User user1 = new User(); user1.setId(user1Id);
        User user2 = new User(); user2.setId(user2Id);

        UserDTO dto1 = new UserDTO(); dto1.setId(user1Id);
        UserDTO dto2 = new UserDTO(); dto2.setId(user2Id);

        when(postRepository.existsById(postId)).thenReturn(true);
        when(postLikeRepository.getUsersWhoLikedPost(postId)).thenReturn(List.of(user1Id, user2Id));
        when(userRepository.getUserById(user1Id)).thenReturn(Optional.of(user1));
        when(userRepository.getUserById(user2Id)).thenReturn(Optional.of(user2));
        when(userMapper.userToUserDTO(user1)).thenReturn(dto1);
        when(userMapper.userToUserDTO(user2)).thenReturn(dto2);

        List<UserDTO> result = postLikeService.getUsersWhoLikedPost(postId);

        assertEquals(2, result.size());
        assertTrue(result.contains(dto1));
        assertTrue(result.contains(dto2));
    }

    @Test
    void getUsersWhoLikedPost_shouldThrow_whenPostDoesNotExist() {
        when(postRepository.existsById(postId)).thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> postLikeService.getUsersWhoLikedPost(postId));
    }

    @Test
    void deleteAllLikesForPost_shouldCallRepository() {
        postLikeService.deleteAllLikesForPost(postId);
        verify(postLikeRepository).deleteAllLikesForPost(postId);
    }

    @Test
    void hasUserLikedPost_shouldReturnTrueIfLiked() {
        when(postLikeRepository.hasUserLikedPost(postId, userId)).thenReturn(true);
        assertTrue(postLikeService.hasUserLikedPost(postId, userId));
    }

    @Test
    void findLikedPostIds_shouldReturnSetOfIds() {
        List<Long> postIds = List.of(1L, 2L, 3L);
        Set<Long> likedPostIds = Set.of(1L, 3L);
        when(postLikeRepository.findLikedPostIds(userId, postIds)).thenReturn(likedPostIds);

        Set<Long> result = postLikeService.findLikedPostIds(userId, postIds);

        assertEquals(likedPostIds, result);
    }
}

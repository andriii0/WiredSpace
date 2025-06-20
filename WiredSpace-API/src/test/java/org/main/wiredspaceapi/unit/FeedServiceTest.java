package org.main.wiredspaceapi.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.main.wiredspaceapi.business.PostLikeService;
import org.main.wiredspaceapi.business.PostService;
import org.main.wiredspaceapi.business.impl.FeedServiceImpl;
import org.main.wiredspaceapi.controller.exceptions.NoMorePostsAvailableException;
import org.main.wiredspaceapi.domain.Friendship;
import org.main.wiredspaceapi.domain.Post;
import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.persistence.FriendshipRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FeedServiceTest {

    private FriendshipRepository friendshipRepository;
    private PostLikeService postLikeService;
    private PostService postService;
    private FeedServiceImpl feedService;

    private UUID userId;
    private UUID friendId;
    private Post friendPost;
    private Post randomPost;

    @BeforeEach
    void setUp() {
        friendshipRepository = mock(FriendshipRepository.class);
        postLikeService = mock(PostLikeService.class);
        postService = mock(PostService.class);
        feedService = new FeedServiceImpl(friendshipRepository, postLikeService, postService);

        userId = UUID.randomUUID();
        friendId = UUID.randomUUID();

        User friendUser = new User();
        friendUser.setId(friendId);
        friendUser.setName("Friend");

        friendPost = Post.builder()
                .id(1L)
                .author(friendUser)
                .content("Friend post")
                .createdAt(LocalDateTime.now())
                .build();

        User randomUser = new User();
        randomUser.setId(UUID.randomUUID());
        randomUser.setName("Random");

        randomPost = Post.builder()
                .id(2L)
                .author(randomUser)
                .content("Random post")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void getSmartFeed_shouldReturnPosts_fromFriends_andExcludeLiked() {
        Friendship friendship = new Friendship(UUID.randomUUID(), userId, friendId, true);

        when(friendshipRepository.findAllByUser(userId)).thenReturn(List.of(friendship));
        when(postService.findPostsByAuthorIdsAndDate(anyList(), any(), any(), anyInt()))
                .thenReturn(List.of(friendPost));
        when(postLikeService.findLikedPostIds(eq(userId), anyList()))
                .thenReturn(Set.of());

        List<Post> result = feedService.getSmartFeed(userId, 0, 5);

        assertEquals(1, result.size());
        assertEquals("Friend post", result.get(0).getContent());
    }

    @Test
    void getSmartFeed_shouldFallbackToRandomPosts_ifNotEnoughFriendPosts() {
        when(friendshipRepository.findAllByUser(userId)).thenReturn(Collections.emptyList());
        when(postService.findRandomPostsExcludingUsers(anyList(), eq(userId), any(), any(), anyInt()))
                .thenReturn(List.of(randomPost));
        when(postLikeService.findLikedPostIds(eq(userId), anyList()))
                .thenReturn(Set.of());

        List<Post> result = feedService.getSmartFeed(userId, 0, 5);

        assertEquals(1, result.size());
        assertEquals("Random post", result.get(0).getContent());
    }

    @Test
    void getSmartFeed_shouldExcludeLikedPosts() {
        when(friendshipRepository.findAllByUser(userId)).thenReturn(Collections.emptyList());
        when(postService.findRandomPostsExcludingUsers(anyList(), eq(userId), any(), any(), anyInt()))
                .thenReturn(List.of(randomPost));
        when(postLikeService.findLikedPostIds(eq(userId), anyList()))
                .thenReturn(Set.of(randomPost.getId())); // User liked the only post

        assertThrows(NoMorePostsAvailableException.class, () ->
                feedService.getSmartFeed(userId, 0, 5));
    }

    @Test
    void getSmartFeed_shouldThrow_ifNoPostsFound() {
        when(friendshipRepository.findAllByUser(userId)).thenReturn(Collections.emptyList());
        when(postService.findRandomPostsExcludingUsers(anyList(), eq(userId), any(), any(), anyInt()))
                .thenReturn(Collections.emptyList());

        assertThrows(NoMorePostsAvailableException.class, () ->
                feedService.getSmartFeed(userId, 0, 5));
    }
}

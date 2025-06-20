package org.main.wiredspaceapi.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.main.wiredspaceapi.business.impl.PostServiceImpl;
import org.main.wiredspaceapi.business.CommentService;
import org.main.wiredspaceapi.business.PostLikeService;
import org.main.wiredspaceapi.business.ReportService;
import org.main.wiredspaceapi.business.impl.UserStatisticsService;
import org.main.wiredspaceapi.controller.dto.post.PostCreateDTO;
import org.main.wiredspaceapi.controller.dto.post.PostDTO;
import org.main.wiredspaceapi.controller.dto.user.UserDTO;
import org.main.wiredspaceapi.controller.exceptions.PostNotFoundException;
import org.main.wiredspaceapi.controller.mapper.PostMapper;
import org.main.wiredspaceapi.controller.mapper.UserMapper;
import org.main.wiredspaceapi.domain.Post;
import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.persistence.PostRepository;
import org.main.wiredspaceapi.persistence.UserRepository;
import org.main.wiredspaceapi.security.util.AuthenticatedUserProvider;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock private PostRepository postRepository;
    @Mock private UserRepository userRepository;
    @Mock private PostMapper postMapper;
    @Mock private UserMapper userMapper;
    @Mock private AuthenticatedUserProvider authenticatedUserProvider;
    @Mock private UserStatisticsService userStatisticsService;
    @Mock private CommentService commentService;
    @Mock private PostLikeService postLikeService;
    @Mock private ReportService reportService;

    @InjectMocks
    private PostServiceImpl postService;

    private UUID userId;
    private User author;
    private Post post;
    private PostDTO postDTO;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        author = new User();
        author.setId(userId);

        post = Post.builder()
                .id(1L)
                .content("test post")
                .createdAt(LocalDateTime.now())
                .author(author)
                .build();

        postDTO = new PostDTO();
        postDTO.setId(1L);
        postDTO.setContent("test post");
        postDTO.setAuthorId(userId);
        postDTO.setLikedByUserIds(List.of());
    }

    @Test
    void createPost_shouldCreateAndReturnDTO() {
        PostCreateDTO dto = new PostCreateDTO();
        dto.setContent("test post");

        when(authenticatedUserProvider.getCurrentUserId()).thenReturn(userId);
        when(userRepository.getUserById(userId)).thenReturn(Optional.of(author));
        when(postMapper.postCreateDtoToPost(dto)).thenReturn(post);
        when(postRepository.create(any())).thenReturn(post);
        when(postMapper.postToPostDto(post)).thenReturn(postDTO);
        when(postLikeService.getUsersWhoLikedPost(1L)).thenReturn(List.of());

        PostDTO result = postService.createPost(dto);

        assertNotNull(result);
        assertEquals("test post", result.getContent());
        assertEquals(userId, result.getAuthorId());
    }

    @Test
    void createPost_shouldThrow_whenContentEmpty() {
        PostCreateDTO dto = new PostCreateDTO();
        dto.setContent(" ");

        assertThrows(RuntimeException.class, () -> postService.createPost(dto));
    }

    @Test
    void getPostsByUserId_shouldReturnPostDTOs() {
        when(postRepository.getPostsByUserId(userId)).thenReturn(List.of(post));
        when(postMapper.postToPostDto(post)).thenReturn(postDTO);
        when(postLikeService.getUsersWhoLikedPost(1L)).thenReturn(List.of());

        List<PostDTO> results = postService.getPostsByUserId(userId);

        assertEquals(1, results.size());
        assertEquals("test post", results.get(0).getContent());
    }

    @Test
    void getPostById_shouldReturnDTO_whenFound() {
        when(postRepository.getById(1L)).thenReturn(Optional.of(post));
        when(postMapper.postToPostDto(post)).thenReturn(postDTO);
        when(postLikeService.getUsersWhoLikedPost(1L)).thenReturn(List.of());

        PostDTO result = postService.getPostById(1L);
        assertEquals(1L, result.getId());
    }

    @Test
    void getPostById_shouldThrowException_whenNotFound() {
        when(postRepository.getById(1L)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.getPostById(1L));
    }

    @Test
    void updatePost_shouldUpdateAndReturnDTO() {
        PostCreateDTO dto = new PostCreateDTO();
        dto.setContent("updated content");

        when(postRepository.getById(1L)).thenReturn(Optional.of(post));
        when(authenticatedUserProvider.getCurrentUserId()).thenReturn(userId);
        when(postRepository.update(any())).thenReturn(post);
        when(postMapper.postToPostDto(post)).thenReturn(postDTO);
        when(postLikeService.getUsersWhoLikedPost(1L)).thenReturn(List.of());

        PostDTO result = postService.updatePost(1L, dto);

        assertEquals("test post", result.getContent());
    }

    @Test
    void updatePost_shouldThrow_whenPostNotFound() {
        PostCreateDTO dto = new PostCreateDTO();
        dto.setContent("updated content");

        when(postRepository.getById(1L)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.updatePost(1L, dto));
    }

    @Test
    void updatePost_shouldThrow_whenUnauthorized() {
        PostCreateDTO dto = new PostCreateDTO();
        dto.setContent("updated content");

        User anotherUser = new User();
        anotherUser.setId(UUID.randomUUID());
        post.setAuthor(anotherUser);

        when(postRepository.getById(1L)).thenReturn(Optional.of(post));
        when(authenticatedUserProvider.getCurrentUserId()).thenReturn(userId);
        when(authenticatedUserProvider.hasAdminRole()).thenReturn(false);

        assertThrows(RuntimeException.class, () -> postService.updatePost(1L, dto));
    }

    @Test
    void deletePost_shouldCallAllDeleteMethods() {
        when(postRepository.getById(1L)).thenReturn(Optional.of(post));
        when(authenticatedUserProvider.getCurrentUserId()).thenReturn(userId);
        when(authenticatedUserProvider.hasAdminRole()).thenReturn(false);

        when(commentService.getCommentsByPostId(1L)).thenReturn(List.of());

        postService.deletePost(1L);

        verify(postLikeService).deleteAllLikesForPost(1L);
        verify(commentService).getCommentsByPostId(1L);
        verify(postRepository).delete(post);
    }

    @Test
    void deletePost_shouldThrow_whenUnauthorized() {
        User anotherUser = new User();
        anotherUser.setId(UUID.randomUUID());
        post.setAuthor(anotherUser);

        when(postRepository.getById(1L)).thenReturn(Optional.of(post));
        when(authenticatedUserProvider.getCurrentUserId()).thenReturn(userId);
        when(authenticatedUserProvider.hasAdminRole()).thenReturn(false);

        assertThrows(RuntimeException.class, () -> postService.deletePost(1L));
    }

    @Test
    void likePost_shouldDelegateToLikeService() {
        postService.likePost(1L, userId);
        verify(postLikeService).likeOrUnlikePost(1L, userId);
    }

    @Test
    void getUsersWhoLikedPost_shouldReturnList() {
        List<UserDTO> userList = List.of(new UserDTO(), new UserDTO());
        when(postLikeService.getUsersWhoLikedPost(1L)).thenReturn(userList);

        List<UserDTO> result = postService.getUsersWhoLikedPost(1L);
        assertEquals(2, result.size());
    }

    @Test
    void findPostsByAuthorIdsAndDate_shouldReturnPosts() {
        List<UUID> authorIds = List.of(userId);
        LocalDateTime from = LocalDateTime.now().minusDays(5);
        LocalDateTime to = LocalDateTime.now();
        int limit = 10;

        when(postRepository.findPostsByAuthorIdsAndDate(authorIds, from, to, limit)).thenReturn(List.of(post));

        List<Post> results = postService.findPostsByAuthorIdsAndDate(authorIds, from, to, limit);
        assertEquals(1, results.size());
    }

    @Test
    void findRandomPostsExcludingUsers_shouldReturnPosts() {
        List<UUID> excludedUserIds = List.of(UUID.randomUUID());
        UUID currentUserId = userId;
        LocalDateTime from = LocalDateTime.now().minusDays(5);
        LocalDateTime to = LocalDateTime.now();
        int limit = 10;

        when(postRepository.findRandomPostsExcludingUsers(excludedUserIds, currentUserId, from, to, limit)).thenReturn(List.of(post));

        List<Post> results = postService.findRandomPostsExcludingUsers(excludedUserIds, currentUserId, from, to, limit);
        assertEquals(1, results.size());
    }
}

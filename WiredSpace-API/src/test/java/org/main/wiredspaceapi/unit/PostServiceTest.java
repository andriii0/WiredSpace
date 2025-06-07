package org.main.wiredspaceapi.unit;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.main.wiredspaceapi.business.impl.PostServiceImpl;
import org.main.wiredspaceapi.controller.dto.post.PostCreateDTO;
import org.main.wiredspaceapi.controller.dto.post.PostDTO;
import org.main.wiredspaceapi.controller.mapper.PostMapper;
import org.main.wiredspaceapi.domain.Post;
import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.persistence.PostRepository;
import org.main.wiredspaceapi.persistence.UserRepository;
import org.main.wiredspaceapi.security.util.AuthenticatedUserProvider;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostServiceTest {

    private PostRepository postRepository;
    private UserRepository userRepository;
    private PostMapper postMapper;
    private AuthenticatedUserProvider authenticatedUserProvider;
    private PostServiceImpl postService;

    @BeforeEach
    void setUp() {
        postRepository = mock(PostRepository.class);
        userRepository = mock(UserRepository.class);
        postMapper = mock(PostMapper.class);
        authenticatedUserProvider = mock(AuthenticatedUserProvider.class);
        postService = new PostServiceImpl(postRepository, userRepository, postMapper, authenticatedUserProvider);
    }

    @Test
    void createPost_shouldReturnPostDTO_whenUserExists() {
        UUID userId = UUID.randomUUID();
        String content = "New post";

        PostCreateDTO dto = new PostCreateDTO();
        dto.setContent(content);

        User author = new User();
        author.setId(userId);
        author.setName("Author");

        Post postEntity = Post.builder()
                .id(1L)
                .content(content)
                .createdAt(LocalDateTime.now())
                .author(author)
                .build();

        PostDTO postDto = new PostDTO();
        postDto.setId(1L);
        postDto.setContent(content);
        postDto.setAuthorId(userId);
        postDto.setLikedByUserIds(List.of());

        when(authenticatedUserProvider.getCurrentUserId()).thenReturn(userId);
        when(userRepository.getUserById(userId)).thenReturn(Optional.of(author));
        when(postMapper.postCreateDtoToPost(dto)).thenReturn(postEntity);
        when(postRepository.create(any(Post.class))).thenReturn(postEntity);
        when(postMapper.postToPostDto(postEntity)).thenReturn(postDto);
        when(postRepository.getUsersWhoLikedPost(1L)).thenReturn(List.of());

        PostDTO result = postService.createPost(dto);

        assertNotNull(result);
        assertEquals(content, result.getContent());
        assertEquals(userId, result.getAuthorId());

        verify(postRepository).create(any(Post.class));
    }

    @Test
    void createPost_shouldThrow_whenUserNotFound() {
        UUID userId = UUID.randomUUID();
        PostCreateDTO dto = new PostCreateDTO();
        dto.setContent("Orphan");

        when(authenticatedUserProvider.getCurrentUserId()).thenReturn(userId);
        when(userRepository.getUserById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> postService.createPost(dto));
    }

    @Test
    void getAllPosts_shouldReturnListOfPostDTOs() {
        UUID userId = UUID.randomUUID();
        User author = new User();
        author.setId(userId);
        author.setName("User");

        Post post1 = Post.builder().id(1L).content("First").createdAt(LocalDateTime.now()).author(author).build();
        Post post2 = Post.builder().id(2L).content("Second").createdAt(LocalDateTime.now()).author(author).build();

        PostDTO dto1 = new PostDTO();
        dto1.setId(1L);
        dto1.setContent("First");
        dto1.setAuthorId(userId);

        PostDTO dto2 = new PostDTO();
        dto2.setId(2L);
        dto2.setContent("Second");
        dto2.setAuthorId(userId);

        when(postRepository.getAll()).thenReturn(List.of(post1, post2));
        when(postMapper.postToPostDto(post1)).thenReturn(dto1);
        when(postMapper.postToPostDto(post2)).thenReturn(dto2);
        when(postRepository.getUsersWhoLikedPost(1L)).thenReturn(List.of());
        when(postRepository.getUsersWhoLikedPost(2L)).thenReturn(List.of());

        List<PostDTO> result = postService.getAllPosts();

        assertEquals(2, result.size());
        assertEquals("First", result.get(0).getContent());
        assertEquals("Second", result.get(1).getContent());
    }

    @Test
    void getPostById_shouldReturnPostDTO_whenFound() {
        Long id = 99L;
        User author = new User();
        author.setId(UUID.randomUUID());
        author.setName("Test Author");

        Post post = Post.builder()
                .id(1L)
                .content("Sample content")
                .createdAt(LocalDateTime.now())
                .author(author)
                .build();


        PostDTO dto = new PostDTO();
        dto.setId(id);
        dto.setContent("My post");

        when(postRepository.getById(id)).thenReturn(Optional.of(post));
        when(postMapper.postToPostDto(post)).thenReturn(dto);
        when(postRepository.getUsersWhoLikedPost(id)).thenReturn(List.of());

        PostDTO result = postService.getPostById(id);

        assertNotNull(result);
        assertEquals("My post", result.getContent());
    }

    @Test
    void getPostById_shouldThrow_whenNotFound() {
        when(postRepository.getById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> postService.getPostById(1L));
    }
    @Test
    void updatePost_shouldUpdateAndReturnPost_whenAuthorized() {
        UUID userId = UUID.randomUUID();
        Long postId = 1L;

        User author = new User();
        author.setId(userId);

        Post existingPost = Post.builder()
                .id(postId)
                .content("Old Content")
                .createdAt(LocalDateTime.now())
                .author(author)
                .build();

        PostCreateDTO updateDto = new PostCreateDTO();
        updateDto.setContent("Updated Content");

        Post updatedPost = Post.builder()
                .id(postId)
                .content("Updated Content")
                .createdAt(LocalDateTime.now())
                .author(author)
                .build();

        PostDTO updatedDto = new PostDTO();
        updatedDto.setId(postId);
        updatedDto.setContent("Updated Content");
        updatedDto.setAuthorId(userId);
        updatedDto.setLikedByUserIds(List.of());

        when(postRepository.getById(postId)).thenReturn(Optional.of(existingPost));
        when(authenticatedUserProvider.getCurrentUserId()).thenReturn(userId);
        when(postRepository.update(any())).thenReturn(updatedPost);
        when(postMapper.postToPostDto(updatedPost)).thenReturn(updatedDto);
        when(postRepository.getUsersWhoLikedPost(postId)).thenReturn(List.of());

        PostDTO result = postService.updatePost(postId, updateDto);

        assertEquals("Updated Content", result.getContent());
        verify(postRepository).update(existingPost);
    }

    @Test
    void deletePost_shouldDelete_whenAuthorized() {
        UUID userId = UUID.randomUUID();
        Long postId = 1L;

        User author = new User();
        author.setId(userId);

        Post post = Post.builder()
                .id(postId)
                .content("To delete")
                .createdAt(LocalDateTime.now())
                .author(author)
                .build();

        when(postRepository.getById(postId)).thenReturn(Optional.of(post));
        when(authenticatedUserProvider.getCurrentUserId()).thenReturn(userId);

        postService.deletePost(postId);

        verify(postRepository).deleteById(postId);
    }

    @Test
    void likePost_shouldCallRepository_whenUserAndPostExist() {
        UUID userId = UUID.randomUUID();
        Long postId = 42L;

        when(postRepository.existsById(postId)).thenReturn(true);
        when(userRepository.getUserById(userId)).thenReturn(Optional.of(new User()));

        postService.likePost(postId, userId.toString());

        verify(postRepository).likePost(postId, userId);
    }

    @Test
    void unlikePost_shouldCallRepository_whenUserAndPostExist() {
        UUID userId = UUID.randomUUID();
        Long postId = 42L;

        when(postRepository.existsById(postId)).thenReturn(true);
        when(userRepository.getUserById(userId)).thenReturn(Optional.of(new User()));

        postService.unlikePost(postId, userId.toString());

        verify(postRepository).unlikePost(postId, userId);
    }

    @Test
    void getUsersWhoLikedPost_shouldReturnList_whenPostExists() {
        Long postId = 10L;
        List<UUID> likedUserIds = List.of(UUID.randomUUID(), UUID.randomUUID());

        when(postRepository.existsById(postId)).thenReturn(true);
        when(postRepository.getUsersWhoLikedPost(postId)).thenReturn(likedUserIds);

        List<String> result = postService.getUsersWhoLikedPost(postId);

        assertEquals(2, result.size());
        assertTrue(result.containsAll(likedUserIds.stream().map(UUID::toString).toList()));
    }

    @Test
    void getUsersWhoLikedPost_shouldThrow_whenPostNotExists() {
        Long postId = 999L;
        when(postRepository.existsById(postId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> postService.getUsersWhoLikedPost(postId));
    }

}

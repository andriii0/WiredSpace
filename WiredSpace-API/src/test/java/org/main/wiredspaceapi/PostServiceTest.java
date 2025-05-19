package org.main.wiredspaceapi;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.main.wiredspaceapi.business.impl.PostServiceImpl;
import org.main.wiredspaceapi.controller.dto.post.PostCreateDTO;
import org.main.wiredspaceapi.controller.dto.post.PostDTO;
import org.main.wiredspaceapi.domain.Post;
import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.persistence.PostRepository;
import org.main.wiredspaceapi.persistence.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostServiceTest {

    private PostRepository postRepository;
    private UserRepository userRepository;
    private PostServiceImpl postService;

    @BeforeEach
    void setUp() {
        postRepository = mock(PostRepository.class);
        userRepository = mock(UserRepository.class);
        postService = new PostServiceImpl(postRepository, userRepository);
    }

    @Test
    void createPost_shouldReturnPostDTO_whenUserExists() {
        // Arrange
        UUID authorId = UUID.randomUUID();
        String content = "Test post content";

        PostCreateDTO dto = new PostCreateDTO();
        dto.setAuthorId(authorId);
        dto.setContent(content);

        User author = new User();
        author.setId(authorId);
        author.setName("Test Author");

        Post post = Post.builder()
                .id(10L)
                .content(content)
                .createdAt(LocalDateTime.now())
                .author(author)
                .build();

        when(userRepository.getUserById(authorId)).thenReturn(Optional.of(author));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        // Act
        PostDTO result = postService.createPost(dto);

        // Assert
        assertNotNull(result);
        assertEquals(post.getContent(), result.getContent());
        assertEquals(author.getId(), result.getAuthorId());

        verify(userRepository).getUserById(authorId);
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void createPost_shouldThrowException_whenUserNotFound() {
        UUID invalidAuthorId = UUID.randomUUID();

        PostCreateDTO dto = new PostCreateDTO();
        dto.setAuthorId(invalidAuthorId);
        dto.setContent("Orphan post");

        when(userRepository.getUserById(invalidAuthorId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> postService.createPost(dto));
        verify(postRepository, never()).save(any());
    }

    @Test
    void getAllPosts_shouldReturnListOfPostDTOs() {
        User author = new User();
        author.setId(UUID.randomUUID());
        author.setName("Test User");

        Post p1 = Post.builder().id(1L).content("Content 1").createdAt(LocalDateTime.now()).author(author).build();
        Post p2 = Post.builder().id(2L).content("Content 2").createdAt(LocalDateTime.now()).author(author).build();

        when(postRepository.findAll()).thenReturn(List.of(p1, p2));

        List<PostDTO> result = postService.getAllPosts();

        assertEquals(2, result.size());
        assertEquals("Content 1", result.get(0).getContent());
        assertEquals("Content 2", result.get(1).getContent());

        verify(postRepository).findAll();
    }

    @Test
    void getPostById_shouldReturnPostDTO_whenFound() {
        Long postId = 123L;
        User author = new User();
        author.setId(UUID.randomUUID());
        author.setName("Author X");

        Post post = Post.builder().id(postId).content("Some post").createdAt(LocalDateTime.now()).author(author).build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        PostDTO result = postService.getPostById(postId);

        assertNotNull(result);
        assertEquals(post.getContent(), result.getContent());
        assertEquals(author.getId(), result.getAuthorId());

        verify(postRepository).findById(postId);
    }

    @Test
    void getPostById_shouldThrowException_whenNotFound() {
        Long postId = 404L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> postService.getPostById(postId));
        verify(postRepository).findById(postId);
    }
}

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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PostServiceTest {

    private PostRepository postRepository;
    private UserRepository userRepository;
    private PostServiceImpl postService;

    @BeforeEach
    public void setUp() {
        postRepository = mock(PostRepository.class);
        userRepository = mock(UserRepository.class);
        postService = new PostServiceImpl(postRepository, userRepository);
    }

    @Test
    public void testCreatePost_Success() {
        PostCreateDTO dto = new PostCreateDTO();
        dto.setContent("Test post");
        dto.setAuthorId(1L);

        User user = new User();
        user.setId(1L);
        user.setName("Test User");

        Post post = Post.builder()
                .id(1L)
                .content("Test post")
                .createdAt(LocalDateTime.now())
                .author(user)
                .build();

        when(userRepository.getUserById(1L)).thenReturn(Optional.of(user));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        PostDTO result = postService.createPost(dto);

        assertNotNull(result);
        assertEquals("Test post", result.getContent());
        assertEquals("Test User", result.getAuthorName());
    }

    @Test
    public void testCreatePost_UserNotFound() {
        PostCreateDTO dto = new PostCreateDTO();
        dto.setContent("Test post");
        dto.setAuthorId(99L);

        when(userRepository.getUserById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> postService.createPost(dto));
    }

    @Test
    public void testGetAllPosts() {
        User user = new User();
        user.setName("User A");

        Post post1 = Post.builder().id(1L).content("First post").createdAt(LocalDateTime.now()).author(user).build();
        Post post2 = Post.builder().id(2L).content("Second post").createdAt(LocalDateTime.now()).author(user).build();

        when(postRepository.findAll()).thenReturn(List.of(post1, post2));

        List<PostDTO> results = postService.getAllPosts();

        assertEquals(2, results.size());
        assertEquals("First post", results.get(0).getContent());
        assertEquals("Second post", results.get(1).getContent());
    }

    @Test
    public void testGetPostById_Success() {
        User user = new User();
        user.setName("User X");

        Post post = Post.builder().id(1L).content("Post by ID").createdAt(LocalDateTime.now()).author(user).build();

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        PostDTO result = postService.getPostById(1L);

        assertNotNull(result);
        assertEquals("Post by ID", result.getContent());
        assertEquals("User X", result.getAuthorName());
    }

    @Test
    public void testGetPostById_NotFound() {
        when(postRepository.findById(42L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> postService.getPostById(42L));
    }
}

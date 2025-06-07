package org.main.wiredspaceapi.unit;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.main.wiredspaceapi.business.impl.CommentServiceImpl;
import org.main.wiredspaceapi.domain.Comment;
import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.persistence.CommentRepository;
import org.main.wiredspaceapi.persistence.PostRepository;
import org.main.wiredspaceapi.persistence.UserRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock private CommentRepository commentRepository;
    @Mock private PostRepository postRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private CommentServiceImpl commentService;

    private Comment comment;

    @BeforeEach
    void setUp() {
        comment = new Comment();
        comment.setId(1L);
        comment.setPostId(100L);
        comment.setAuthorId(java.util.UUID.randomUUID());
        comment.setContent("Test comment");
        comment.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void createComment_shouldSucceed_whenPostAndUserExist() {
        when(postRepository.existsById(100L)).thenReturn(true);
        when(userRepository.getUserById(comment.getAuthorId()))
                .thenReturn(Optional.of(new User()));
        when(commentRepository.create(any(Comment.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Comment created = commentService.createComment(comment);

        assertNotNull(created.getCreatedAt());
        assertEquals("Test comment", created.getContent());
        verify(commentRepository).create(any(Comment.class));
    }

    @Test
    void createComment_shouldThrow_whenPostNotFound() {
        when(postRepository.existsById(100L)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> commentService.createComment(comment));
    }

    @Test
    void createComment_shouldThrow_whenUserNotFound() {
        when(postRepository.existsById(100L)).thenReturn(true);
        when(userRepository.getUserById(comment.getAuthorId()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> commentService.createComment(comment));
    }

    @Test
    void updateComment_shouldUpdateContent_whenCommentExists() {
        when(commentRepository.getById(1L)).thenReturn(Optional.of(comment));
        when(commentRepository.update(any())).thenAnswer(inv -> inv.getArgument(0));

        Comment updated = commentService.updateComment(1L, "Updated content");

        assertEquals("Updated content", updated.getContent());
        verify(commentRepository).update(any());
    }

    @Test
    void updateComment_shouldThrow_whenCommentNotFound() {
        when(commentRepository.getById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> commentService.updateComment(1L, "New content"));
    }

    @Test
    void deleteComment_shouldCallDelete_whenExists() {
        when(commentRepository.existsById(1L)).thenReturn(true);
        doNothing().when(commentRepository).deleteById(1L);

        assertDoesNotThrow(() -> commentService.deleteComment(1L));
        verify(commentRepository).deleteById(1L);
    }

    @Test
    void deleteComment_shouldThrow_whenCommentNotFound() {
        when(commentRepository.existsById(1L)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> commentService.deleteComment(1L));
    }

    @Test
    void getCommentById_shouldReturnComment_whenExists() {
        when(commentRepository.getById(1L)).thenReturn(Optional.of(comment));

        Comment result = commentService.getCommentById(1L);
        assertEquals("Test comment", result.getContent());
    }

    @Test
    void getCommentById_shouldThrow_whenNotFound() {
        when(commentRepository.getById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> commentService.getCommentById(1L));
    }

    @Test
    void getAllComments_shouldReturnList() {
        when(commentRepository.getAll()).thenReturn(List.of(comment));
        List<Comment> result = commentService.getAllComments();
        assertEquals(1, result.size());
        verify(commentRepository).getAll();
    }

    @Test
    void getCommentsByPostId_shouldReturnComments_whenPostExists() {
        when(postRepository.existsById(100L)).thenReturn(true);
        when(commentRepository.getByPostId(100L)).thenReturn(List.of(comment));

        List<Comment> comments = commentService.getCommentsByPostId(100L);
        assertEquals(1, comments.size());
        verify(commentRepository).getByPostId(100L);
    }

    @Test
    void getCommentsByPostId_shouldThrow_whenPostNotFound() {
        when(postRepository.existsById(100L)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> commentService.getCommentsByPostId(100L));
    }
}

package org.main.wiredspaceapi.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.main.wiredspaceapi.business.impl.CommentServiceImpl;
import org.main.wiredspaceapi.controller.dto.post.CommentDTO;
import org.main.wiredspaceapi.controller.exceptions.*;
import org.main.wiredspaceapi.controller.mapper.CommentMapper;
import org.main.wiredspaceapi.domain.Comment;
import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.persistence.CommentRepository;
import org.main.wiredspaceapi.persistence.PostRepository;
import org.main.wiredspaceapi.persistence.UserRepository;
import org.main.wiredspaceapi.business.impl.UserStatisticsService;
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
class CommentServiceTest {

    @Mock private CommentRepository commentRepository;
    @Mock private PostRepository postRepository;
    @Mock private UserRepository userRepository;
    @Mock private CommentMapper commentMapper;
    @Mock private UserStatisticsService userStatisticsService;

    @InjectMocks private CommentServiceImpl commentService;

    private Comment comment;
    private User user;
    private CommentDTO commentDTO;

    @BeforeEach
    void setUp() {
        UUID authorId = UUID.randomUUID();

        comment = new Comment();
        comment.setId(1L);
        comment.setPostId(100L);
        comment.setAuthorId(authorId);
        comment.setContent("Test comment");

        user = new User();
        user.setId(authorId);
        user.setName("John");

        commentDTO = new CommentDTO();
        commentDTO.setId(1L);
        commentDTO.setContent("Test comment");
        commentDTO.setAuthorName("John");
    }

    @Test
    void createComment_shouldReturnDto_whenPostAndUserExist() {
        when(postRepository.existsById(100L)).thenReturn(true);
        when(userRepository.getUserById(comment.getAuthorId())).thenReturn(Optional.of(user));
        when(commentRepository.create(any(Comment.class))).thenAnswer(inv -> {
            Comment c = inv.getArgument(0);
            c.setCreatedAt(LocalDateTime.now());
            return c;
        });
        when(commentMapper.toDto(any())).thenReturn(commentDTO);

        CommentDTO result = commentService.createComment(comment);

        assertNotNull(result);
        assertEquals("Test comment", result.getContent());
        assertEquals("John", result.getAuthorName());
        verify(userStatisticsService).incrementComments(user.getId());
    }

    @Test
    void createComment_shouldThrow_whenPostNotFound() {
        when(postRepository.existsById(100L)).thenReturn(false);

        assertThrows(PostNotFoundException.class, () -> commentService.createComment(comment));
    }

    @Test
    void createComment_shouldThrow_whenUserNotFound() {
        when(postRepository.existsById(100L)).thenReturn(true);
        when(userRepository.getUserById(comment.getAuthorId())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> commentService.createComment(comment));
    }

    @Test
    void createComment_shouldThrow_whenContentInvalid() {
        comment.setContent("   ");
        when(postRepository.existsById(comment.getPostId())).thenReturn(true);

        assertThrows(InvalidCommentContentException.class, () -> commentService.createComment(comment));
    }

    @Test
    void updateComment_shouldReturnUpdated_whenFound() {
        when(commentRepository.getById(1L)).thenReturn(Optional.of(comment));
        when(commentRepository.update(any())).thenAnswer(inv -> inv.getArgument(0));

        Comment result = commentService.updateComment(1L, "Updated content");

        assertEquals("Updated content", result.getContent());
        verify(commentRepository).update(any());
    }

    @Test
    void updateComment_shouldThrow_whenNotFound() {
        when(commentRepository.getById(1L)).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () -> commentService.updateComment(1L, "Something"));
    }

    @Test
    void updateComment_shouldThrow_whenContentInvalid() {
        when(commentRepository.getById(1L)).thenReturn(Optional.of(comment));

        assertThrows(InvalidCommentContentException.class, () -> commentService.updateComment(1L, ""));
    }

    @Test
    void deleteComment_shouldSucceed_whenCommentExists() {
        when(commentRepository.getById(1L)).thenReturn(Optional.of(comment));

        commentService.deleteComment(1L);

        verify(commentRepository).deleteById(1L);
        verify(userStatisticsService).decrementComments(comment.getAuthorId());
    }

    @Test
    void deleteComment_shouldThrow_whenCommentNotFound() {
        when(commentRepository.getById(1L)).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () -> commentService.deleteComment(1L));
    }

    @Test
    void getCommentById_shouldReturnComment_whenExists() {
        when(commentRepository.getById(1L)).thenReturn(Optional.of(comment));

        Comment result = commentService.getCommentById(1L);

        assertEquals(comment.getContent(), result.getContent());
    }

    @Test
    void getCommentById_shouldThrow_whenNotFound() {
        when(commentRepository.getById(1L)).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () -> commentService.getCommentById(1L));
    }

    @Test
    void getAllComments_shouldReturnList() {
        when(commentRepository.getAll()).thenReturn(List.of(comment));

        List<Comment> result = commentService.getAllComments();

        assertEquals(1, result.size());
    }

    @Test
    void getCommentsByPostId_shouldReturnList_whenPostExists() {
        when(postRepository.existsById(100L)).thenReturn(true);
        when(commentRepository.getByPostId(100L)).thenReturn(List.of(comment));

        List<Comment> result = commentService.getCommentsByPostId(100L);

        assertEquals(1, result.size());
    }

    @Test
    void getCommentsByPostId_shouldThrow_whenPostNotFound() {
        when(postRepository.existsById(100L)).thenReturn(false);

        assertThrows(PostNotFoundException.class, () -> commentService.getCommentsByPostId(100L));
    }
}

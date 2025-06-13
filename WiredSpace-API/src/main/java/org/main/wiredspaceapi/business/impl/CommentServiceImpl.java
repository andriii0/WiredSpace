package org.main.wiredspaceapi.business.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.business.CommentService;
import org.main.wiredspaceapi.controller.dto.post.CommentDTO;
import org.main.wiredspaceapi.controller.mapper.CommentMapper;
import org.main.wiredspaceapi.domain.Comment;
import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.persistence.CommentRepository;
import org.main.wiredspaceapi.persistence.PostRepository;
import org.main.wiredspaceapi.persistence.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;
    private final UserStatisticsService userStatisticsService;

    public CommentDTO createComment(Comment comment) {
        if (!postRepository.existsById(comment.getPostId())) {
            throw new EntityNotFoundException("Post not found with id: " + comment.getPostId());
        }

        validateCommentContent(comment.getContent());

        User user = userRepository.getUserById(comment.getAuthorId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + comment.getAuthorId()));

        comment.setCreatedAt(LocalDateTime.now());
        Comment savedComment = commentRepository.create(comment);

        userStatisticsService.incrementComments(user.getId());

        CommentDTO dto = commentMapper.toDto(savedComment);
        dto.setAuthorName(user.getName());

        return dto;
    }

    @Override
    public Comment updateComment(Long commentId, String content) {
        Comment existing = commentRepository.getById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + commentId));

        validateCommentContent(content);

        existing.setContent(content);
        return commentRepository.update(existing);
    }

    @Override
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.getById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + commentId));

        commentRepository.deleteById(commentId);

        userStatisticsService.decrementComments(comment.getAuthorId());
    }

    @Override
    public Comment getCommentById(Long commentId) {
        return commentRepository.getById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + commentId));
    }

    @Override
    public List<Comment> getAllComments() {
        return commentRepository.getAll();
    }

    @Override
    public List<Comment> getCommentsByPostId(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException("Post not found with id: " + postId);
        }
        return commentRepository.getByPostId(postId);
    }
    private void validateCommentContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment content must not be empty");
        }
        if (content.length() > 500) {
            throw new IllegalArgumentException("Comment content exceeds 500 characters");
        }
    }

}

package org.main.wiredspaceapi.business.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.business.CommentService;
import org.main.wiredspaceapi.domain.Comment;
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

    @Override
    public Comment createComment(Comment comment) {
        if (!postRepository.existsById(comment.getPostId())) {
            throw new EntityNotFoundException("Post not found with id: " + comment.getPostId());
        }
        if (userRepository.getUserById(comment.getAuthorId()).isEmpty()) {
            throw new EntityNotFoundException("User not found with id: " + comment.getAuthorId());
        }
        comment.setCreatedAt(LocalDateTime.now());
        return commentRepository.create(comment);
    }

    @Override
    public Comment updateComment(Long commentId, String content) {
        Comment existing = commentRepository.getById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + commentId));
        existing.setContent(content);
        return commentRepository.update(existing);
    }

    @Override
    public void deleteComment(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new EntityNotFoundException("Comment not found with id: " + commentId);
        }
        commentRepository.deleteById(commentId);
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
}

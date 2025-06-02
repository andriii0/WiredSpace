package org.main.wiredspaceapi.persistence;

import org.main.wiredspaceapi.domain.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {
    Comment create(Comment comment);
    Optional<Comment> getById(Long id);
    List<Comment> getAll();
    List<Comment> getByPostId(Long postId);
    Comment update(Comment comment);
    void deleteById(Long id);
    boolean existsById(Long id);
}

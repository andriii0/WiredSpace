package org.main.wiredspaceapi.business;

import org.main.wiredspaceapi.domain.Comment;

import java.util.List;

public interface CommentService {
    Comment createComment(Comment comment);
    Comment updateComment(Long commentId, String content);
    void deleteComment(Long commentId);
    Comment getCommentById(Long commentId);
    List<Comment> getAllComments();
    List<Comment> getCommentsByPostId(Long postId);
}

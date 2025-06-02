package org.main.wiredspaceapi.persistence.impl.post;

import org.main.wiredspaceapi.persistence.entity.PostCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentDB extends JpaRepository<PostCommentEntity, Long> {
    List<PostCommentEntity> findAllByPostId(Long postId);
}

package org.main.wiredspaceapi.persistence.impl.post;

import org.main.wiredspaceapi.persistence.entity.CompositeKey.PostLikeId;
import org.main.wiredspaceapi.persistence.entity.PostLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostLikeDB extends JpaRepository<PostLikeEntity, PostLikeId> {
    boolean existsById(PostLikeId id);
    void deleteById(PostLikeId id);
    List<PostLikeEntity> findAllByPostId(Long postId);
}
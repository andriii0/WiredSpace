package org.main.wiredspaceapi.persistence.impl.post;

import org.main.wiredspaceapi.persistence.entity.CompositeKey.PostLikeId;
import org.main.wiredspaceapi.persistence.entity.PostLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface PostLikeDB extends JpaRepository<PostLikeEntity, PostLikeId> {
    boolean existsById(PostLikeId id);

    @Modifying
    @Transactional
    @Query("DELETE FROM PostLikeEntity pl WHERE pl.id.postId = :postId AND pl.id.userId = :userId")
    void deleteByPostIdAndUserId(@Param("postId") Long postId, @Param("userId") UUID userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM PostLikeEntity pl WHERE pl.post.id = :postId")

    void deleteAllLikesForPost(@Param("postId") Long postId);
    List<PostLikeEntity> findAllByPostId(Long postId);
    List<PostLikeEntity> findAllByUser_Id(UUID userId);

    void deleteAllByUser_Id(UUID userId);

    @Query("SELECT COUNT(l) > 0 FROM PostLikeEntity l WHERE l.post.id = :postId AND l.user.id = :userId")
    boolean hasUserLikedPost(@Param("postId") Long postId, @Param("userId") UUID userId);
}
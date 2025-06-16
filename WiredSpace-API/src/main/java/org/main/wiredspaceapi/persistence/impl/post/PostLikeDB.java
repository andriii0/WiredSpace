package org.main.wiredspaceapi.persistence.impl.post;

import org.main.wiredspaceapi.persistence.entity.PostLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface PostLikeDB extends JpaRepository<PostLikeEntity, Long> {

    boolean existsById(Long id);

    @Modifying
    @Transactional
    @Query("DELETE FROM PostLikeEntity pl WHERE pl.post.id = :postId AND pl.user.id = :userId")
    void deleteByPostIdAndUserId(@Param("postId") Long postId, @Param("userId") UUID userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM PostLikeEntity pl WHERE pl.post.id = :postId")
    void deleteAllLikesForPost(@Param("postId") Long postId);

    List<PostLikeEntity> findAllByPost_Id(Long postId);

    List<PostLikeEntity> findAllByUser_Id(UUID userId);

    void deleteAllByUser_Id(UUID userId);

    @Query("SELECT COUNT(pl) > 0 FROM PostLikeEntity pl WHERE pl.post.id = :postId AND pl.user.id = :userId")
    boolean hasUserLikedPost(@Param("postId") Long postId, @Param("userId") UUID userId);

    @Query("SELECT pl.post.id FROM PostLikeEntity pl WHERE pl.user.id = :userId AND pl.post.id IN :postIds")
    Set<Long> findLikedPostIds(@Param("userId") UUID userId, @Param("postIds") List<Long> postIds);
}

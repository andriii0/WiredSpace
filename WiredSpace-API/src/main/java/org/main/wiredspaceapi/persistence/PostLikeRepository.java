package org.main.wiredspaceapi.persistence;

import org.main.wiredspaceapi.persistence.entity.PostLikeEntity;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface PostLikeRepository {
    boolean hasUserLikedPost(Long postId, UUID userId);

    void likePost(Long postId, UUID userId);

    void unlikePost(Long postId, UUID userId);

    List<UUID> getUsersWhoLikedPost(Long postId);

    List<PostLikeEntity> getLikedPostEntitiesByUserId(UUID userId);

    void deleteAllLikesForPost(Long postId);

    void deleteAllLikesByUserId(UUID userId);

    Set<Long> findLikedPostIds(UUID userId, List<Long> postIds);
}

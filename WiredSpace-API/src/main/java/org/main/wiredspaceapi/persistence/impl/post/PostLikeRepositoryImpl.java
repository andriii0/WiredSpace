package org.main.wiredspaceapi.persistence.impl.post;

import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.persistence.PostLikeRepository;
import org.main.wiredspaceapi.persistence.entity.PostEntity;
import org.main.wiredspaceapi.persistence.entity.PostLikeEntity;
import org.main.wiredspaceapi.persistence.entity.UserEntity;
import org.main.wiredspaceapi.persistence.impl.user.UserDB;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PostLikeRepositoryImpl implements PostLikeRepository {

    private final PostLikeDB postLikeDB;
    private final PostDB postDB;
    private final UserDB userDB;

    @Override
    public boolean hasUserLikedPost(Long postId, UUID userId) {
        return postLikeDB.hasUserLikedPost(postId, userId);
    }

    @Override
    public void likePost(Long postId, UUID userId) {
        if (postLikeDB.hasUserLikedPost(postId, userId)) return;

        PostEntity post = postDB.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        UserEntity user = userDB.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        PostLikeEntity like = PostLikeEntity.builder()
                .post(post)
                .user(user)
                .likedAt(LocalDateTime.now())
                .build();

        postLikeDB.save(like);
    }

    @Override
    public void unlikePost(Long postId, UUID userId) {
        postLikeDB.deleteByPostIdAndUserId(postId, userId);
    }

    @Override
    public List<UUID> getUsersWhoLikedPost(Long postId) {
        return postLikeDB.findAllByPost_Id(postId)
                .stream()
                .map(like -> like.getUser().getId())
                .toList();
    }

    @Override
    public List<PostLikeEntity> getLikedPostEntitiesByUserId(UUID userId) {
        return postLikeDB.findAllByUser_Id(userId);
    }

    @Override
    public void deleteAllLikesForPost(Long postId) {
        postLikeDB.deleteAllLikesForPost(postId);
    }

    @Override
    public void deleteAllLikesByUserId(UUID userId) {
        postLikeDB.deleteAllByUser_Id(userId);
    }

    @Override
    public Set<Long> findLikedPostIds(UUID userId, List<Long> postIds){
        return postLikeDB.findLikedPostIds(userId, postIds);
    }

}

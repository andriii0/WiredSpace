package org.main.wiredspaceapi.persistence.impl.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.main.wiredspaceapi.domain.Post;
import org.main.wiredspaceapi.persistence.PostRepository;
import org.main.wiredspaceapi.persistence.entity.CompositeKey.PostLikeId;
import org.main.wiredspaceapi.persistence.entity.PostEntity;
import org.main.wiredspaceapi.persistence.entity.PostLikeEntity;
import org.main.wiredspaceapi.persistence.entity.UserEntity;
import org.main.wiredspaceapi.persistence.impl.user.UserDB;
import org.main.wiredspaceapi.persistence.mapper.PostEntityMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Transactional
public class PostRepositoryImpl implements PostRepository {

    private final PostEntityMapper postEntityMapper;
    private final PostLikeDB postLikeDB;
    private final PostDB postDB;
    private final UserDB userDB;

    @Override
    public Post create(Post post) {
        return postEntityMapper.toDomain(postDB.save(postEntityMapper.toEntity(post)));
    }

    @Override
    public Post update(Post post) {
        return postEntityMapper.toDomain(postDB.save(postEntityMapper.toEntity(post)));
    }

    @Override
    public Optional<Post> getById(Long id) {
        return postDB.findById(id)
                .map(postEntityMapper::toDomain);
    }

    @Override
    public List<Post> getAll() {
        return postDB.findAll()
                .stream()
                .map(postEntityMapper::toDomain)
                .toList();
    }

    @Override
    public void delete(Post post) {
        PostEntity postEntity = postEntityMapper.toEntity(post);
        postDB.delete(postEntity);
    }


    @Override
    public boolean existsById(Long id) {
        return postDB.existsById(id);
    }

    @Override
    public boolean hasUserLikedPost(Long postId, UUID userId) {
        return postLikeDB.hasUserLikedPost(postId, userId);
    }

    @Override
    public void likePost(Long postId, UUID userId) {
        PostLikeId likeId = new PostLikeId(postId, userId);
        if (postLikeDB.existsById(likeId)) return;

        PostEntity post = postDB.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        UserEntity user = userDB.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        PostLikeEntity like = PostLikeEntity.builder()
                .id(likeId)
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
        return postLikeDB.findAllByPostId(postId)
                .stream()
                .map(like -> like.getUser().getId())
                .toList();
    }

    @Override
    public List<Post> getAllByUserId(UUID userId) {
        return postDB.findAllByAuthor_Id(userId)
                .stream()
                .map(postEntityMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteAllByUserId(UUID userId) {
        postDB.deleteAllByAuthor_Id(userId);
    }

    @Override
    public List<Post> getPostsByUserId(UUID userId) {
        return postDB.findPostsWithCommentsByAuthorId(userId)
                .stream()
                .map(postEntityMapper::toDomain)
                .toList();
    }

    public void deleteAllLikesForPost(Long postId){
        postLikeDB.deleteAllLikesForPost(postId);
    }

    @Override
    public List<Post> getLikedPostsByUserId(UUID userId) {
        return postLikeDB.findAllByUser_Id(userId)
                .stream()
                .map(PostLikeEntity::getPost)
                .map(postEntityMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteAllLikesByUserId(UUID userId) {
        postLikeDB.deleteAllByUser_Id(userId);
    }

}

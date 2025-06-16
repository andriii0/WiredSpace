package org.main.wiredspaceapi.persistence.impl.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.main.wiredspaceapi.domain.Post;
import org.main.wiredspaceapi.persistence.PostLikeRepository;
import org.main.wiredspaceapi.persistence.PostRepository;
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
public class PostRepositoryImpl implements PostRepository {

    private final PostEntityMapper postEntityMapper;
    private final PostDB postDB;

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
        postDB.deletePostById(post.getId());
    }

    @Override
    public boolean existsById(Long id) {
        return postDB.existsById(id);
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
}

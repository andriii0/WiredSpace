package org.main.wiredspaceapi.persistence.impl.post;

import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.domain.Post;
import org.main.wiredspaceapi.persistence.PostRepository;
import org.main.wiredspaceapi.persistence.mapper.PostEntityMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository {

    private final PostDB postDB;
    private final PostEntityMapper postEntityMapper;

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
    public void deleteById(Long id) {
        postDB.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return postDB.existsById(id);
    }
}

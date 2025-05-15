package org.main.wiredspaceapi.persistence.impl.post;

import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.domain.Post;
import org.main.wiredspaceapi.persistence.PostRepository;
import org.main.wiredspaceapi.persistence.entity.PostEntity;
import org.main.wiredspaceapi.persistence.mapper.PostMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository {

    private final PostDB postDB;
    private final PostMapper postMapper;

    @Override
    public Post save(Post post) {
        PostEntity saved = postDB.save(postMapper.toPostEntity(post));
        return postMapper.toEntity(saved);
    }

    @Override
    public List<Post> findAll() {
        return postDB.findAll().stream()
                .map(postMapper::toEntity)
                .toList();
    }

    @Override
    public Optional<Post> findById(Long id) {
        return postDB.findById(id)
                .map(postMapper::toEntity);
    }
}

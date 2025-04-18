package org.main.wiredspaceapi.persistence.impl.post;

import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.domain.Post;
import org.main.wiredspaceapi.persistence.PostRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository {

    private final PostDB postDB;

    @Override
    public Post save(Post post) {
        return postDB.save(post);
    }

    @Override
    public List<Post> findAll() {
        return postDB.findAll();
    }

    @Override
    public Optional<Post> findById(Long id) {
        return postDB.findById(id);
    }
}

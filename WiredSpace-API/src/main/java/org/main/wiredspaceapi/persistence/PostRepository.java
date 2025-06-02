package org.main.wiredspaceapi.persistence;

import org.main.wiredspaceapi.domain.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    Post create(Post post);
    Post update(Post post);
    Optional<Post> getById(Long id);
    List<Post> getAll();
    void deleteById(Long id);
    boolean existsById(Long id);
}

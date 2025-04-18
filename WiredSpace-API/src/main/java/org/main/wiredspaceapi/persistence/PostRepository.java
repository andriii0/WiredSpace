package org.main.wiredspaceapi.persistence;

import org.main.wiredspaceapi.domain.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    Post save(Post post);
    Optional<Post> findById(Long id);
    List<Post> findAll();
}

package org.main.wiredspaceapi.persistence;

import org.main.wiredspaceapi.domain.Post;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostRepository {
    Post create(Post post);
    Post update(Post post);
    Optional<Post> getById(Long id);
    List<Post> getAll();
    void deleteById(Long id);
    boolean existsById(Long id);

    void likePost(Long postId, UUID userId);
    void unlikePost(Long postId, UUID userId);
    List<UUID> getUsersWhoLikedPost(Long postId);
}

package org.main.wiredspaceapi.persistence;

import org.main.wiredspaceapi.domain.Post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostRepository {
    Post create(Post post);
    Post update(Post post);
    Optional<Post> getById(Long id);
    List<Post> getAll();
    void delete(Post post);
    boolean existsById(Long id);


    List<Post> getAllByUserId(UUID userId);
    void deleteAllByUserId(UUID userId);
    List<Post> getPostsByUserId(UUID userId);

    List<Post> findPostsByAuthorIdsAndDate(List<UUID> authorIds, LocalDateTime from, LocalDateTime to);
    List<Post> findRandomPostsExcludingUsers(List<UUID> excludedUserIds, UUID currentUserId, LocalDateTime from, LocalDateTime to, int limit);
}

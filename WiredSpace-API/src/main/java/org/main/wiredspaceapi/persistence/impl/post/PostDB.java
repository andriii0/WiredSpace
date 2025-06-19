package org.main.wiredspaceapi.persistence.impl.post;

import org.main.wiredspaceapi.domain.Post;
import org.main.wiredspaceapi.persistence.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PostDB extends JpaRepository<PostEntity, Long> {
    List<PostEntity> findAllByAuthor_Id(UUID authorId);

    @Query("select p from PostEntity p left join fetch p.comments c left join fetch c.author where p.author.id = :authorId")
    List<PostEntity> findPostsWithCommentsByAuthorId(@Param("authorId") UUID authorId);
    void deleteAllByAuthor_Id(UUID authorId);
    List<PostEntity> getPostsByAuthor_Id(UUID authorId);
    @Modifying
    @Transactional
    @Query("DELETE FROM PostEntity p WHERE p.id = :id")
    void deletePostById(@Param("id") Long id);

    @Query("""
    SELECT p FROM PostEntity p
    WHERE p.author.id IN :authorIds
      AND p.createdAt BETWEEN :from AND :to
    ORDER BY p.createdAt DESC
""")
    List<PostEntity> findPostsByAuthorIdsAndDate(
            @Param("authorIds") List<UUID> authorIds,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            org.springframework.data.domain.Pageable pageable
    );

    @Query(value = """
    SELECT * FROM posts p
    WHERE p.user_id NOT IN (:excludedUserIds)
      AND p.user_id != :currentUserId
      AND p.created_at BETWEEN :from AND :to
    LIMIT :limit OFFSET :offset
""", nativeQuery = true)
    List<PostEntity> findPostsExcludingUsersWithOffset(
            @Param("excludedUserIds") List<UUID> excludedUserIds,
            @Param("currentUserId") UUID currentUserId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("limit") int limit,
            @Param("offset") int offset
    );
    @Query(value = """
    SELECT COUNT(*) FROM posts p
    WHERE p.user_id NOT IN (:excludedUserIds)
      AND p.user_id != :currentUserId
      AND p.created_at BETWEEN :from AND :to
""", nativeQuery = true)
    int countPostsExcludingUsers(
            @Param("excludedUserIds") List<UUID> excludedUserIds,
            @Param("currentUserId") UUID currentUserId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );
}

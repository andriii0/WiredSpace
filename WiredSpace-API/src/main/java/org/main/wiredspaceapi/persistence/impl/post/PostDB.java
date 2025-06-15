package org.main.wiredspaceapi.persistence.impl.post;

import org.main.wiredspaceapi.domain.Post;
import org.main.wiredspaceapi.persistence.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

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

}

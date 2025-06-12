package org.main.wiredspaceapi.persistence.impl.post;

import org.main.wiredspaceapi.domain.Post;
import org.main.wiredspaceapi.persistence.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PostDB extends JpaRepository<PostEntity, Long> {
    List<PostEntity> findAllByAuthor_Id(UUID authorId);

    void deleteAllByAuthor_Id(UUID authorId);
    List<PostEntity> getPostsByAuthor_Id(UUID authorId);
}

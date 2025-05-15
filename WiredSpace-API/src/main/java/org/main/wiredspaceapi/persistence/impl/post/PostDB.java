package org.main.wiredspaceapi.persistence.impl.post;

import org.main.wiredspaceapi.domain.Post;
import org.main.wiredspaceapi.persistence.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostDB extends JpaRepository<PostEntity, Long> {
}

package org.main.wiredspaceapi.persistence.impl.post;

import org.main.wiredspaceapi.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostDB extends JpaRepository<Post, Long> {
}

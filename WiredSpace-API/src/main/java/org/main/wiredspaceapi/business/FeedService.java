package org.main.wiredspaceapi.business;

import org.main.wiredspaceapi.domain.Post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface FeedService {
    List<Post> getSmartFeed(UUID currentUserId, int page, int size);
}
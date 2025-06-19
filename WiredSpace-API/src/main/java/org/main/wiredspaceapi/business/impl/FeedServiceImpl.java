package org.main.wiredspaceapi.business.impl;

import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.business.FeedService;
import org.main.wiredspaceapi.business.PostLikeService;
import org.main.wiredspaceapi.business.PostService;
import org.main.wiredspaceapi.controller.exceptions.NoMorePostsAvailableException;
import org.main.wiredspaceapi.domain.Post;
import org.main.wiredspaceapi.persistence.FriendshipRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final FriendshipRepository friendshipRepository;
    private final PostLikeService postLikeService;
    private final PostService postService;

    @Override
    public List<Post> getSmartFeed(UUID currentUserId, int page, int size) {
        LocalDateTime now = LocalDateTime.now();
        int targetCount = (page + 1) * size;
        Set<Long> collectedPostIds = new LinkedHashSet<>();
        List<Post> collectedPosts = new ArrayList<>();
        int daysBack = 0;

        List<UUID> friendIds = getFriendIds(currentUserId);

        if (!friendIds.isEmpty()) {
            while (collectedPostIds.size() < targetCount && daysBack <= 30) {
                LocalDateTime from = now.minusDays(daysBack).toLocalDate().atStartOfDay();
                LocalDateTime to = now.minusDays(daysBack - 1).toLocalDate().atStartOfDay();
                List<Post> friendPosts = postService.findPostsByAuthorIdsAndDate(friendIds, from, to, size * 2);

                for (Post post : friendPosts) {
                    if (collectedPostIds.add(post.getId())) {
                        collectedPosts.add(post);
                    }
                }

                daysBack++;
            }
        }

        if (collectedPostIds.size() < targetCount) {
            daysBack = 0;
            List<UUID> excludedUserIds = new ArrayList<>(friendIds);
            excludedUserIds.add(currentUserId);

            while (collectedPostIds.size() < targetCount && daysBack <= 30) {
                LocalDateTime from = now.minusDays(daysBack).toLocalDate().atStartOfDay();
                LocalDateTime to = now.minusDays(daysBack - 1).toLocalDate().atStartOfDay();

                List<Post> suggestedPosts = postService.findRandomPostsExcludingUsers(
                        excludedUserIds, currentUserId, from, to, size * 2
                );

                for (Post post : suggestedPosts) {
                    if (collectedPostIds.add(post.getId())) {
                        collectedPosts.add(post);
                    }
                }

                daysBack++;
            }
        }

        Set<Long> likedPostIds = postLikeService.findLikedPostIds(
                currentUserId,
                collectedPosts.stream().map(Post::getId).toList()
        );

        List<Post> filtered = collectedPosts.stream()
                .filter(post -> !likedPostIds.contains(post.getId()))
                .toList();

        int fromIndex = page * size;
        int toIndex = Math.min(filtered.size(), fromIndex + size);

        if (fromIndex >= filtered.size()) {
            throw new NoMorePostsAvailableException("No more posts available for user: " + currentUserId);
        }

        return filtered.subList(fromIndex, toIndex);
    }

    private List<UUID> getFriendIds(UUID currentUserId) {
        return friendshipRepository.findAllByUser(currentUserId).stream()
                .filter(f -> f.isAccepted() || f.getUserId().equals(currentUserId))
                .map(f -> f.getUserId().equals(currentUserId) ? f.getFriendId() : f.getUserId())
                .distinct()
                .toList();
    }
}

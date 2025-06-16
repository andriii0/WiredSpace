package org.main.wiredspaceapi.business.impl;

import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.business.FeedService;
import org.main.wiredspaceapi.business.PostLikeService;
import org.main.wiredspaceapi.business.PostService;
import org.main.wiredspaceapi.controller.exceptions.NoMorePostsAvailableException;
import org.main.wiredspaceapi.domain.Friendship;
import org.main.wiredspaceapi.domain.Post;
import org.main.wiredspaceapi.persistence.FriendshipRepository;
import org.main.wiredspaceapi.persistence.PostRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final FriendshipRepository friendshipRepository;
    private final PostLikeService postLikeService;
    private final PostService postService;

    @Override
    public List<Post> getSmartFeed(UUID currentUserId, int page, int size) {
        LocalDateTime now = LocalDateTime.now();
        List<UUID> friendIds = getFriendIds(currentUserId);
        List<Post> result = new ArrayList<>();
        int daysBack = 0;

        while (result.size() < (page + 1) * size && daysBack <= 30) {
            LocalDateTime from = now.minusDays(daysBack).toLocalDate().atStartOfDay();
            LocalDateTime to = now.minusDays(daysBack - 1).toLocalDate().atStartOfDay();
            result.addAll(getUnlikedFriendPosts(friendIds, currentUserId, from, to, size * 2));
            daysBack++;
        }

        if (result.size() < (page + 1) * size) {
            daysBack = 0;
            List<UUID> excludedUserIds = new ArrayList<>(friendIds);
            excludedUserIds.add(currentUserId);

            while (result.size() < (page + 1) * size && daysBack <= 30) {
                LocalDateTime from = now.minusDays(daysBack).toLocalDate().atStartOfDay();
                LocalDateTime to = now.minusDays(daysBack - 1).toLocalDate().atStartOfDay();
                result.addAll(getUnlikedSuggestedPosts(currentUserId, excludedUserIds, from, to, size * 2));  // Лимит добавлен
                daysBack++;
            }
        }

        int fromIndex = page * size;
        int toIndex = Math.min(result.size(), fromIndex + size);

        if (fromIndex >= result.size()) {
            throw new NoMorePostsAvailableException("No more posts available for user: " + currentUserId);
        }

        return result.subList(fromIndex, toIndex);
    }

    private List<UUID> getFriendIds(UUID currentUserId) {
        return friendshipRepository.findAllByUser(currentUserId).stream()
                .filter(f -> f.isAccepted() || f.getUserId().equals(currentUserId))
                .map(f -> f.getUserId().equals(currentUserId) ? f.getFriendId() : f.getUserId())
                .distinct()
                .toList();
    }

    private List<Post> getUnlikedFriendPosts(List<UUID> friendIds, UUID currentUserId, LocalDateTime from, LocalDateTime to, int limit) {
        List<Post> posts = postService.findPostsByAuthorIdsAndDate(friendIds, from, to, limit);

        List<Long> postIds = posts.stream()
                .map(Post::getId)
                .toList();

        Set<Long> likedPostIds = postLikeService.findLikedPostIds(currentUserId, postIds);

        return posts.stream()
                .filter(post -> !likedPostIds.contains(post.getId()))
                .toList();
    }

    private List<Post> getUnlikedSuggestedPosts(UUID currentUserId, List<UUID> excludedUserIds, LocalDateTime from, LocalDateTime to, int limit) {
        List<Post> posts = postService.findRandomPostsExcludingUsers(excludedUserIds, currentUserId, from, to, limit);

        List<Long> postIds = posts.stream()
                .map(Post::getId)
                .toList();

        Set<Long> likedPostIds = postLikeService.findLikedPostIds(currentUserId, postIds);

        return posts.stream()
                .filter(post -> !likedPostIds.contains(post.getId()))
                .toList();
    }
}

package org.main.wiredspaceapi.business.impl;

import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.business.PostService;
import org.main.wiredspaceapi.controller.dto.user.UserStatisticsDTO;
import org.main.wiredspaceapi.persistence.PostRepository;
import org.main.wiredspaceapi.persistence.UserRepository;
import org.main.wiredspaceapi.persistence.impl.post.PostRepositoryImpl;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserStatisticsService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public void incrementComments(UUID userId) {
        userRepository.getUserById(userId).ifPresent(user -> {
            user.setCommentsCount(user.getCommentsCount() + 1);
            userRepository.updateStatistics(user);
        });
    }

    public void decrementComments(UUID userId) {
        userRepository.getUserById(userId).ifPresent(user -> {
            int newCount = Math.max(0, user.getCommentsCount() - 1);
            user.setCommentsCount(newCount);
            userRepository.updateStatistics(user);
        });
    }

    public void incrementLikes(UUID userId) {
        userRepository.getUserById(userId).ifPresent(user -> {
            user.setLikesGiven(user.getLikesGiven() + 1);
            userRepository.updateStatistics(user);
        });
    }

    public void decrementLikes(UUID userId) {
        userRepository.getUserById(userId).ifPresent(user -> {
            int newCount = Math.max(0, user.getLikesGiven() - 1);
            user.setLikesGiven(newCount);
            userRepository.updateStatistics(user);
        });
    }

    public void incrementFriends(UUID userId) {
        userRepository.getUserById(userId).ifPresent(user -> {
            user.setFriendsCount(user.getFriendsCount() + 1);
            userRepository.updateStatistics(user);
        });
    }

    public void decrementFriends(UUID userId) {
        userRepository.getUserById(userId).ifPresent(user -> {
            int newCount = Math.max(0, user.getFriendsCount() - 1);
            user.setFriendsCount(newCount);
            userRepository.updateStatistics(user);
        });
    }

    public UserStatisticsDTO getUserStatistics(UUID userId) {
        return userRepository.getUserById(userId)
                .map(user -> new UserStatisticsDTO(
                        user.getCommentsCount(),
                        user.getLikesGiven(),
                        user.getFriendsCount(),
                        postRepository.getPostsByUserId(userId).size()
                ))
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
    }
}
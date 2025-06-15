package org.main.wiredspaceapi.business.impl;

import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.business.PostLikeService;
import org.main.wiredspaceapi.controller.dto.user.UserDTO;
import org.main.wiredspaceapi.controller.exceptions.PostNotFoundException;
import org.main.wiredspaceapi.controller.exceptions.UserNotFoundException;
import org.main.wiredspaceapi.controller.mapper.UserMapper;
import org.main.wiredspaceapi.persistence.PostRepository;
import org.main.wiredspaceapi.persistence.UserRepository;
import org.main.wiredspaceapi.security.util.AuthenticatedUserProvider;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PostLikeServiceImpl implements PostLikeService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserStatisticsService userStatisticsService;
    private final PostRepository postRepository;

    @Override
    public void likeOrUnlikePost(Long postId, UUID userId) {
        checkPostAndUserExist(postId, userId);

        boolean alreadyLiked = postRepository.hasUserLikedPost(postId, userId);
        if (alreadyLiked) {
            postRepository.unlikePost(postId, userId);
            userStatisticsService.decrementLikes(userId);
        } else {
            postRepository.likePost(postId, userId);
            userStatisticsService.incrementLikes(userId);
        }
    }

    @Override
    public List<UserDTO> getUsersWhoLikedPost(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new PostNotFoundException("Post not found with id: " + postId);
        }

        return postRepository.getUsersWhoLikedPost(postId).stream()
                .map(id -> userRepository.getUserById(id)
                        .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id)))
                .map(userMapper::userToUserDTO)
                .toList();
    }

    private void checkPostAndUserExist(Long postId, UUID userId) {
        if (!postRepository.existsById(postId)) {
            throw new PostNotFoundException("Post not found with id: " + postId);
        }
        if (userRepository.getUserById(userId).isEmpty()) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }
    }
}

package org.main.wiredspaceapi.business.impl;

import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.business.PostLikeService;
import org.main.wiredspaceapi.controller.dto.user.UserDTO;
import org.main.wiredspaceapi.controller.exceptions.PostNotFoundException;
import org.main.wiredspaceapi.controller.exceptions.UserNotFoundException;
import org.main.wiredspaceapi.controller.mapper.UserMapper;
import org.main.wiredspaceapi.persistence.PostLikeRepository;
import org.main.wiredspaceapi.persistence.PostRepository;
import org.main.wiredspaceapi.persistence.UserRepository;
import org.main.wiredspaceapi.persistence.impl.post.PostLikeRepositoryImpl;
import org.main.wiredspaceapi.security.util.AuthenticatedUserProvider;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PostLikeServiceImpl implements PostLikeService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserStatisticsService userStatisticsService;
    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;

    @Override
    public void likeOrUnlikePost(Long postId, UUID userId) {
        checkPostAndUserExist(postId, userId);

        boolean alreadyLiked = hasUserLikedPost(postId, userId);
        if (alreadyLiked) {
            postLikeRepository.unlikePost(postId, userId);
            userStatisticsService.decrementLikes(userId);
        } else {
            postLikeRepository.likePost(postId, userId);
            userStatisticsService.incrementLikes(userId);
        }
    }

    @Override
    public List<UserDTO> getUsersWhoLikedPost(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new PostNotFoundException("Post not found with id: " + postId);
        }

        return postLikeRepository.getUsersWhoLikedPost(postId).stream()
                .map(id -> userRepository.getUserById(id)
                        .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id)))
                .map(userMapper::userToUserDTO)
                .toList();
    }

    @Override
    public void deleteAllLikesForPost(Long postId) {
        postLikeRepository.deleteAllLikesForPost(postId);
    }

    private void checkPostAndUserExist(Long postId, UUID userId) {
        if (!postRepository.existsById(postId)) {
            throw new PostNotFoundException("Post not found with id: " + postId);
        }
        if (userRepository.getUserById(userId).isEmpty()) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }
    }

    @Override
    public boolean hasUserLikedPost(Long postId, UUID userid){
        return postLikeRepository.hasUserLikedPost(postId, userid);
    }

    @Override
    public Set<Long> findLikedPostIds(UUID userId, List<Long> postIds){
        return postLikeRepository.findLikedPostIds(userId, postIds);
    }
}

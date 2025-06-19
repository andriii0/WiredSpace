package org.main.wiredspaceapi.business.impl;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.business.CommentService;
import org.main.wiredspaceapi.business.PostLikeService;
import org.main.wiredspaceapi.business.PostService;
import org.main.wiredspaceapi.business.ReportService;
import org.main.wiredspaceapi.controller.dto.post.PostCreateDTO;
import org.main.wiredspaceapi.controller.dto.post.PostDTO;
import org.main.wiredspaceapi.controller.dto.user.UserDTO;
import org.main.wiredspaceapi.controller.exceptions.*;
import org.main.wiredspaceapi.controller.mapper.PostMapper;
import org.main.wiredspaceapi.controller.mapper.UserMapper;
import org.main.wiredspaceapi.domain.Post;
import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.persistence.PostRepository;
import org.main.wiredspaceapi.persistence.UserRepository;
import org.main.wiredspaceapi.security.util.AuthenticatedUserProvider;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostMapper postConverter;
    private final UserMapper userMapper;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final UserStatisticsService userStatisticsService;
    private final CommentService commentService;
    private final PostLikeService postLikeService;
    private final ReportService reportService;

    @Override
    public PostDTO createPost(PostCreateDTO dto) {
        validatePostContent(dto.getContent());

        UUID userId = authenticatedUserProvider.getCurrentUserId();
        User author = userRepository.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        Post post = postConverter.postCreateDtoToPost(dto);
        post.setCreatedAt(LocalDateTime.now());
        post.setAuthor(author);

        post = postRepository.create(post);

        PostDTO postDto = postConverter.postToPostDto(post);
        return enrichWithLikes(postDto, postDto.getId());
    }

    @Override
    public List<PostDTO> getPostsByUserId(UUID userId) {
        List<Post> posts = postRepository.getPostsByUserId(userId);
        return posts.stream()
                .map(post -> enrichWithLikes(postConverter.postToPostDto(post), post.getId()))
                .toList();
    }

    @Override
    public PostDTO getPostById(Long id) {
        Post post = postRepository.getById(id)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + id));
        return enrichWithLikes(postConverter.postToPostDto(post), id);
    }

    @Override
    public PostDTO updatePost(Long id, PostCreateDTO dto) {
        Post existingPost = postRepository.getById(id)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + id));

        validatePostContent(dto.getContent());
        validatePostOwnershipOrAdmin(existingPost);

        existingPost.setContent(dto.getContent());

        Post updatedPost = postRepository.update(existingPost);
        return enrichWithLikes(postConverter.postToPostDto(updatedPost), id);
    }

    @Transactional
    @Override
    public void deletePost(Long id) {
        Post post = postRepository.getById(id)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + id));

        validatePostOwnershipOrAdmin(post);

        reportService.deleteAllByPost(id);
        postLikeService.deleteAllLikesForPost(id);
        commentService.getCommentsByPostId(id)
                .forEach(comment -> commentService.deleteComment(comment.getId()));
        postRepository.delete(post);
    }

    @Override
    public void likePost(Long postId, UUID userId) {
        postLikeService.likeOrUnlikePost(postId, userId);
    }

    @Override
    public List<UserDTO> getUsersWhoLikedPost(Long postId) {
        return postLikeService.getUsersWhoLikedPost(postId);
    }

    private PostDTO enrichWithLikes(PostDTO dto, Long postId) {
        List<UUID> likedUserIds = postLikeService.getUsersWhoLikedPost(postId).stream()
                .map(UserDTO::getId)
                .toList();

        dto.setLikedByUserIds(likedUserIds);
        return dto;
    }


    @Override
    public List<Post> findPostsByAuthorIdsAndDate(List<UUID> authorIds, LocalDateTime from, LocalDateTime to, int limit) {
        return postRepository.findPostsByAuthorIdsAndDate(authorIds, from, to, limit);
    }

    @Override
    public List<Post> findRandomPostsExcludingUsers(List<UUID> excludedUserIds, UUID currentUserId, LocalDateTime from, LocalDateTime to, int limit) {
        return postRepository.findRandomPostsExcludingUsers(excludedUserIds, currentUserId, from, to, limit);
    }

    private void validatePostContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new InvalidPostContentException("Post content must not be empty");
        }
        if (content.length() > 1000) {
            throw new InvalidPostContentException("Post content exceeds 1000 characters");
        }
    }

    private void validatePostOwnershipOrAdmin(Post post) {
        UUID currentUserId = authenticatedUserProvider.getCurrentUserId();
        boolean isOwner = post.getAuthor().getId().equals(currentUserId);
        boolean isAdmin = authenticatedUserProvider.hasAdminRole();

        if (!isOwner && !isAdmin) {
            throw new UnauthorizedPostActionException("You are not allowed to perform this action on this post");
        }
    }
}

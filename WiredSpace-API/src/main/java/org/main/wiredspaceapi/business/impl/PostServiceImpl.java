package org.main.wiredspaceapi.business.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.business.CommentService;
import org.main.wiredspaceapi.business.PostLikeService;
import org.main.wiredspaceapi.business.PostService;
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

        UUID currentUserId = authenticatedUserProvider.getCurrentUserId();
        if (!existingPost.getAuthor().getId().equals(currentUserId)) {
            throw new UnauthorizedPostActionException("You are not the owner of this post");
        }

        existingPost.setContent(dto.getContent());
        existingPost.setCreatedAt(LocalDateTime.now());

        Post updatedPost = postRepository.update(existingPost);
        return enrichWithLikes(postConverter.postToPostDto(updatedPost), id);
    }

    @Override
    public void deletePost(Long id) {
        Post post = postRepository.getById(id)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + id));

        UUID currentUserId = authenticatedUserProvider.getCurrentUserId();
        if (!post.getAuthor().getId().equals(currentUserId)) {
            throw new UnauthorizedPostActionException("You are not allowed to delete this post");
        }

        postRepository.deleteAllLikesForPost(id);
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
        dto.setLikedByUserIds(postRepository.getUsersWhoLikedPost(postId));
        return dto;
    }

    private void validatePostContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new InvalidPostContentException("Post content must not be empty");
        }
        if (content.length() > 1000) {
            throw new InvalidPostContentException("Post content exceeds 1000 characters");
        }
    }
}

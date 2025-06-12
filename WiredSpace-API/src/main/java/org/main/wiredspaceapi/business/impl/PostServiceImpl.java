package org.main.wiredspaceapi.business.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.business.PostService;
import org.main.wiredspaceapi.controller.dto.post.PostCreateDTO;
import org.main.wiredspaceapi.controller.dto.post.PostDTO;
import org.main.wiredspaceapi.controller.dto.user.UserDTO;
import org.main.wiredspaceapi.controller.mapper.PostMapper;
import org.main.wiredspaceapi.controller.mapper.UserMapper;
import org.main.wiredspaceapi.domain.Comment;
import org.main.wiredspaceapi.domain.Post;
import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.persistence.CommentRepository;
import org.main.wiredspaceapi.persistence.PostRepository;
import org.main.wiredspaceapi.persistence.UserRepository;
import org.main.wiredspaceapi.security.util.AuthenticatedUserProvider;
import org.springframework.security.access.AccessDeniedException;
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

    @Override
    public PostDTO createPost(PostCreateDTO dto) {
        UUID userId = authenticatedUserProvider.getCurrentUserId();
        User author = userRepository.getUserById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        Post post = postConverter.postCreateDtoToPost(dto);
        post.setCreatedAt(LocalDateTime.now());
        post.setAuthor(author);

        post = postRepository.create(post);

        PostDTO postDto = postConverter.postToPostDto(post);

        postDto.setAuthorName(author.getName());

        return enrichWithLikes(postDto, postDto.getId());
    }

    @Override
    public List<PostDTO> getAllPosts() {
        return postRepository.getAll()
                .stream()
                .map(post -> enrichWithLikes(postConverter.postToPostDto(post), post.getId()))
                .toList();
    }

    @Override
    public PostDTO getPostById(Long id) {
        Post post = postRepository.getById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + id));
        return enrichWithLikes(postConverter.postToPostDto(post), id);
    }

    @Override
    public PostDTO updatePost(Long id, PostCreateDTO dto) {
        Post existingPost = postRepository.getById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + id));

        UUID currentUserId = authenticatedUserProvider.getCurrentUserId();
        if (!existingPost.getAuthor().getId().equals(currentUserId)) {
            throw new AccessDeniedException("You are not the owner of this post");
        }

        existingPost.setContent(dto.getContent());
        existingPost.setCreatedAt(LocalDateTime.now());

        Post updatedPost = postRepository.update(existingPost);
        return enrichWithLikes(postConverter.postToPostDto(updatedPost), id);
    }


    @Override
    public void deletePost(Long id) {
        Post post = postRepository.getById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + id));

        UUID currentUserId = authenticatedUserProvider.getCurrentUserId();

        if (!post.getAuthor().getId().equals(currentUserId)) {
            throw new AccessDeniedException("You are not allowed to delete this post");
        }

        postRepository.deleteById(post.getId());
    }

    @Override
    public void likePost(Long postId, UUID userId) {
        checkPostAndUserExist(postId, userId);

        boolean alreadyLiked = postRepository.hasUserLikedPost(postId, userId);

        if (alreadyLiked) {
            postRepository.unlikePost(postId, userId);
        } else {
            postRepository.likePost(postId, userId);
        }
    }

//    @Override
//    public void unlikePost(Long postId, String userId) {
//        UUID uuid = UUID.fromString(userId);
//        checkPostAndUserExist(postId, uuid);
//        postRepository.unlikePost(postId, uuid);
//    }

    @Override
    public List<UserDTO> getUsersWhoLikedPost(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException("Post not found with id: " + postId);
        }

        List<UUID> userIds = postRepository.getUsersWhoLikedPost(postId);

        return userIds.stream()
                .map(userId -> userRepository.getUserById(userId)
                        .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId)))
                .map(userMapper::userToUserDTO)
                .toList();
    }


    private void checkPostAndUserExist(Long postId, UUID userId) {
        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException("Post not found with id: " + postId);
        }
        if (userRepository.getUserById(userId).isEmpty()) {
            throw new EntityNotFoundException("User not found with id: " + userId);
        }
    }

    private PostDTO enrichWithLikes(PostDTO dto, Long postId) {
        dto.setLikedByUserIds(postRepository.getUsersWhoLikedPost(postId));
        return dto;
    }
}

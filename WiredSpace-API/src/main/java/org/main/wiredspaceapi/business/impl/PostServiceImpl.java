package org.main.wiredspaceapi.business.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.business.PostService;
import org.main.wiredspaceapi.controller.dto.post.PostCreateDTO;
import org.main.wiredspaceapi.controller.dto.post.PostDTO;
import org.main.wiredspaceapi.controller.mapper.PostMapper;
import org.main.wiredspaceapi.domain.Post;
import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.persistence.PostRepository;
import org.main.wiredspaceapi.persistence.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostMapper postConverter;

    @Override
    public PostDTO createPost(PostCreateDTO dto) {
        User author = userRepository.getUserById(dto.getAuthorId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + dto.getAuthorId()));

        Post post = postConverter.postCreateDtoToPost(dto);
        post.setCreatedAt(LocalDateTime.now());
        post.setAuthor(author);

        post = postRepository.create(post);

        return postConverter.postToPostDto(post);
    }

    @Override
    public List<PostDTO> getAllPosts() {
        return postRepository.getAll()
                .stream()
                .map(postConverter::postToPostDto)
                .toList();
    }

    @Override
    public PostDTO getPostById(Long id) {
        Post post = postRepository.getById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + id));

        return postConverter.postToPostDto(post);
    }

    @Override
    public PostDTO updatePost(Long id, PostCreateDTO dto) {
        Post existingPost = postRepository.getById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + id));

        existingPost.setContent(dto.getContent());
        existingPost.setCreatedAt(LocalDateTime.now());

        Post updatedPost = postRepository.update(existingPost);
        return postConverter.postToPostDto(updatedPost);
    }

    @Override
    public void deletePost(Long id) {
        if (!postRepository.existsById(id)) {
            throw new EntityNotFoundException("Post not found with id: " + id);
        }
        postRepository.deleteById(id);
    }
}

package org.main.wiredspaceapi.controller;

import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.business.PostService;
import org.main.wiredspaceapi.controller.dto.post.PostCreateDTO;
import org.main.wiredspaceapi.controller.dto.post.PostDTO;
import org.main.wiredspaceapi.security.util.AuthenticatedUserProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class PostController {

    private final PostService postService;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @PostMapping
    public ResponseEntity<PostDTO> createPost(@RequestBody PostCreateDTO dto) {
        PostDTO createdPost = postService.createPost(dto);
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PostDTO>> getAllPosts() {
        List<PostDTO> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable Long id) {
        PostDTO post = postService.getPostById(id);
        return ResponseEntity.ok(post);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDTO> updatePost(
            @PathVariable Long id,
            @RequestBody PostCreateDTO dto
    ) {
        UUID userId = authenticatedUserProvider.getCurrentUserId();
        PostDTO existingPost = postService.getPostById(id);
        if (!existingPost.getAuthorId().equals(userId)) {
            return ResponseEntity.status(403).build();
        }

        PostDTO updated = postService.updatePost(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id
    ) {
        UUID userId = authenticatedUserProvider.getCurrentUserId();
        PostDTO existingPost = postService.getPostById(id);
        if (!existingPost.getAuthorId().equals(userId)) {
            return ResponseEntity.status(403).build();
        }

        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    //like part

    @PostMapping("/{id}/like")
    public ResponseEntity<Void> likePost(
            @PathVariable Long id
    ) {
        UUID userId = authenticatedUserProvider.getCurrentUserId();
        postService.likePost(id, userId.toString());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/like")
    public ResponseEntity<Void> unlikePost(
            @PathVariable Long id
    ) {
        UUID userId = authenticatedUserProvider.getCurrentUserId();
        postService.unlikePost(id, userId.toString());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/likes")
    public ResponseEntity<List<String>> getUsersWhoLikedPost(
            @PathVariable Long id
    ) {
        List<String> userIds = postService.getUsersWhoLikedPost(id);
        return ResponseEntity.ok(userIds);
    }
}

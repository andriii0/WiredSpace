package org.main.wiredspaceapi.controller;

import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.business.CommentService;
import org.main.wiredspaceapi.business.PostService;
import org.main.wiredspaceapi.controller.dto.post.CommentDTO;
import org.main.wiredspaceapi.controller.dto.post.PostCreateDTO;
import org.main.wiredspaceapi.controller.dto.post.PostDTO;
import org.main.wiredspaceapi.controller.dto.user.UserDTO;
import org.main.wiredspaceapi.controller.exceptions.UnauthorizedPostActionException;
import org.main.wiredspaceapi.controller.mapper.CommentMapper;
import org.main.wiredspaceapi.domain.Comment;
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
    private final CommentService commentService;
    private final CommentMapper commentMapper;

    @PostMapping
    public ResponseEntity<PostDTO> createPost(@RequestBody PostCreateDTO dto) {
        PostDTO createdPost = postService.createPost(dto);
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostDTO>> getAllPostsByUserId(@PathVariable UUID userId) {
        List<PostDTO> posts = postService.getPostsByUserId(userId);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable Long id) {
        PostDTO post = postService.getPostById(id);
        return ResponseEntity.ok(post);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDTO> updatePost(@PathVariable Long id, @RequestBody PostCreateDTO dto) {
        UUID userId = authenticatedUserProvider.getCurrentUserId();
        PostDTO post = postService.getPostById(id);

        if (!post.getAuthorId().equals(userId)) {
            throw new UnauthorizedPostActionException("You are not the owner of this post.");
        }

        PostDTO updated = postService.updatePost(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        UUID userId = authenticatedUserProvider.getCurrentUserId();
        PostDTO post = postService.getPostById(id);

        if (!post.getAuthorId().equals(userId)) {
            throw new UnauthorizedPostActionException("You are not the owner of this post.");
        }

        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Void> likePost(@PathVariable Long id) {
        UUID userId = authenticatedUserProvider.getCurrentUserId();
        postService.likePost(id, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/likes")
    public ResponseEntity<List<UserDTO>> getUsersWhoLikedPost(@PathVariable Long id) {
        List<UserDTO> users = postService.getUsersWhoLikedPost(id);
        return ResponseEntity.ok(users);
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentDTO> addComment(
            @PathVariable Long postId,
            @RequestBody CommentDTO commentDTO
    ) {
        UUID userId = authenticatedUserProvider.getCurrentUserId();
        commentDTO.setPostId(postId);
        commentDTO.setAuthorId(userId);

        Comment comment = commentMapper.toEntity(commentDTO);
        CommentDTO created = commentService.createComment(comment);

        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentDTO>> getCommentsByPost(@PathVariable Long postId) {
        List<Comment> comments = commentService.getCommentsByPostId(postId);
        List<CommentDTO> dtos = comments.stream()
                .map(commentMapper::toDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/comments/{commentId}")
    public ResponseEntity<CommentDTO> getCommentById(@PathVariable Long commentId) {
        Comment comment = commentService.getCommentById(commentId);
        CommentDTO dto = commentMapper.toDto(comment);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentDTO> updateComment(
            @PathVariable Long commentId,
            @RequestBody String content
    ) {
        UUID userId = authenticatedUserProvider.getCurrentUserId();
        Comment comment = commentService.getCommentById(commentId);

        if (!comment.getAuthorId().equals(userId)) {
            throw new UnauthorizedPostActionException("You are not the owner of this comment.");
        }

        Comment updated = commentService.updateComment(commentId, content);
        return ResponseEntity.ok(commentMapper.toDto(updated));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        UUID userId = authenticatedUserProvider.getCurrentUserId();
        Comment comment = commentService.getCommentById(commentId);

        if (!comment.getAuthorId().equals(userId)) {
            throw new UnauthorizedPostActionException("You are not the owner of this comment.");
        }

        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}

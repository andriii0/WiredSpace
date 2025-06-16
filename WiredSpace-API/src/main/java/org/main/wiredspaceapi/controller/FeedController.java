package org.main.wiredspaceapi.controller;

import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.business.FeedService;
import org.main.wiredspaceapi.controller.dto.post.PostDTO;
import org.main.wiredspaceapi.controller.mapper.PostMapper;
import org.main.wiredspaceapi.domain.Post;
import org.main.wiredspaceapi.security.util.AuthenticatedUserProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
@RestController
@RequestMapping("/api/feed")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class FeedController {

    private final FeedService feedService;
    private final PostMapper postMapper;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @GetMapping("/smart")
    public ResponseEntity<List<PostDTO>> getSmartFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        UUID currentUserId = authenticatedUserProvider.getCurrentUserId();
        List<Post> posts = feedService.getSmartFeed(currentUserId, page, size);
        List<PostDTO> postDTOs = posts.stream()
                .map(postMapper::postToPostDto)
                .toList();
        return ResponseEntity.ok(postDTOs);
    }
}

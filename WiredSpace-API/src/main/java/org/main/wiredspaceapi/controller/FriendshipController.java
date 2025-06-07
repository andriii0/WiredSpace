package org.main.wiredspaceapi.controller;

import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.business.FriendshipService;
import org.main.wiredspaceapi.business.UserService;
import org.main.wiredspaceapi.controller.dto.friendship.FriendshipRequestDTO;
import org.main.wiredspaceapi.controller.dto.friendship.FriendshipResponseDTO;
import org.main.wiredspaceapi.controller.dto.friendship.FriendshipStatusResponseDTO;
import org.main.wiredspaceapi.domain.Friendship;
import org.main.wiredspaceapi.controller.mapper.FriendshipMapper;
import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.security.util.AuthenticatedUserProvider;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
@RestController
@RequestMapping("/api/friendships")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class FriendshipController {

    private final FriendshipService friendshipService;
    private final FriendshipMapper friendshipMapper;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<FriendshipResponseDTO> sendFriendRequest(
            @RequestBody FriendshipRequestDTO request,
            Authentication authentication
    ) {
        UUID currentUserId = authenticatedUserProvider.getCurrentUserId();
        Friendship friendship = friendshipService.sendFriendRequest(currentUserId, request.getFriendId());
        return ResponseEntity.ok(friendshipMapper.toDTO(friendship));
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<FriendshipResponseDTO> acceptFriendRequest(@PathVariable UUID id) {
        validateUserAccessToFriendship(id);

        Friendship accepted = friendshipService.acceptFriendRequest(id);
        return ResponseEntity.ok(friendshipMapper.toDTO(accepted));
    }

    @GetMapping("/me")
    public ResponseEntity<List<FriendshipResponseDTO>> getMyFriendships(Authentication authentication) {
        UUID currentUserId = authenticatedUserProvider.getCurrentUserId();
        List<Friendship> friendships = friendshipService.getFriendsOfUser(currentUserId);

        List<FriendshipResponseDTO> response = friendships.stream()
                .map(friendship -> {
                    User user = userService.getUserById(friendship.getUserId())
                            .orElseThrow(() -> new IllegalArgumentException("User not found"));
                    User friend = userService.getUserById(friendship.getFriendId())
                            .orElseThrow(() -> new IllegalArgumentException("Friend not found"));
                    return friendshipMapper.toDTO(friendship, user, friend);
                })
                .toList();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFriendship(@PathVariable UUID id) {
        validateUserAccessToFriendship(id);

        friendshipService.deleteFriendship(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<FriendshipResponseDTO> updateFriendship(
            @PathVariable UUID id,
            @RequestBody FriendshipRequestDTO request
    ) {
        validateUserAccessToFriendship(id);

        Friendship updated = friendshipService.updateFriendship(id, request.isAccepted());
        return ResponseEntity.ok(friendshipMapper.toDTO(updated));
    }

    @GetMapping("/status")
    public ResponseEntity<FriendshipStatusResponseDTO> getFriendshipStatus(
            @RequestParam UUID friendId) {

        UUID currentUserId = authenticatedUserProvider.getCurrentUserId();
        String status = friendshipService.getFriendshipStatus(friendId);

        FriendshipStatusResponseDTO response = FriendshipStatusResponseDTO.builder()
                .status(status)
                .userId(currentUserId)
                .friendId(friendId)
                .build();

        return ResponseEntity.ok(response);
    }
    private void validateUserAccessToFriendship(UUID friendshipId) {
        Friendship f = friendshipService.findFriendshipById(friendshipId);
        UUID currentUserId = authenticatedUserProvider.getCurrentUserId();

        if (!f.getUserId().equals(currentUserId) && !f.getFriendId().equals(currentUserId)) {
            throw new AccessDeniedException("You are not allowed to manage this friendship");
        }
    }
}

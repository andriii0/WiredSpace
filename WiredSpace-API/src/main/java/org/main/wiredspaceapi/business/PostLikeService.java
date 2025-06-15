package org.main.wiredspaceapi.business;

import org.main.wiredspaceapi.controller.dto.user.UserDTO;

import java.util.List;
import java.util.UUID;

public interface PostLikeService {
    void likeOrUnlikePost(Long postId, UUID userId);
    List<UserDTO> getUsersWhoLikedPost(Long postId);
}

package org.main.wiredspaceapi.business;

import org.main.wiredspaceapi.controller.dto.post.PostCreateDTO;
import org.main.wiredspaceapi.controller.dto.post.PostDTO;
import org.main.wiredspaceapi.controller.dto.user.UserDTO;
import org.main.wiredspaceapi.domain.Post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PostService {
    PostDTO createPost(PostCreateDTO postCreateDTO);
    List<PostDTO> getPostsByUserId(UUID userId);
    PostDTO getPostById(Long id);
    PostDTO updatePost(Long id, PostCreateDTO dto);
    void deletePost(Long id);

    List<Post> findPostsByAuthorIdsAndDate(List<UUID> authorIds, LocalDateTime from, LocalDateTime to, int limit);
    List<Post> findRandomPostsExcludingUsers(List<UUID> excludedUserIds, UUID currentUserId, LocalDateTime from, LocalDateTime to, int limit);

    //like part

    void likePost(Long postId, UUID userId);
//    void unlikePost(Long postId, String userId);
    List<UserDTO> getUsersWhoLikedPost(Long postId);
}

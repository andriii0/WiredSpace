package org.main.wiredspaceapi.business;

import org.main.wiredspaceapi.controller.dto.post.PostCreateDTO;
import org.main.wiredspaceapi.controller.dto.post.PostDTO;
import org.main.wiredspaceapi.controller.dto.user.UserDTO;

import java.util.List;
import java.util.UUID;

public interface PostService {
    PostDTO createPost(PostCreateDTO postCreateDTO);
    List<PostDTO> getAllPosts();
    PostDTO getPostById(Long id);
    PostDTO updatePost(Long id, PostCreateDTO dto);
    void deletePost(Long id); //TODO change to bool

    //like part

    void likePost(Long postId, UUID userId);
//    void unlikePost(Long postId, String userId);
    List<UserDTO> getUsersWhoLikedPost(Long postId);
}

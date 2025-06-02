package org.main.wiredspaceapi.business;

import org.main.wiredspaceapi.controller.dto.post.PostCreateDTO;
import org.main.wiredspaceapi.controller.dto.post.PostDTO;
import org.main.wiredspaceapi.domain.Comment;

import java.util.List;

public interface PostService {
    PostDTO createPost(PostCreateDTO postCreateDTO);
    List<PostDTO> getAllPosts();
    PostDTO getPostById(Long id);
    PostDTO updatePost(Long id, PostCreateDTO dto);
    void deletePost(Long id); //TODO change to bool

    //like part

    void likePost(Long postId, String userId);
    void unlikePost(Long postId, String userId);
    List<String> getUsersWhoLikedPost(Long postId);
}

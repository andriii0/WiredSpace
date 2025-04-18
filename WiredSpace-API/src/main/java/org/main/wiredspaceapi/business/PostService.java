package org.main.wiredspaceapi.business;

import org.main.wiredspaceapi.controller.dto.post.PostCreateDTO;
import org.main.wiredspaceapi.controller.dto.post.PostDTO;

import java.util.List;

public interface PostService {
    PostDTO createPost(PostCreateDTO postCreateDTO);
    List<PostDTO> getAllPosts();
    PostDTO getPostById(Long id);
}

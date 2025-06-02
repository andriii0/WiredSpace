package org.main.wiredspaceapi.controller.mapper;

import org.main.wiredspaceapi.controller.dto.post.CommentDTO;
import org.main.wiredspaceapi.controller.dto.post.PostCreateDTO;
import org.main.wiredspaceapi.controller.dto.post.PostDTO;
import org.main.wiredspaceapi.domain.Comment;
import org.main.wiredspaceapi.domain.Post;
import org.main.wiredspaceapi.domain.User;
import org.mapstruct.*;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface PostMapper {

    Post postCreateDtoToPost(PostCreateDTO dto);

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(target = "likedByUserIds", expression = "java(mapUsersToUUIDs(post.getLikedBy()))")
    @Mapping(target = "comments", expression = "java(mapComments(post.getComments()))")
    PostDTO postToPostDto(Post post);

    default List<UUID> mapUsersToUUIDs(List<User> users) {
        return users == null ? List.of() :
                users.stream()
                        .map(User::getId)
                        .toList();
    }

    default List<CommentDTO> mapComments(List<Comment> comments) {
        return comments == null ? List.of() :
                comments.stream()
                        .map(this::mapCommentToDTO)
                        .toList();
    }

    default CommentDTO mapCommentToDTO(Comment comment) {
        if (comment == null) return null;
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setAuthorId(comment.getAuthorId());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        return dto;
    }
}

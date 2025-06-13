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

@Mapper(componentModel = "spring", uses = {CommentMapper.class})
public interface PostMapper {

    Post postCreateDtoToPost(PostCreateDTO dto);

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "author.name", target = "authorName")
    @Mapping(target = "likedByUserIds", expression = "java(mapUsersToUUIDs(post.getLikedBy()))")
    PostDTO postToPostDto(Post post);

    default List<UUID> mapUsersToUUIDs(List<User> users) {
        if (users == null) return List.of();
        return users.stream().map(User::getId).toList();
    }
}

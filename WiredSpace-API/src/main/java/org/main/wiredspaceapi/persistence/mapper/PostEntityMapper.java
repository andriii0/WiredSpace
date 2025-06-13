package org.main.wiredspaceapi.persistence.mapper;

import org.main.wiredspaceapi.domain.Post;
import org.main.wiredspaceapi.persistence.entity.PostEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CommentEntityMapper.class})
public interface PostEntityMapper {
    @Mapping(source = "author", target = "author")
    @Mapping(source = "comments", target = "comments")
    PostEntity toEntity(Post post);

    @Mapping(source = "author", target = "author")
    @Mapping(source = "comments", target = "comments")
    Post toDomain(PostEntity postEntity);
}

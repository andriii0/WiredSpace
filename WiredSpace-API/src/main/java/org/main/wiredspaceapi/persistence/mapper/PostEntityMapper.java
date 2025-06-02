package org.main.wiredspaceapi.persistence.mapper;

import org.main.wiredspaceapi.domain.Post;
import org.main.wiredspaceapi.persistence.entity.PostEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostEntityMapper {
    PostEntity toEntity(Post post);
    Post toDomain(PostEntity postEntity);
}

package org.main.wiredspaceapi.persistence.mapper;

import org.main.wiredspaceapi.controller.dto.post.PostDTO;
import org.main.wiredspaceapi.domain.Post;
import org.main.wiredspaceapi.persistence.entity.PostEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostMapper {
    Post toEntity(PostEntity postEntity);
    PostEntity toPostEntity(Post post);
}

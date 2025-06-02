package org.main.wiredspaceapi.persistence.mapper;

import org.main.wiredspaceapi.domain.Comment;
import org.main.wiredspaceapi.persistence.entity.PostCommentEntity;
import org.main.wiredspaceapi.persistence.entity.PostEntity;
import org.main.wiredspaceapi.persistence.entity.UserEntity;
import org.mapstruct.*;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface CommentEntityMapper {

    // DOMAIN -> ENTITY
    @Mappings({
            @Mapping(target = "post", expression = "java(toPostEntity(domain.getPostId()))"),
            @Mapping(target = "author", expression = "java(toUserEntity(domain.getAuthorId()))")
    })
    PostCommentEntity toEntity(Comment domain);

    // ENTITY -> DOMAIN
    @Mappings({
            @Mapping(target = "postId", source = "post.id"),
            @Mapping(target = "authorId", source = "author.id")
    })
    Comment toDomain(PostCommentEntity entity);

    List<Comment> toDomainList(List<PostCommentEntity> entities);

//    default PostEntity toPostEntity(Long id) {
//        if (id == null) return null;
//        return PostEntity.builder().id(id).build();
//    }
//
//    default UserEntity toUserEntity(UUID id) {
//        if (id == null) return null;
//        return UserEntity.builder().id(id).build();
//    }
}

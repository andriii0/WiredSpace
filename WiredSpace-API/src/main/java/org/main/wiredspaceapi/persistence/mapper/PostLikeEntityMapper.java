package org.main.wiredspaceapi.persistence.mapper;

import org.main.wiredspaceapi.domain.PostLike;
import org.main.wiredspaceapi.persistence.entity.PostEntity;
import org.main.wiredspaceapi.persistence.entity.PostLikeEntity;
import org.main.wiredspaceapi.persistence.entity.UserEntity;
import org.mapstruct.*;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface PostLikeEntityMapper {

    // DOMAIN -> ENTITY
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "post", expression = "java(toPostEntity(domain.getPostId()))"),
            @Mapping(target = "user", expression = "java(toUserEntity(domain.getUserId()))"),
            @Mapping(target = "likedAt", source = "likedAt")
    })
    PostLikeEntity toEntity(PostLike domain);

    // ENTITY -> DOMAIN
    @Mappings({
            @Mapping(target = "postId", source = "post.id"),
            @Mapping(target = "userId", source = "user.id"),
            @Mapping(target = "likedAt", source = "likedAt")
    })
    PostLike toDomain(PostLikeEntity entity);

    List<PostLike> toDomainList(List<PostLikeEntity> entities);

    default PostEntity toPostEntity(Long postId) {
        if (postId == null) return null;
        return PostEntity.builder().id(postId).build();
    }

    default UserEntity toUserEntity(UUID userId) {
        if (userId == null) return null;
        return UserEntity.builder().id(userId).build();
    }
}

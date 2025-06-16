package org.main.wiredspaceapi.persistence.mapper;

import org.main.wiredspaceapi.domain.Report;
import org.main.wiredspaceapi.domain.Post;
import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.persistence.entity.ReportEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {UserEntityMapper.class, PostEntityMapper.class})
public interface ReportEntityMapper {

    @Mapping(source = "reporter", target = "reporterId", qualifiedByName = "mapUserToId")
    @Mapping(source = "post", target = "postId", qualifiedByName = "mapPostToId")
    Report toDomain(ReportEntity entity);

    @Mapping(source = "reporterId", target = "reporter", qualifiedByName = "mapIdToUser")
    @Mapping(source = "postId", target = "post", qualifiedByName = "mapIdToPost")
    ReportEntity toEntity(Report domain);


    @Named("mapUserToId")
    static java.util.UUID mapUserToId(org.main.wiredspaceapi.persistence.entity.UserEntity user) {
        return user == null ? null : user.getId();
    }

    @Named("mapPostToId")
    static Long mapPostToId(org.main.wiredspaceapi.persistence.entity.PostEntity post) {
        return post == null ? null : post.getId();
    }

    @Named("mapIdToUser")
    static org.main.wiredspaceapi.persistence.entity.UserEntity mapIdToUser(java.util.UUID id) {
        return id == null ? null : org.main.wiredspaceapi.persistence.entity.UserEntity.builder().id(id).build();
    }

    @Named("mapIdToPost")
    static org.main.wiredspaceapi.persistence.entity.PostEntity mapIdToPost(Long id) {
        return id == null ? null : org.main.wiredspaceapi.persistence.entity.PostEntity.builder().id(id).build();
    }
}

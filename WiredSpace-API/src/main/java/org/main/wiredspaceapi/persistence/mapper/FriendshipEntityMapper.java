package org.main.wiredspaceapi.persistence.mapper;

import org.main.wiredspaceapi.domain.Friendship;
import org.main.wiredspaceapi.persistence.entity.FriendshipEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface FriendshipEntityMapper {

    FriendshipEntityMapper INSTANCE = Mappers.getMapper(FriendshipEntityMapper.class);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "friendId", source = "friend.id")
    Friendship toDomain(FriendshipEntity entity);
}

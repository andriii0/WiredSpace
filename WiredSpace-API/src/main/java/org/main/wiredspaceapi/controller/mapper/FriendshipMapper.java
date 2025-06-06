package org.main.wiredspaceapi.controller.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.main.wiredspaceapi.controller.dto.friendship.FriendshipRequestDTO;
import org.main.wiredspaceapi.controller.dto.friendship.FriendshipResponseDTO;
import org.main.wiredspaceapi.domain.Friendship;
import org.main.wiredspaceapi.domain.User;

@Mapper(componentModel = "spring")
public interface FriendshipMapper {

    @Mappings({
            @Mapping(target = "id", source = "friendship.id"),
            @Mapping(target = "accepted", source = "friendship.accepted"),
            @Mapping(target = "user", source = "user"),
            @Mapping(target = "friend", source = "friend")
    })
    FriendshipResponseDTO toDTO(Friendship friendship, User user, User friend);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "friend", ignore = true)
    FriendshipResponseDTO toDTO(Friendship friendship);

    @Mapping(target = "id", ignore = true)
    Friendship fromRequestDTO(FriendshipRequestDTO dto);
}

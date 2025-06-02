package org.main.wiredspaceapi.controller.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.main.wiredspaceapi.controller.dto.friendship.FriendshipRequestDTO;
import org.main.wiredspaceapi.controller.dto.friendship.FriendshipResponseDTO;
import org.main.wiredspaceapi.domain.Friendship;

@Mapper(componentModel = "spring")
public interface FriendshipMapper {

    FriendshipResponseDTO toDTO(Friendship friendship);

    @Mapping(target = "id", ignore = true)
    Friendship fromRequestDTO(FriendshipRequestDTO dto);
}

package org.main.wiredspaceapi.controller.converter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.main.wiredspaceapi.controller.dto.user.UserCreateDTO;
import org.main.wiredspaceapi.controller.dto.user.UserDTO;
import org.main.wiredspaceapi.domain.User;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    UserDTO userToUserDTO(User user);
    @Mapping(target = "id", ignore = true)
    User userCreateDTOToUser(UserCreateDTO dto);
}

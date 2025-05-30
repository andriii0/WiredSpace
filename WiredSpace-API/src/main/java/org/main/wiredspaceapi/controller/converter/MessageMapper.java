package org.main.wiredspaceapi.controller.converter;

import org.main.wiredspaceapi.controller.dto.message.MessageDTO;
import org.main.wiredspaceapi.domain.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(source = "fromUser", target = "from")
    @Mapping(source = "toUser", target = "to")
    MessageDTO toDTO(Message message);

    @Mapping(source = "from", target = "fromUser")
    @Mapping(source = "to", target = "toUser")
    Message toDomain(MessageDTO dto);
}

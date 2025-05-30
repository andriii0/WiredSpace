package org.main.wiredspaceapi.controller.converter;


import org.main.wiredspaceapi.controller.dto.message.MessageDTO;
import org.main.wiredspaceapi.domain.Message;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    MessageDTO toDTO(Message message);

    Message toDomain(MessageDTO dto);
}
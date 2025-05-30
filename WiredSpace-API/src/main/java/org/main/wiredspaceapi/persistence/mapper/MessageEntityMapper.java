package org.main.wiredspaceapi.persistence.mapper;

import org.main.wiredspaceapi.domain.Message;
import org.main.wiredspaceapi.persistence.entity.MessageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MessageEntityMapper {

    @Mapping(source = "fromUser", target = "sender")
    @Mapping(source = "toUser", target = "recipient")
    MessageEntity toEntity(Message message);

    @Mapping(source = "sender", target = "fromUser")
    @Mapping(source = "recipient", target = "toUser")
    Message toDomain(MessageEntity entity);
}

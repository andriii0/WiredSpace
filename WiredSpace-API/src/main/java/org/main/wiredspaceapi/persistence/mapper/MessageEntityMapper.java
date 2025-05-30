package org.main.wiredspaceapi.persistence.mapper;

import org.main.wiredspaceapi.domain.Message;
import org.main.wiredspaceapi.persistence.entity.MessageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface MessageEntityMapper {

    @Mappings({
            @Mapping(source = "from", target = "fromUser"),
            @Mapping(source = "to", target = "toUser")
    })
    MessageEntity toEntity(Message message);

    @Mappings({
            @Mapping(source = "fromUser", target = "from"),
            @Mapping(source = "toUser", target = "to")
    })
    Message toDomain(MessageEntity entity);
}

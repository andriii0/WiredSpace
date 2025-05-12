package org.main.wiredspaceapi.persistence.mapper;

import org.mapstruct.Mapper;
import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.persistence.entity.UserEntity;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    User toDomain(UserEntity entity);
    UserEntity toEntity(User domain);
}

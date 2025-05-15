package org.main.wiredspaceapi.persistence.mapper;
import org.main.wiredspaceapi.domain.Admin;
import org.main.wiredspaceapi.persistence.entity.AdminEntity;
import org.mapstruct.Mapper;
import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.persistence.entity.UserEntity;

@Mapper(componentModel = "spring")
public interface AdminEntityMapper {
    Admin toDomain(AdminEntity adminEntity);
    AdminEntity toEntity(Admin admin);
}

package org.main.wiredspaceapi.persistence.impl.admin;

import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.domain.Admin;
import org.main.wiredspaceapi.domain.enums.AdminRole;
import org.main.wiredspaceapi.persistence.AdminRepository;
import org.main.wiredspaceapi.persistence.entity.AdminEntity;
import org.main.wiredspaceapi.persistence.impl.admin.AdminDB;
import org.main.wiredspaceapi.persistence.mapper.AdminEntityMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AdminRepositoryImpl implements AdminRepository {

    private final AdminDB adminDB;
    private final AdminEntityMapper adminMapper;

    @Override
    public Admin createAdmin(String name, String email, String password, AdminRole role) {
        AdminEntity entity = new AdminEntity();
        entity.setName(name);
        entity.setEmail(email);
        entity.setPassword(password);
        entity.setRole(role);

        AdminEntity saved = adminDB.save(entity);
        return adminMapper.toDomain(saved);
    }

    @Override
    public Optional<Admin> getAdminById(UUID id) {
        return adminDB.findById(id)
                .map(adminMapper::toDomain);
    }

    @Override
    public Optional<Admin> findByEmail(String email) {
        Optional<AdminEntity> entity = adminDB.findByEmail(email);
        System.out.println("ENTITY: " + entity);

        Optional<Admin> result = entity.map(adminMapper::toDomain);
        System.out.println("MAPPED: " + result);
        return result;
    }


    @Override
    public boolean deleteAdmin(UUID id) {
        if (!adminDB.existsById(id)) return false;
        adminDB.deleteById(id);
        return true;
    }
}

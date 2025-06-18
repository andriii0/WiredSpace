package org.main.wiredspaceapi.persistence.impl.admin;

import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.domain.Admin;
import org.main.wiredspaceapi.domain.enums.AdminRole;
import org.main.wiredspaceapi.persistence.AdminRepository;
import org.main.wiredspaceapi.persistence.entity.AdminEntity;
import org.main.wiredspaceapi.persistence.mapper.AdminEntityMapper;
import org.main.wiredspaceapi.controller.exceptions.AdminNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.List;
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
    public Optional<Admin> findAdminById(UUID id) {
        return adminDB.findById(id)
                .map(adminMapper::toDomain);
    }

    @Override
    public Optional<Admin> findByEmail(String email) {
        return adminDB.findByEmail(email)
                .map(adminMapper::toDomain);
    }

    @Override
    public List<Admin> getAllAdmins() {
        return adminDB.findAll().stream()
                .map(adminMapper::toDomain)
                .toList();
    }

    @Override
    public Admin updateAdmin(Admin admin) {
        AdminEntity entity = adminDB.findById(admin.getId())
                .orElseThrow(() -> new AdminNotFoundException("Admin with ID " + admin.getId() + " not found."));

        entity.setName(admin.getName());
        entity.setEmail(admin.getEmail());
        entity.setPassword(admin.getPassword());
        entity.setRole(admin.getRole());

        AdminEntity updated = adminDB.save(entity);
        return adminMapper.toDomain(updated);
    }

    @Override
    public void deleteAdmin(UUID id) {
        if (!adminDB.existsById(id)) {
            throw new AdminNotFoundException("Admin with ID " + id + " not found.");
        }
        adminDB.deleteById(id);
    }
}

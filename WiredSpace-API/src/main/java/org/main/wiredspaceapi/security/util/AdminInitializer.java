package org.main.wiredspaceapi.security.util;

import org.main.wiredspaceapi.domain.enums.AdminRole;
import org.main.wiredspaceapi.persistence.AdminRepository;
import org.main.wiredspaceapi.persistence.entity.AdminEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
public class AdminInitializer implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.default.email}")
    private String defaultAdminEmail;

    @Value("${admin.default.password}")
    private String defaultAdminPassword;

    public AdminInitializer(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (adminRepository.findByEmail(defaultAdminEmail).isEmpty()) {
            AdminEntity admin = new AdminEntity();
            String name = "Super Admin";
            String email = defaultAdminEmail ;
            String password = passwordEncoder.encode(defaultAdminPassword);

            adminRepository.createAdmin(name, email, password, AdminRole.ADMIN);
            System.out.println("🚀🚀🚀 Default admin created: " + defaultAdminEmail);
        } else {
            System.out.println("✅✅✅ Admin already exists: " + defaultAdminEmail);
        }
    }
}

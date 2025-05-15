package org.main.wiredspaceapi.controller;

import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.business.AdminService;
import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.domain.enums.UserRole;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@PreAuthorize("hasRole()")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUserById(@PathVariable UUID id) {
        return adminService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/email")
    public ResponseEntity<User> getUserByEmail(@RequestParam String email) {
        return adminService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/user/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable UUID id,
            @RequestParam String name,
            @RequestParam String email
    ) {
        return adminService.updateUser(id, name, email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/user/{id}/role")
    public ResponseEntity<User> updateUserWithRole(
            @PathVariable UUID id,
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam UserRole role
    ) {
        return adminService.updateUser(id, name, email, role)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        boolean deleted = adminService.deleteUser(id);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}

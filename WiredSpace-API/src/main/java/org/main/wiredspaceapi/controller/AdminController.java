package org.main.wiredspaceapi.controller;

import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.business.AdminService;
import org.main.wiredspaceapi.business.impl.UserStatisticsService;
import org.main.wiredspaceapi.controller.dto.user.UserStatisticsDTO;
import org.main.wiredspaceapi.domain.Admin;
import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.controller.dto.args.DemoteAdminArgs;
import org.main.wiredspaceapi.controller.dto.args.PromoteUserArgs;
import org.main.wiredspaceapi.domain.enums.UserRole;
import org.main.wiredspaceapi.security.util.AuthenticatedUserProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated() and (hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPPORT'))")
public class AdminController {

    private final AdminService adminService;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final UserStatisticsService userStatisticsService;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/promote")
    public ResponseEntity<Admin> promoteUserToAdmin(@RequestBody PromoteUserArgs request) {
        return adminService.promoteUserToAdmin(request.getUserId(), request.getAdminRole())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/demote")
    public ResponseEntity<User> demoteAdminToUser(@RequestBody DemoteAdminArgs request) {
        UUID currentAdminId = authenticatedUserProvider.getCurrentUserId();
        if (currentAdminId.equals(request.getAdminId())) {
            throw new IllegalArgumentException("You cannot delete/demote yourself.");
        }
        return adminService.demoteAdminToUser(request.getAdminId(), request.getUserRole())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPPORT')")
    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUserById(@PathVariable UUID id) {
        return adminService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPPORT')")
    @GetMapping("/user/email")
    public ResponseEntity<User> getUserByEmail(@RequestParam String email) {
        return adminService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/statistics/{userId}")
    public ResponseEntity<UserStatisticsDTO> getStatistics(@PathVariable UUID userId) {
        UserStatisticsDTO stats = userStatisticsService.getUserStatistics(userId);
        return ResponseEntity.ok(stats);
    }



//    @PreAuthorize("hasAuthority('ROLE_ADMIN_ROLE')")
//    @PutMapping("/user/{id}")
//    public ResponseEntity<User> updateUser(
//            @PathVariable UUID id,
//            @RequestParam String name,
//            @RequestParam String email
//    ) {
//        return adminService.updateUser(id, name, email)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }

//    @PreAuthorize("hasAuthority('ROLE_ADMIN_ROLE')")
//    @PutMapping("/user/{id}/role")
//    public ResponseEntity<User> updateUserWithRole(
//            @PathVariable UUID id,
//            @RequestParam String name,
//            @RequestParam String email,
//            @RequestParam UserRole role
//    ) {
//        return adminService.updateUser(id, name, email, role)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        boolean deleted = adminService.deleteUser(id);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}

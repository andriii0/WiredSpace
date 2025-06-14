package org.main.wiredspaceapi.controller;

import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.business.AdminService;
import org.main.wiredspaceapi.business.impl.UserStatisticsService;
import org.main.wiredspaceapi.controller.dto.user.UserStatisticsDTO;
import org.main.wiredspaceapi.domain.Admin;
import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.controller.dto.args.DemoteAdminArgs;
import org.main.wiredspaceapi.controller.dto.args.PromoteUserArgs;
import org.main.wiredspaceapi.controller.exceptions.SelfDemotionException;
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
        Admin admin = adminService.promoteUserToAdmin(request.getUserId(), request.getAdminRole())
                .orElseThrow(() -> new RuntimeException("Promotion failed unexpectedly."));
        return ResponseEntity.ok(admin);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/demote")
    public ResponseEntity<User> demoteAdminToUser(@RequestBody DemoteAdminArgs request) {
        UUID currentAdminId = authenticatedUserProvider.getCurrentUserId();
        if (currentAdminId.equals(request.getAdminId())) {
            throw new SelfDemotionException("You cannot delete/demote yourself.");
        }

        User user = adminService.demoteAdminToUser(request.getAdminId(), request.getUserRole())
                .orElseThrow(() -> new RuntimeException("Demotion failed unexpectedly."));
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPPORT')")
    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUserById(@PathVariable UUID id) {
        User user = adminService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User retrieval failed unexpectedly."));
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPPORT')")
    @GetMapping("/user/email")
    public ResponseEntity<User> getUserByEmail(@RequestParam String email) {
        User user = adminService.getUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User retrieval by email failed unexpectedly."));
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/statistics/{userId}")
    public ResponseEntity<UserStatisticsDTO> getStatistics(@PathVariable UUID userId) {
        UserStatisticsDTO stats = userStatisticsService.getUserStatistics(userId);
        return ResponseEntity.ok(stats);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}

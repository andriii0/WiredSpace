package org.main.wiredspaceapi.controller;

import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.business.*;
import org.main.wiredspaceapi.business.impl.StatisticsServiceImpl;
import org.main.wiredspaceapi.business.impl.UserStatisticsService;
import org.main.wiredspaceapi.controller.dto.DailyStatsDto;
import org.main.wiredspaceapi.controller.dto.args.DemoteAdminArgs;
import org.main.wiredspaceapi.controller.dto.args.PromoteUserArgs;
import org.main.wiredspaceapi.controller.dto.post.PostCreateDTO;
import org.main.wiredspaceapi.controller.dto.post.PostDTO;
import org.main.wiredspaceapi.controller.dto.user.UserDTO;
import org.main.wiredspaceapi.controller.dto.user.UserStatisticsDTO;
import org.main.wiredspaceapi.controller.exceptions.AdminNotFoundException;
import org.main.wiredspaceapi.controller.exceptions.SelfDemotionException;
import org.main.wiredspaceapi.controller.exceptions.UserNotFoundException;
import org.main.wiredspaceapi.controller.mapper.FriendshipMapper;
import org.main.wiredspaceapi.controller.mapper.UserMapper;
import org.main.wiredspaceapi.domain.Admin;
import org.main.wiredspaceapi.domain.Report;
import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.domain.enums.AdminRole;
import org.main.wiredspaceapi.security.util.AuthenticatedUserProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    private final UserService userService;
    private final PostService postService;
    private final ReportService reportService;
    private final UserMapper userMapper;
    private final StatisticsService statisticsService;

    //ADMIN
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/admins")
    public ResponseEntity<List<Admin>> getAllAdmins() {
        return ResponseEntity.ok(adminService.getAllAdmins());
    }
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/admin/{id}")
    public ResponseEntity<Admin> getAdminById(@PathVariable UUID id) {
        return adminService.getAdminById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new AdminNotFoundException("Admin with ID " + id + " not found."));
    }
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/admin/email")
    public ResponseEntity<Admin> getAdminByEmail(@RequestParam String email) {
        return adminService.findAdminByEmail(email)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new AdminNotFoundException("Admin with email " + email + " not found."));
    }
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/admin/create")
    public ResponseEntity<Admin> createAdmin(@RequestParam String name,
                                             @RequestParam String email,
                                             @RequestParam String password,
                                             @RequestParam AdminRole role) {
        Admin admin = adminService.createAdmin(name, email, password, role);
        return ResponseEntity.ok(admin);
    }
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/admin/update")
    public ResponseEntity<Admin> updateAdmin(@RequestBody Admin admin) {
        return ResponseEntity.ok(adminService.updateAdmin(admin));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable UUID id) {
        UUID currentAdminId = authenticatedUserProvider.getCurrentUserId();
        if (currentAdminId.equals(id)) {
            throw new SelfDemotionException("You cannot delete yourself.");
        }

        adminService.deleteAdmin(id);
        return ResponseEntity.ok().build();
    }

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

    @GetMapping("/user/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable UUID id) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found"));
        return ResponseEntity.ok(userMapper.userToUserDTO(user));
    }

    @GetMapping("/user/email")
    public ResponseEntity<UserDTO> getUserByEmail(@RequestParam String email) {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found"));
        return ResponseEntity.ok(userMapper.userToUserDTO(user));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUserById(id);
        return ResponseEntity.ok().build();
    }

    //POST

    @GetMapping("/post/{id}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/post/{id}")
    public ResponseEntity<PostDTO> updatePost(@PathVariable Long id, @RequestBody PostCreateDTO dto) {
        return ResponseEntity.ok(postService.updatePost(id, dto));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/post/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.ok().build();
    }

    // REPORTS

    @GetMapping("/reports")
    public ResponseEntity<List<Report>> getAllReports(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "100") int limit) {
        return ResponseEntity.ok(reportService.getAllReports(offset, limit));
    }

    @GetMapping("/reports/post/{postId}")
    public ResponseEntity<List<Report>> getReportsForPost(@PathVariable Long postId) {
        return ResponseEntity.ok(reportService.getAllReportsForPost(postId));
    }

    @DeleteMapping("/reports/{reportId}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long reportId) {
        reportService.deleteReport(reportId);
        return ResponseEntity.ok().build();
    }

    // Stats of the user

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/statistics/{userId}")
    public ResponseEntity<UserStatisticsDTO> getStatistics(@PathVariable UUID userId) {
        return ResponseEntity.ok(userStatisticsService.getUserStatistics(userId));
    }

    // Platform statistics

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/statistics/last-30-days")
    public ResponseEntity<List<DailyStatsDto>> getLast30DaysStats() {
        return ResponseEntity.ok(statisticsService.getLast30DaysStats());
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/statistics/last-7-days")
    public ResponseEntity<List<DailyStatsDto>> getLast7DaysStats() {
        return ResponseEntity.ok(statisticsService.getLast7DaysStats());
    }

}

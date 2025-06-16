package org.main.wiredspaceapi.controller;

import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.business.ReportService;
import org.main.wiredspaceapi.controller.dto.post.ReportDTO;
import org.main.wiredspaceapi.controller.mapper.ReportMapper;
import org.main.wiredspaceapi.domain.Report;
import org.main.wiredspaceapi.security.util.AuthenticatedUserProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class ReportController {

    private final ReportService reportService;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final ReportMapper reportMapper;


    @PostMapping("/{postId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> reportPost(@PathVariable Long postId, @RequestBody ReportDTO reportDTO) {
        UUID reporterId = authenticatedUserProvider.getCurrentUserId();
        reportService.reportPost(postId, reporterId, reportDTO.getReason());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<ReportDTO>> getAllReports(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<ReportDTO> reports = reportService.getAllReports(offset, limit)
                .stream()
                .map(reportMapper::toDto)
                .toList();

        return ResponseEntity.ok(reports);
    }

    @GetMapping("/post/{postId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<ReportDTO>> getAllReportsForPost(@PathVariable Long postId) {
        List<Report> reports = reportService.getAllReportsForPost(postId);
        List<ReportDTO> dtos = reports.stream()
                .map(reportMapper::toDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @DeleteMapping("/{reportId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteReport(@PathVariable Long reportId) {
        reportService.deleteReport(reportId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/reporter/{reporterId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteAllByReporter(@PathVariable UUID reporterId) {
        reportService.deleteAllByReporter(reporterId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/post/{postId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteAllByPost(@PathVariable Long postId) {
        reportService.deleteAllByPost(postId);
        return ResponseEntity.noContent().build();
    }
}

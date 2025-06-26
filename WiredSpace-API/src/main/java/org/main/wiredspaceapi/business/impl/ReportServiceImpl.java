package org.main.wiredspaceapi.business.impl;

import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.business.ReportService;
import org.main.wiredspaceapi.controller.exceptions.*;
import org.main.wiredspaceapi.domain.Post;
import org.main.wiredspaceapi.domain.Report;
import org.main.wiredspaceapi.domain.User;
import org.main.wiredspaceapi.persistence.PostRepository;
import org.main.wiredspaceapi.persistence.ReportRepository;
import org.main.wiredspaceapi.persistence.UserRepository;
import org.main.wiredspaceapi.security.util.AuthenticatedUserProvider;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Override
    public void reportPost(Long postId, UUID reporterId, String reason) {
        Post post = postRepository.getById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + postId));

        User reporter = userRepository.getUserById(reporterId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + reporterId));

        if (reportRepository.alreadyReported(reporterId, postId)) {
            throw new PostAlreadyReportedExcepion("You have already reported this post");
        }

        Report report = Report.builder()
                .postId(postId)
                .reporterId(reporterId)
                .reason(reason)
                .reportedAt(LocalDateTime.now())
                .build();

        reportRepository.save(report);
    }

    @Override
    public List<Report> getAllReports(int offset, int limit) {
        if (!authenticatedUserProvider.hasAdminRole() && !authenticatedUserProvider.hasSupportRole()) {
            throw new UnauthorizedException("Only admins or support can ...");
        }
        return reportRepository.findAll(offset, limit);
    }

    @Override
    public void deleteReport(Long reportId) {
        if (!authenticatedUserProvider.hasAdminRole() && !authenticatedUserProvider.hasSupportRole()) {
            throw new UnauthorizedException("Only admins or support can ...");
        }
        if (!reportRepository.existsById(reportId)) {
            throw new ReportNotFoundException("Report not found with id: " + reportId);
        }
        reportRepository.deleteById(reportId);
    }

    @Override
    public List<Report> getAllReportsForPost(Long postId) {
        if (!authenticatedUserProvider.hasAdminRole() && !authenticatedUserProvider.hasSupportRole()) {
            throw new UnauthorizedException("Only admins or support can ...");
        }
        return reportRepository.getAllReportsForPost(postId);
    }

    @Override
    public void deleteAllByReporter(UUID reporterId) {
        if (!authenticatedUserProvider.hasAdminRole() && !authenticatedUserProvider.hasSupportRole()) {
            throw new UnauthorizedException("Only admins or support can ...");
        }
        reportRepository.deleteAllByReporter(reporterId);
    }

    @Override
    public void deleteAllByPost(Long postId) {
        if (!authenticatedUserProvider.hasAdminRole() && !authenticatedUserProvider.hasSupportRole()) {
            throw new UnauthorizedException("Only admins or support can ...");
        }
        reportRepository.deleteAllByPost(postId);
    }

}

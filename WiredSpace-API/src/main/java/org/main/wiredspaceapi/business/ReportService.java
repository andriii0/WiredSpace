package org.main.wiredspaceapi.business;

import org.main.wiredspaceapi.domain.Report;

import java.util.List;
import java.util.UUID;

public interface ReportService {
    void reportPost(Long postId, UUID reporterId, String reason);
    List<Report> getAllReports(int offset, int limit);
    void deleteReport(Long reportId);
    List<Report> getAllReportsForPost(Long postId);
    void deleteAllByReporter(UUID reporterId);
    void deleteAllByPost(Long postId);
}

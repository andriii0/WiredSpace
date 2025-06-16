package org.main.wiredspaceapi.persistence;

import org.main.wiredspaceapi.domain.Report;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReportRepository {

    Report save(Report report);

    boolean alreadyReported(UUID reporterId, Long postId);

    List<Report> getAllReportsForPost(Long postId);

    List<Report> findAll(int offset, int limit);

    boolean existsById(Long id); // NEW

    void deleteById(Long id);

    void deleteAllByReporter(UUID reporterId);

    void deleteAllByPost(Long postId);
}

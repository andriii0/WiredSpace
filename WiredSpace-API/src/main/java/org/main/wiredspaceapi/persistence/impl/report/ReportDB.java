package org.main.wiredspaceapi.persistence.impl.report;

import org.main.wiredspaceapi.persistence.entity.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReportDB extends JpaRepository<ReportEntity, Long> {

    List<ReportEntity> findAllByPost_Id(Long postId);

    Optional<ReportEntity> findByReporter_IdAndPost_Id(UUID reporterId, Long postId);

    void deleteAllByReporter_Id(UUID reporterId);

    void deleteAllByPost_Id(Long postId);
}

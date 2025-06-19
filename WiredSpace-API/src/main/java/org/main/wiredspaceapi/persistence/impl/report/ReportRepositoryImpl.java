package org.main.wiredspaceapi.persistence.impl.report;

import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.domain.Report;
import org.main.wiredspaceapi.persistence.ReportRepository;
import org.main.wiredspaceapi.persistence.mapper.ReportEntityMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ReportRepositoryImpl implements ReportRepository {

    private final ReportDB reportDB;
    private final ReportEntityMapper reportEntityMapper;

    @Override
    public Report save(Report report) {
        return reportEntityMapper.toDomain(
                reportDB.save(reportEntityMapper.toEntity(report))
        );
    }

    @Override
    public boolean alreadyReported(UUID reporterId, Long postId) {
        return reportDB.findByReporter_IdAndPost_Id(reporterId, postId).isPresent();
    }

    @Override
    public List<Report> getAllReportsForPost(Long postId) {
        return reportDB.findAllByPost_Id(postId)
                .stream()
                .map(reportEntityMapper::toDomain)
                .toList();
    }

    @Transactional
    @Override
    public void deleteAllByReporter(UUID reporterId) {
        reportDB.deleteAllByReporter_Id(reporterId);
    }

    @Override
    public void deleteAllByPost(Long postId) {
        reportDB.deleteAllByPost_Id(postId);
    }

    @Override
    public List<Report> findAll(int offset, int limit) {
        Pageable pageable = PageRequest.of(offset / limit, limit); // page = offset / limit
        return reportDB.findAll(pageable)
                .stream()
                .map(reportEntityMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsById(Long id) {
        return reportDB.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        reportDB.deleteById(id);
    }
}

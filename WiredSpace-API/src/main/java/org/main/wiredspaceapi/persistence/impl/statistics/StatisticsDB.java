package org.main.wiredspaceapi.persistence.impl.statistics;

import org.main.wiredspaceapi.controller.dto.DailyStatsDto;
import org.main.wiredspaceapi.persistence.entity.AccountEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.UUID;

public interface StatisticsDB extends Repository<AccountEntity, UUID> {

    @Query(value = """
        SELECT
            DATE(a.registered_at) AS date,
            COUNT(*) AS users,
            (SELECT COUNT(*) FROM posts p WHERE DATE(p.created_at) = DATE(a.registered_at)) AS posts,
            (SELECT COUNT(*) FROM post_comments c WHERE DATE(c.created_at) = DATE(a.registered_at)) AS comments,
            (SELECT COUNT(*) FROM post_likes l WHERE DATE(l.liked_at) = DATE(a.registered_at)) AS likes,
            (SELECT COUNT(*) FROM post_reports r WHERE DATE(r.reported_at) = DATE(a.registered_at)) AS reports
        FROM accounts a
        WHERE a.registered_at >= CURRENT_DATE - INTERVAL 30 DAY
        GROUP BY DATE(a.registered_at)
        ORDER BY DATE(a.registered_at)
        """, nativeQuery = true)
    List<DailyStatsDto> getLast30DaysStats();


    @Query(value = """
        SELECT
            DATE(a.registered_at) AS date,
            COUNT(*) AS users,
            (SELECT COUNT(*) FROM posts p WHERE DATE(p.created_at) = DATE(a.registered_at)) AS posts,
            (SELECT COUNT(*) FROM post_comments c WHERE DATE(c.created_at) = DATE(a.registered_at)) AS comments,
            (SELECT COUNT(*) FROM post_likes l WHERE DATE(l.liked_at) = DATE(a.registered_at)) AS likes,
            (SELECT COUNT(*) FROM post_reports r WHERE DATE(r.reported_at) = DATE(a.registered_at)) AS reports
        FROM accounts a
        WHERE a.registered_at >= CURRENT_DATE - INTERVAL 7 DAY
        GROUP BY DATE(a.registered_at)
        ORDER BY DATE(a.registered_at)
        """, nativeQuery = true)
    List<DailyStatsDto> getLast7DaysStats();
}

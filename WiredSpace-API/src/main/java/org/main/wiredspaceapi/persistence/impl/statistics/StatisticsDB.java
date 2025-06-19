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
        DATE_SUB(CURDATE(), INTERVAL n DAY) AS date,
        (SELECT COUNT(*) FROM accounts a WHERE DATE(a.registered_at) = DATE_SUB(CURDATE(), INTERVAL n DAY)) AS users,
        (SELECT COUNT(*) FROM posts p WHERE DATE(p.created_at) = DATE_SUB(CURDATE(), INTERVAL n DAY)) AS posts,
        (SELECT COUNT(*) FROM post_comments c WHERE DATE(c.created_at) = DATE_SUB(CURDATE(), INTERVAL n DAY)) AS comments,
        (SELECT COUNT(*) FROM post_likes l WHERE DATE(l.liked_at) = DATE_SUB(CURDATE(), INTERVAL n DAY)) AS likes,
        (SELECT COUNT(*) FROM post_reports r WHERE DATE(r.reported_at) = DATE_SUB(CURDATE(), INTERVAL n DAY)) AS reports
    FROM (
        SELECT 0 AS n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL
        SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL
        SELECT 10 UNION ALL SELECT 11 UNION ALL SELECT 12 UNION ALL SELECT 13 UNION ALL SELECT 14 UNION ALL
        SELECT 15 UNION ALL SELECT 16 UNION ALL SELECT 17 UNION ALL SELECT 18 UNION ALL SELECT 19 UNION ALL
        SELECT 20 UNION ALL SELECT 21 UNION ALL SELECT 22 UNION ALL SELECT 23 UNION ALL SELECT 24 UNION ALL
        SELECT 25 UNION ALL SELECT 26 UNION ALL SELECT 27 UNION ALL SELECT 28 UNION ALL SELECT 29
    ) AS days
    ORDER BY date
    """, nativeQuery = true)
    List<DailyStatsDto> getLast30DaysStats();

    @Query(value = """
    SELECT
        DATE_SUB(CURDATE(), INTERVAL n DAY) AS date,
        (SELECT COUNT(*) FROM accounts a WHERE DATE(a.registered_at) = DATE_SUB(CURDATE(), INTERVAL n DAY)) AS users,
        (SELECT COUNT(*) FROM posts p WHERE DATE(p.created_at) = DATE_SUB(CURDATE(), INTERVAL n DAY)) AS posts,
        (SELECT COUNT(*) FROM post_comments c WHERE DATE(c.created_at) = DATE_SUB(CURDATE(), INTERVAL n DAY)) AS comments,
        (SELECT COUNT(*) FROM post_likes l WHERE DATE(l.liked_at) = DATE_SUB(CURDATE(), INTERVAL n DAY)) AS likes,
        (SELECT COUNT(*) FROM post_reports r WHERE DATE(r.reported_at) = DATE_SUB(CURDATE(), INTERVAL n DAY)) AS reports
    FROM (
        SELECT 0 AS n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL
        SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6
    ) AS days
    ORDER BY date
    """, nativeQuery = true)
    List<DailyStatsDto> getLast7DaysStats();
}

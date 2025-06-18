package org.main.wiredspaceapi.persistence.impl;

import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.controller.dto.DailyStatsDto;
import org.main.wiredspaceapi.persistence.StatisticsRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class StatisticsRepositoryImpl implements StatisticsRepository {

    private final StatisticsDB statisticsDB;

    @Override
    public List<DailyStatsDto> getLast30DaysStats() {
        return statisticsDB.getLast30DaysStats();
    }

    @Override
    public List<DailyStatsDto> getLast7DaysStats() {
        return statisticsDB.getLast7DaysStats();
    }
}

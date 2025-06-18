package org.main.wiredspaceapi.persistence;

import org.main.wiredspaceapi.controller.dto.DailyStatsDto;

import java.util.List;

public interface StatisticsRepository {
    List<DailyStatsDto> getLast30DaysStats();
    List<DailyStatsDto> getLast7DaysStats();
}
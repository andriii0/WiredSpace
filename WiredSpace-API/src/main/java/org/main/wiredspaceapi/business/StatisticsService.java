package org.main.wiredspaceapi.business;

import org.main.wiredspaceapi.controller.dto.DailyStatsDto;

import java.util.List;

public interface StatisticsService {

    List<DailyStatsDto> getLast7DaysStats();

    List<DailyStatsDto> getLast30DaysStats();
}

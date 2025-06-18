package org.main.wiredspaceapi.business.impl;

import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.business.StatisticsService;
import org.main.wiredspaceapi.controller.dto.DailyStatsDto;
import org.main.wiredspaceapi.persistence.StatisticsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final StatisticsRepository statisticsRepository;

    @Override
    public List<DailyStatsDto> getLast30DaysStats() {
        return statisticsRepository.getLast30DaysStats();
    }

    @Override
    public List<DailyStatsDto> getLast7DaysStats() {
        return statisticsRepository.getLast7DaysStats();
    }
}

package org.main.wiredspaceapi.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.main.wiredspaceapi.business.impl.StatisticsServiceImpl;
import org.main.wiredspaceapi.controller.dto.DailyStatsDto;
import org.main.wiredspaceapi.persistence.StatisticsRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.sql.Date;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

    @Mock
    private StatisticsRepository statisticsRepository;

    @InjectMocks
    private StatisticsServiceImpl statisticsService;

    private List<DailyStatsDto> mockStats;

    @BeforeEach
    void setUp() {
        mockStats = List.of(
                new DailyStatsDto(Date.valueOf(LocalDate.now().minusDays(1)), 5, 10, 3, 2, 1),
                new DailyStatsDto(Date.valueOf(LocalDate.now()), 7, 12, 4, 3, 2)
        );
    }

    @Test
    void getLast30DaysStats_shouldReturnListOfStats() {
        when(statisticsRepository.getLast30DaysStats()).thenReturn(mockStats);

        List<DailyStatsDto> result = statisticsService.getLast30DaysStats();

        assertEquals(mockStats, result);
        verify(statisticsRepository).getLast30DaysStats();
    }

    @Test
    void getLast7DaysStats_shouldReturnListOfStats() {
        when(statisticsRepository.getLast7DaysStats()).thenReturn(mockStats);

        List<DailyStatsDto> result = statisticsService.getLast7DaysStats();

        assertEquals(mockStats, result);
        verify(statisticsRepository).getLast7DaysStats();
    }
}

package org.main.wiredspaceapi.controller.dto;

import java.time.LocalDate;

public record DailyStatsDto(
        LocalDate date,
        long users,
        long posts,
        long comments,
        long likes,
        long reports
) {}
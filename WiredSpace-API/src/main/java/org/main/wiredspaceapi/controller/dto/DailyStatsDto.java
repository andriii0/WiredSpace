package org.main.wiredspaceapi.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.sql.Date;

@Data
@AllArgsConstructor
public class DailyStatsDto {
    private Date date;
    private long users;
    private long posts;
    private long comments;
    private long likes;
    private long reports;
}

package org.main.wiredspaceapi.controller.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserStatisticsDTO {
    private int commentsCount;
    private int likesGiven;
    private int friendsCount;
    private int postsCount;
}
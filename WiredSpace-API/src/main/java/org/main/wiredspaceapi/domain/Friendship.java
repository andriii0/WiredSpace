package org.main.wiredspaceapi.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class Friendship {
    private UUID id;
    private UUID userId;
    private UUID friendId;
    private boolean accepted;
}

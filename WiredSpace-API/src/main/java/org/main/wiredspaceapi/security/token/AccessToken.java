package org.main.wiredspaceapi.security.token;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.*;

@Getter
@EqualsAndHashCode
@Builder
public class AccessToken {
    private String subject;
    private UUID accountId;
    //private final Set<String> roles;
}

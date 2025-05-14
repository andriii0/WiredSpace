package org.main.wiredspaceapi.security.token.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidRefreshTokenException extends ResponseStatusException {
    public InvalidRefreshTokenException() {
        super(HttpStatus.UNAUTHORIZED, "Invalid user.");
    }
}

package org.main.wiredspaceapi.controller.exceptions;

public class UnauthorizedPostActionException extends RuntimeException {
    public UnauthorizedPostActionException(String message) {
        super(message);
    }
}

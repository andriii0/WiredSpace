package org.main.wiredspaceapi.controller.exceptions;

public class UnauthorizedFriendshipAccessException extends RuntimeException {
    public UnauthorizedFriendshipAccessException(String message) {
        super(message);
    }
}
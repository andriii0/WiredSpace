package org.main.wiredspaceapi.controller.exceptions;

public class FriendshipNotFoundException extends RuntimeException {
    public FriendshipNotFoundException(String message) {
        super(message);
    }
}
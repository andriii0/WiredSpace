package org.main.wiredspaceapi.controller.exceptions;

public class FriendshipAlreadyExistsException extends RuntimeException {
    public FriendshipAlreadyExistsException(String message) {
        super(message);
    }
}
package org.main.wiredspaceapi.controller.exceptions;

public class FriendshipAlreadyAcceptedException extends RuntimeException {
    public FriendshipAlreadyAcceptedException(String message) {
        super(message);
    }
}
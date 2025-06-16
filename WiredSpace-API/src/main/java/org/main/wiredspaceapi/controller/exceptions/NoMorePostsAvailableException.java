package org.main.wiredspaceapi.controller.exceptions;

public class NoMorePostsAvailableException extends RuntimeException {
    public NoMorePostsAvailableException(String message) {
        super(message);
    }
}

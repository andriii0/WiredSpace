package org.main.wiredspaceapi.controller.exceptions;

public class InvalidPostContentException extends RuntimeException {
    public InvalidPostContentException(String message) {
        super(message);
    }
}

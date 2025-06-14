package org.main.wiredspaceapi.controller.exceptions;

public class InvalidCommentContentException extends RuntimeException {
    public InvalidCommentContentException(String message) {
        super(message);
    }
}

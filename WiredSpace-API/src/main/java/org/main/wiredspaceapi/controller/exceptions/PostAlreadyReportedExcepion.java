package org.main.wiredspaceapi.controller.exceptions;

public class PostAlreadyReportedExcepion extends RuntimeException {
    public PostAlreadyReportedExcepion(String message) {
        super(message);
    }
}

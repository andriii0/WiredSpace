package org.main.wiredspaceapi.controller.exceptions;

public class SelfDemotionException extends RuntimeException {
    public SelfDemotionException(String message) {
        super(message);
    }
}

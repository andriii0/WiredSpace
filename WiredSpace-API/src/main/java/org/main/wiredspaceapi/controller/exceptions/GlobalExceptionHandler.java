package org.main.wiredspaceapi.controller.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFound(UserNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleInvalidCredentials(InvalidCredentialsException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(AccountAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleAccountExists(AccountAlreadyExistsException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneric(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
    }

    private ResponseEntity<Map<String, String>> buildResponse(HttpStatus status, String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return new ResponseEntity<>(error, status);
    }
    @ExceptionHandler(AdminNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleAdminNotFound(AdminNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(UserPromotionException.class)
    public ResponseEntity<Map<String, String>> handleUserPromotion(UserPromotionException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(SelfDemotionException.class)
    public ResponseEntity<Map<String, String>> handleSelfDemotion(SelfDemotionException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDenied(AccessDeniedException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }
    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleCommentNotFound(CommentNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<Map<String, String>> handlePostNotFound(PostNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(InvalidCommentContentException.class)
    public ResponseEntity<Map<String, String>> handleInvalidComment(InvalidCommentContentException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(InvalidPostContentException.class)
    public ResponseEntity<Map<String, String>> handleInvalidPostContent(InvalidPostContentException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedPostActionException.class)
    public ResponseEntity<Map<String, String>> handleUnauthorizedAction(UnauthorizedPostActionException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }
    @ExceptionHandler(FriendshipNotFoundException.class)
    public ResponseEntity<String> handleFriendshipNotFound(FriendshipNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(FriendshipAlreadyExistsException.class)
    public ResponseEntity<String> handleFriendshipExists(FriendshipAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(FriendshipAlreadyAcceptedException.class)
    public ResponseEntity<String> handleFriendshipAlreadyAccepted(FriendshipAlreadyAcceptedException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedFriendshipAccessException.class)
    public ResponseEntity<String> handleUnauthorizedAccess(UnauthorizedFriendshipAccessException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, String>> handleUnauthorized(UnauthorizedException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }
    @ExceptionHandler(ReportNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleReportNotFound(ReportNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }
    @ExceptionHandler(PostAlreadyReportedExcepion.class)
    public ResponseEntity<Map<String, String>> handleReportNotFound(PostAlreadyReportedExcepion ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }
}
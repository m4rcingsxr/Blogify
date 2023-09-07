package com.blogify.exception;

import com.blogify.payload.ErrorResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex) {
        log.debug("Handling ApiException", ex);

        return handleApiExceptionInternal(ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        log.debug("Handling all exceptions", ex);

        ErrorResponse error = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                                ex.getMessage(), Instant.now().getEpochSecond()
        );

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ErrorResponse> handleException(LockedException exp) {
        return ResponseEntity.status(UNAUTHORIZED).body(
                ErrorResponse.builder().timestamp(Instant.now().getEpochSecond()).status(
                        UNAUTHORIZED.value()).message(exp.getMessage()).build());
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponse> handleException(DisabledException exp) {
        return ResponseEntity.status(UNAUTHORIZED).body(
                ErrorResponse.builder().timestamp(Instant.now().getEpochSecond()).status(
                        UNAUTHORIZED.value()).message(exp.getMessage()).build());
    }


    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleException() {
        return ResponseEntity.status(UNAUTHORIZED).body(
                ErrorResponse.builder().timestamp(Instant.now().getEpochSecond()).status(
                        UNAUTHORIZED.value()).message("Bad credentials").build());
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex) {
        log.debug("Handling forbidden exception", ex);

        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage(),
                                                Instant.now().getEpochSecond()
        );

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(AccessDeniedException ex) {
        log.debug("Handling forbidden exception", ex);

        ErrorResponse error = new ErrorResponse(HttpStatus.FORBIDDEN.value(), ex.getMessage(),
                                                Instant.now().getEpochSecond()
        );

        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }


    private ResponseEntity<ErrorResponse> handleApiExceptionInternal(ApiException ex) {
        log.debug("Assigning correct status to ResponseEntity", ex);

        ErrorResponse errorResponse;
        HttpStatus status = ex.getStatus();

        switch (status) {
            case NOT_FOUND:
                errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage(),
                                                  Instant.now().getEpochSecond()
                );
                break;

            case BAD_REQUEST:
                errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage(),
                                                  Instant.now().getEpochSecond()
                );
                break;

            case METHOD_NOT_ALLOWED:
                errorResponse = new ErrorResponse(HttpStatus.METHOD_NOT_ALLOWED.value(),
                                                  ex.getMessage(), Instant.now().getEpochSecond()
                );
                break;

            default:
                errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                                  ex.getMessage(), Instant.now().getEpochSecond()
                );
                status = HttpStatus.INTERNAL_SERVER_ERROR;
                break;
        }

        log.debug("Finish handling ApiException", ex);
        log.debug("Status {}", status);
        log.debug("ErrorResponse {}", errorResponse);

        return new ResponseEntity<>(errorResponse, status);
    }
}

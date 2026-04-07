package com.pm.preciousmetals.api.error;

import io.micrometer.tracing.Tracer;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Optional;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private final Tracer tracer;
    private final HttpServletRequest request;

    public GlobalExceptionHandler(Optional<Tracer> tracer, HttpServletRequest request) {
        this.tracer = tracer.orElse(null);
        this.request = request;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        String message;
        try {
            message = ex.getBindingResult().getFieldErrors().getFirst().getDefaultMessage();
        } catch (Exception e) {
            message = ex.getMessage();
        }

        ApiError error = new ApiError(
                "VALIDATION_FAILED",
                message,
                request.getRequestURI(),
                getTraceId(),
                LocalDateTime.now()
        );

        log.warn("Validation error [traceId: {}]: {} at {}", error.traceId(), message, error.path());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        String message = "Malformed JSON request";
        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("Invalid metal type")) {
                message = ex.getMessage().substring(ex.getMessage().indexOf("Invalid metal type"));
            } else if (ex.getMessage().contains("Metal type cannot be empty")) {
                message = "Metal type cannot be empty";
            } else if (ex.getMessage().contains("Price must be positive")) {
                message = "Price must be positive";
            }
            
            if (message.contains(";")) {
                message = message.substring(0, message.indexOf(";"));
            }
        }

        ApiError error = new ApiError(
                "BAD_REQUEST",
                message,
                request.getRequestURI(),
                getTraceId(),
                LocalDateTime.now()
        );

        log.warn("Malformed JSON [traceId: {}]: {} at {}", error.traceId(), ex.getMessage(), error.path());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAll(Exception ex) {
        ApiError error = new ApiError(
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred",
                request.getRequestURI(),
                getTraceId(),
                LocalDateTime.now()
        );

        log.error("Critical error [traceId: {}]: ", error.traceId(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    private String getTraceId() {
        try {
            String traceId = Optional.ofNullable(tracer)
                    .map(Tracer::currentSpan)
                    .map(span -> span.context().traceId())
                    .orElse(null);
            
            if (traceId == null) {
                traceId = org.slf4j.MDC.get("traceId");
            }
            
            return traceId != null ? traceId : "no-trace";
        } catch (Exception e) {
            log.error("Error getting traceId: ", e);
            return "error-trace";
        }
    }
}
package com.ticketing.orderservice.exception;

import com.ticketing.orderservice.dto.ErrorResponse;
import com.ticketing.orderservice.util.AuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final AuditService auditService;

    public GlobalExceptionHandler(AuditService auditService) {
        this.auditService = auditService;
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException ex) {
        String traceId = MDC.get("traceId");
        logger.error("Unauthorized access attempt", ex);
        auditService.logError("UNAUTHORIZED", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                "UNAUTHORIZED",
                "Authentication required or invalid credentials",
                Instant.now(),
                traceId
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(OrderAccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleOrderAccessDeniedException(OrderAccessDeniedException ex) {
        String traceId = MDC.get("traceId");
        logger.error("Order access denied", ex);
        auditService.logError("ACCESS_DENIED", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                "ACCESS_DENIED",
                "You do not have permission to access this order",
                Instant.now(),
                traceId
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler({OrderNotFoundException.class, TierNotFoundException.class, EventNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFoundException(RuntimeException ex) {
        String traceId = MDC.get("traceId");
        logger.error("Resource not found", ex);
        auditService.logError("NOT_FOUND", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                "NOT_FOUND",
                ex.getMessage(),
                Instant.now(),
                traceId
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler({InsufficientInventoryException.class, InvalidTierStatusException.class,
                       InvalidEventStatusException.class})
    public ResponseEntity<ErrorResponse> handleConflictException(RuntimeException ex) {
        String traceId = MDC.get("traceId");
        logger.error("Conflict in order creation", ex);
        auditService.logError("CONFLICT", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                "CONFLICT",
                ex.getMessage(),
                Instant.now(),
                traceId
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(QuantityExceedsMaxPerOrderException.class)
    public ResponseEntity<ErrorResponse> handleQuantityExceedsMaxPerOrderException(QuantityExceedsMaxPerOrderException ex) {
        String traceId = MDC.get("traceId");
        logger.error("Quantity validation failed", ex);
        auditService.logError("VALIDATION_FAILED", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                "VALIDATION_FAILED",
                ex.getMessage(),
                Instant.now(),
                traceId
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String traceId = MDC.get("traceId");
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        logger.error("Validation failed: {}", errorMessage);
        auditService.logError("VALIDATION_FAILED", errorMessage);

        ErrorResponse error = new ErrorResponse(
                "VALIDATION_FAILED",
                errorMessage,
                Instant.now(),
                traceId
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(PaymentServiceException.class)
    public ResponseEntity<ErrorResponse> handlePaymentServiceException(PaymentServiceException ex) {
        String traceId = MDC.get("traceId");
        logger.error("Payment service error", ex);
        auditService.logError("PAYMENT_ERROR", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                "PAYMENT_ERROR",
                "Payment service error occurred",
                Instant.now(),
                traceId
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        String traceId = MDC.get("traceId");
        logger.error("Unexpected error occurred", ex);
        auditService.logError("INTERNAL_ERROR", "An unexpected error occurred");

        ErrorResponse error = new ErrorResponse(
                "INTERNAL_ERROR",
                "An unexpected error occurred",
                Instant.now(),
                traceId
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

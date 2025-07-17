package com.acme.insurance.policy.api.interfaceadapters.exception;

import com.acme.insurance.policy.api.domain.exception.PolicyBadRequestException;
import com.acme.insurance.policy.api.domain.exception.PolicyNotFoundException;
import com.acme.insurance.policy.api.interfaceadapters.dto.ErrorDetailsDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PolicyNotFoundException.class)
    public ResponseEntity<ErrorDetailsDTO> handlePolicyNotFoundException(PolicyNotFoundException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(PolicyBadRequestException.class)
    public ResponseEntity<ErrorDetailsDTO> handleInvalidPolicyException(PolicyBadRequestException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    private ResponseEntity<ErrorDetailsDTO> buildErrorResponse(HttpStatus status, String message) {
        ErrorDetailsDTO errorDetails = new ErrorDetailsDTO(status.value(), message);
        return new ResponseEntity<>(errorDetails, status);
    }
}

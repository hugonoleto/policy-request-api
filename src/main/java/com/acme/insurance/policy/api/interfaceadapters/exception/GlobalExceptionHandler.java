package com.acme.insurance.policy.api.interfaceadapters.exception;

import com.acme.insurance.policy.api.domain.exception.PolicyBadRequestException;
import com.acme.insurance.policy.api.domain.exception.PolicyNotFoundException;
import com.acme.insurance.policy.api.interfaceadapters.dto.ErrorDetailsDTO;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String VALIDATION_MESSAGE = "Erro de validação nos campos fornecidos.";

    @ExceptionHandler(PolicyNotFoundException.class)
    public ResponseEntity<ErrorDetailsDTO> handlePolicyNotFoundException(PolicyNotFoundException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    @ExceptionHandler(PolicyBadRequestException.class)
    public ResponseEntity<ErrorDetailsDTO> handleInvalidPolicyException(PolicyBadRequestException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetailsDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach
                ((error) -> errors.put(error.getField(), error.getDefaultMessage()));

        return buildErrorResponse(HttpStatus.BAD_REQUEST, VALIDATION_MESSAGE, errors);
    }

    private ResponseEntity<ErrorDetailsDTO> buildErrorResponse(HttpStatus status, String message, Map<String, String> errors) {
        ErrorDetailsDTO.ErrorDetailsDTOBuilder builder = ErrorDetailsDTO.builder()
                .statusCode(status.value())
                .message(message);

        if (ObjectUtils.isNotEmpty(errors)) {
            builder.errors(errors);
        }

        return new ResponseEntity<>(builder.build(), status);
    }
}
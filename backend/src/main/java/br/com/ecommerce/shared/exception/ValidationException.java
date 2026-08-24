package br.com.ecommerce.shared.exception;

import org.springframework.http.HttpStatus;

public class ValidationException extends BusinessException {

    public ValidationException(String message) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY, "VALIDATION_ERROR");
    }
}
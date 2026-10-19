package com.example.diagnostico_api.exeption;

/**
 * Violação de regra de negócio (HTTP 400).
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
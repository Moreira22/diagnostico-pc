package com.example.diagnostico_api.exeption;

/**
 * Conflito com o estado atual dos dados, ex.: e-mail já cadastrado (HTTP 409).
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
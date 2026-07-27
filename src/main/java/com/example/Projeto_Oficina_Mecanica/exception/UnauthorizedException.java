package com.example.Projeto_Oficina_Mecanica.exception;

/**
 * Exceção utilizada quando o usuário não está autenticado.
 *
 * O tratamento HTTP desta exceção é feito centralmente em
 * {@link GlobalExceptionHandler}.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}


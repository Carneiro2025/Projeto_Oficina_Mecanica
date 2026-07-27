package com.example.Projeto_Oficina_Mecanica.exception;

/**
 * Exceção utilizada quando o usuário está autenticado,
 * porém não possui permissão para executar determinada ação.
 *
 * O tratamento HTTP desta exceção é feito centralmente em
 * {@link GlobalExceptionHandler}.
 */
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}


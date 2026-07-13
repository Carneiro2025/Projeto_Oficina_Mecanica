package com.example.Projeto_Oficina_Mecanica.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.Projeto_Oficina_Mecanica.dto.response.ApiErrorResponseDTO;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Exceção utilizada quando o usuário está autenticado,
 * porém não possui permissão para executar determinada ação.
 */
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleForbidden(
        ForbiddenException ex,
        HttpServletRequest req) {

    return build(
            HttpStatus.FORBIDDEN,
            "Acesso negado",
            ex.getMessage(),
            req,
            null
    );
}

}


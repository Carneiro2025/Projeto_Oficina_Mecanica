package com.example.Projeto_Oficina_Mecanica.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.Projeto_Oficina_Mecanica.dto.response.ApiErrorResponseDTO;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Exceção utilizada quando o usuário não está autenticado.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleUnauthorized(
        UnauthorizedException ex,
        HttpServletRequest req) {

    return build(
            HttpStatus.UNAUTHORIZED,
            "Não autorizado",
            ex.getMessage(),
            req,
            null
    );
}

}


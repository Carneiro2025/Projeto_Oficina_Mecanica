package com.example.Projeto_Oficina_Mecanica.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO utilizado para padronizar as respostas de erro da API.
 *
 * Todas as exceções lançadas pela aplicação serão convertidas
 * para este formato pelo GlobalExceptionHandler.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    /**
     * Data e hora do erro.
     */
    private LocalDateTime timestamp;

    /**
     * Código HTTP.
     */
    private Integer status;

    /**
     * Nome do erro HTTP.
     */
    private String error;

    /**
     * Mensagem amigável.
     */
    private String message;

    /**
     * Endpoint que gerou o erro.
     */
    private String path;

}
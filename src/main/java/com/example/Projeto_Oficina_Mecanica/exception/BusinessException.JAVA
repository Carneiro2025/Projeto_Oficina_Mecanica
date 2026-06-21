package com.example.Projeto_Oficina_Mecanica.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção para violações de regras de negócio.
 * Ex: CPF duplicado, estoque zerado, OS em andamento.
 */
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class BusinessException extends RuntimeException {

    public BusinessException(String mensagem) {
        super(mensagem);
    }
}

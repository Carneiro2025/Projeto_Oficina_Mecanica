package com.example.Projeto_Oficina_Mecanica.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção 404 - recurso não encontrado.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String recurso, Long id) {
        super(recurso + " não encontrado(a) com ID: " + id);
    }

    public ResourceNotFoundException(String mensagem) {
        super(mensagem);
    }
}

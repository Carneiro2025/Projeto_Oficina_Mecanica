package com.example.Projeto_Oficina_Mecanica.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.Projeto_Oficina_Mecanica.dto.response.ApiErrorResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Handler global de exceções.
 * Padroniza todas as respostas de erro da API.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── 404 ──────────────────────────────────────────────────────────────────
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleNotFound(
            ResourceNotFoundException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, "Recurso não encontrado", ex.getMessage(), req, null);
    }

    // ── 422 Regra de Negócio ──────────────────────────────────────────────────
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleBusiness(
            BusinessException ex, HttpServletRequest req) {
        return build(HttpStatus.UNPROCESSABLE_ENTITY, "Erro de negócio", ex.getMessage(), req, null);
    }

    // ── 400 Validação ─────────────────────────────────────────────────────────
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<String> erros = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .toList();
        return build(HttpStatus.BAD_REQUEST, "Dados inválidos", "Verifique os campos", req, erros);
    }

    // ── 401 Autenticação ──────────────────────────────────────────────────────
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleBadCredentials(
            BadCredentialsException ex, HttpServletRequest req) {
        return build(HttpStatus.UNAUTHORIZED, "Não autorizado", "E-mail ou senha inválidos", req, null);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleDisabled(
            DisabledException ex, HttpServletRequest req) {
        return build(HttpStatus.UNAUTHORIZED, "Conta inativa", "Usuário desativado no sistema", req, null);
    }

    // ── 403 Acesso negado ────────────────────────────────────────────────────
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest req) {
        return build(HttpStatus.FORBIDDEN, "Acesso negado",
                "Você não tem permissão para acessar este recurso", req, null);
    }

    // ── 500 Erro inesperado ───────────────────────────────────────────────────
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponseDTO> handleGeneral(
            Exception ex, HttpServletRequest req) {
        log.error("Erro inesperado: {}", ex.getMessage(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno",
                "Ocorreu um erro inesperado. Contate o suporte.", req, null);
    }

    // ── Helper ───────────────────────────────────────────────────────────────
    private ResponseEntity<ApiErrorResponseDTO> build(
            HttpStatus status, String erro, String mensagem,
            HttpServletRequest req, List<String> detalhes) {

        ApiErrorResponseDTO body = ApiErrorResponseDTO.builder()
                .status(status.value())
                .erro(erro)
                .mensagem(mensagem)
                .caminho(req.getRequestURI())
                .timestamp(LocalDateTime.now())
                .detalhes(detalhes)
                .build();

        return ResponseEntity.status(status).body(body);
    }
}
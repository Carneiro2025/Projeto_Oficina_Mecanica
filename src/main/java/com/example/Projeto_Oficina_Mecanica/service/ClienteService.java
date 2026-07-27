package com.example.Projeto_Oficina_Mecanica.service;

import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarClienteRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarClienteRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.ClienteResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Interface de serviço responsável pelas regras de negócio do módulo Cliente.
 */
public interface ClienteService {

    /**
     * Cadastra um novo cliente.
     */
    ClienteResponseDTO criar(CriarClienteRequestDTO dto);

    /**
     * Lista clientes com filtros opcionais.
     */
    Page<ClienteResponseDTO> listar(
            String nome,
            String cpfCnpj,
            Boolean ativo,
            Pageable pageable
    );

    /**
     * Busca um cliente pelo ID.
     */
    ClienteResponseDTO buscarPorId(Long id);

    /**
     * Atualiza os dados do cliente.
     */
    ClienteResponseDTO atualizar(
            Long id,
            AtualizarClienteRequestDTO dto
    );

    /**
     * Ativa um cliente.
     */
    ClienteResponseDTO reativar(Long id);

    /**
     * Desativa um cliente (Soft Delete).
     */
    void desativar(Long id);

}
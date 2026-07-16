package com.example.Projeto_Oficina_Mecanica.service;

import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarVeiculoRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarVeiculoRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.VeiculoResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VeiculoService {

    /**
     * Cadastra um veículo.
     */
    VeiculoResponseDTO criar(CriarVeiculoRequestDTO dto);

    /**
     * Lista veículos utilizando filtros.
     */
    Page<VeiculoResponseDTO> listar(
            String placa,
            String modelo,
            Long clienteId,
            Boolean ativo,
            Pageable pageable);

    /**
     * Busca um veículo pelo ID.
     */
    VeiculoResponseDTO buscarPorId(Long id);

    /**
     * Atualiza um veículo.
     */
    VeiculoResponseDTO atualizar(
            Long id,
            AtualizarVeiculoRequestDTO dto);

    /**
     * Desativa um veículo.
     */
    void desativar(Long id);

    /**
     * Reativa um veículo.
     */
    VeiculoResponseDTO reativar(Long id);

}

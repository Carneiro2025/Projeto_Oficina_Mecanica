package com.example.Projeto_Oficina_Mecanica.service;

import com.example.Projeto_Oficina_Mecanica.dto.request.RecebimentoRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.RecebimentoResponseDTO;

import java.util.List;

public interface RecebimentoService {

    /**
     * Registra o pagamento de um recebimento.
     */
    RecebimentoResponseDTO registrarPagamento(RecebimentoRequestDTO dto);

    /**
     * Busca um recebimento pelo ID.
     */
    RecebimentoResponseDTO buscarPorId(Long id);

    /**
     * Lista todos os recebimentos.
     */
    List<RecebimentoResponseDTO> listar();

    /**
     * Lista recebimentos pendentes.
     */
    List<RecebimentoResponseDTO> listarPendentes();

    /**
     * Lista recebimentos de um cliente.
     */
    List<RecebimentoResponseDTO> listarPorCliente(Long clienteId);

}

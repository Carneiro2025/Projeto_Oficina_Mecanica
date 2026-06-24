package com.example.Projeto_Oficina_Mecanica.service;

import com.example.Projeto_Oficina_Mecanica.dto.request.OrdemServicoRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.OrdemServicoResponseDTO;

import java.util.List;

public interface OrdemServicoService {

    OrdemServicoResponseDTO abrir(
            OrdemServicoRequestDTO dto
    );

    OrdemServicoResponseDTO buscarPorId(
            Long id
    );

    List<OrdemServicoResponseDTO> listar();

    OrdemServicoResponseDTO finalizar(
            Long id
    );

    void cancelar(
            Long id
    );
}

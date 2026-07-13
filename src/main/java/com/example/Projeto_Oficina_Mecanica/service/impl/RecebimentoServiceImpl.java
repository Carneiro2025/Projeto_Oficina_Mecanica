package com.example.Projeto_Oficina_Mecanica.service.impl;

import com.example.Projeto_Oficina_Mecanica.dto.request.RecebimentoRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.RecebimentoResponseDTO;
import com.example.Projeto_Oficina_Mecanica.entity.Recebimento;
import com.example.Projeto_Oficina_Mecanica.repository.RecebimentoRepository;
import com.example.Projeto_Oficina_Mecanica.service.RecebimentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RecebimentoServiceImpl implements RecebimentoService {

    private final RecebimentoRepository recebimentoRepository;

    @Override
    public RecebimentoResponseDTO registrarPagamento(RecebimentoRequestDTO dto) {

       Recebimento recebimento = recebimentoRepository
        .findByOrdemServicoId(dto.getOrdemServicoId())
        .orElseThrow(() ->
                new RuntimeException("Recebimento não encontrado."));

        recebimento.registrarPagamento(dto.getFormaPagamento());

        recebimentoRepository.save(recebimento);

        return converter(recebimento);
    }

    @Override
    @Transactional(readOnly = true)
    public RecebimentoResponseDTO buscarPorId(Long id) {

        Recebimento recebimento = recebimentoRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Recebimento não encontrado."));

        return converter(recebimento);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecebimentoResponseDTO> listar() {

        return recebimentoRepository.findAll()
                .stream()
                .map(this::converter)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecebimentoResponseDTO> listarPendentes() {

        return recebimentoRepository.findByStatus(
                        com.example.Projeto_Oficina_Mecanica.enums.StatusRecebimento.PENDENTE)
                .stream()
                .map(this::converter)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecebimentoResponseDTO> listarPorCliente(Long clienteId) {

        return recebimentoRepository.findByClienteId(clienteId)
                .stream()
                .map(this::converter)
                .toList();
    }

    /**
     * Converte Entity para DTO.
     */
    private RecebimentoResponseDTO converter(Recebimento recebimento) {

        return RecebimentoResponseDTO.builder()
                .id(recebimento.getId())
                .ordemServico(recebimento.getOrdemServico().getId())
                .cliente(recebimento.getCliente().getNome())
                .valor(recebimento.getValor())
                .formaPagamento(recebimento.getFormaPagamento())
                .status(recebimento.getStatus())
                .dataVencimento(recebimento.getDataVencimento())
                .dataPagamento(recebimento.getDataPagamento())
                .build();
    }

}


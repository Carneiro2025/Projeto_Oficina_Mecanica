package com.example.Projeto_Oficina_Mecanica.service.impl;

import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarVeiculoRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarVeiculoRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.VeiculoResponseDTO;
import com.example.Projeto_Oficina_Mecanica.entity.Cliente;
import com.example.Projeto_Oficina_Mecanica.entity.Veiculo;
import com.example.Projeto_Oficina_Mecanica.exception.BusinessException;
import com.example.Projeto_Oficina_Mecanica.exception.ResourceNotFoundException;
import com.example.Projeto_Oficina_Mecanica.mapper.VeiculoMapper;
import com.example.Projeto_Oficina_Mecanica.repository.ClienteRepository;
import com.example.Projeto_Oficina_Mecanica.repository.VeiculoRepository;
import com.example.Projeto_Oficina_Mecanica.service.VeiculoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ===========================================================
 * SERVICE - VEÍCULO
 * ===========================================================
 *
 * Regras de Negócio
 *
 * RN01 - Todo veículo pertence a um cliente.
 *
 * RN02 - A placa deve ser única.
 *
 * RN03 - O chassi deve ser único.
 *
 * RN04 - O Renavam deve ser único.
 *
 * RN05 - A quilometragem nunca poderá diminuir.
 *
 * RN06 - Exclusão lógica utilizando o campo ativo.
 *
 * RN07 - Cliente inativo não pode receber veículos.
 *
 * RN08 - Apenas veículos ativos podem abrir Ordem de Serviço.
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VeiculoServiceImpl implements VeiculoService {

    private final VeiculoRepository veiculoRepository;

    private final ClienteRepository clienteRepository;

    private final VeiculoMapper veiculoMapper;

    // ==========================================================
    // CRIAR
    // ==========================================================

    @Override
    @Transactional
    public VeiculoResponseDTO criar(CriarVeiculoRequestDTO dto) {

        validarPlaca(dto.getPlaca());

        if (dto.getChassi() != null &&
                veiculoRepository.existsByChassi(dto.getChassi())) {

            throw new BusinessException(
                    "Já existe um veículo cadastrado com este chassi.");
        }

        if (dto.getRenavam() != null &&
                veiculoRepository.existsByRenavam(dto.getRenavam())) {

            throw new BusinessException(
                    "Já existe um veículo cadastrado com este RENAVAM.");
        }

        Cliente cliente = buscarCliente(dto.getClienteId());

        if (!cliente.getAtivo()) {

            throw new BusinessException(
                    "Não é permitido cadastrar veículos para clientes inativos.");
        }

        Veiculo veiculo = veiculoMapper.toEntity(dto);

        veiculo.setCliente(cliente);

        veiculo.setAtivo(true);

        Veiculo salvo = veiculoRepository.save(veiculo);

        log.info(
                "Veículo {} cadastrado para o cliente {}.",
                salvo.getPlaca(),
                cliente.getNome());

        return veiculoMapper.toResponseDTO(salvo);

    }

        // ==========================================================
    // LISTAR
    // ==========================================================

    @Override
    public Page<VeiculoResponseDTO> listar(
            String placa,
            String modelo,
            Long clienteId,
            Boolean ativo,
            Pageable pageable) {

        return veiculoRepository
                .buscarComFiltros(
                        placa,
                        modelo,
                        clienteId,
                        ativo,
                        pageable)
                .map(veiculoMapper::toResponseDTO);

    }

    // ==========================================================
    // BUSCAR POR ID
    // ==========================================================

    @Override
    public VeiculoResponseDTO buscarPorId(Long id) {

        return veiculoMapper.toResponseDTO(
                buscarEntidade(id));

    }

    // ==========================================================
    // ATUALIZAR
    // ==========================================================

    @Override
    @Transactional
    public VeiculoResponseDTO atualizar(
            Long id,
            AtualizarVeiculoRequestDTO dto) {

        Veiculo veiculo = buscarEntidade(id);

        // ===========================================
        // Cliente
        // ===========================================

        if (dto.getClienteId() != null &&
                !dto.getClienteId().equals(
                        veiculo.getCliente().getId())) {

            Cliente cliente = buscarCliente(dto.getClienteId());

            if (!cliente.getAtivo()) {
                throw new BusinessException(
                        "Não é permitido vincular veículo a cliente inativo.");
            }

            veiculo.setCliente(cliente);
        }

        // ===========================================
        // Placa
        // ===========================================

        if (dto.getPlaca() != null &&
                !dto.getPlaca().equalsIgnoreCase(veiculo.getPlaca())) {

            validarPlaca(dto.getPlaca());

            veiculo.setPlaca(dto.getPlaca());
        }

        // ===========================================
        // Chassi
        // ===========================================

        if (dto.getChassi() != null &&
                !dto.getChassi().equals(veiculo.getChassi())) {

            if (veiculoRepository.existsByChassi(dto.getChassi())) {

                throw new BusinessException(
                        "Já existe um veículo com este chassi.");
            }

            veiculo.setChassi(dto.getChassi());

        }

        // ===========================================
        // Renavam
        // ===========================================

        if (dto.getRenavam() != null &&
                !dto.getRenavam().equals(veiculo.getRenavam())) {

            if (veiculoRepository.existsByRenavam(dto.getRenavam())) {

                throw new BusinessException(
                        "Já existe um veículo com este RENAVAM.");
            }

            veiculo.setRenavam(dto.getRenavam());

        }

        // ===========================================
        // Quilometragem
        // ===========================================

        if (dto.getQuilometragem() != null) {

            validarQuilometragem(
                    veiculo.getQuilometragem(),
                    dto.getQuilometragem());

            veiculo.setQuilometragem(dto.getQuilometragem());

        }

        // ===========================================
        // Demais campos
        // ===========================================

        if (dto.getMarca() != null)
            veiculo.setMarca(dto.getMarca());

        if (dto.getModelo() != null)
            veiculo.setModelo(dto.getModelo());

        if (dto.getVersao() != null)
            veiculo.setVersao(dto.getVersao());

        if (dto.getAnoFabricacao() != null)
            veiculo.setAnoFabricacao(dto.getAnoFabricacao());

        if (dto.getAnoModelo() != null)
            veiculo.setAnoModelo(dto.getAnoModelo());

        if (dto.getCor() != null)
            veiculo.setCor(dto.getCor());

        if (dto.getCombustivel() != null)
            veiculo.setCombustivel(dto.getCombustivel());

        if (dto.getObservacoes() != null)
            veiculo.setObservacoes(dto.getObservacoes());

        Veiculo atualizado = veiculoRepository.save(veiculo);

        log.info(
                "Veículo {} atualizado com sucesso.",
                atualizado.getPlaca());

        return veiculoMapper.toResponseDTO(atualizado);

    }

        // ==========================================================
    // DESATIVAR
    // ==========================================================

    @Override
    @Transactional
    public void desativar(Long id) {

        Veiculo veiculo = buscarEntidade(id);

        if (!veiculo.getAtivo()) {
            throw new BusinessException(
                    "O veículo já está inativo.");
        }

        veiculo.setAtivo(false);

        veiculoRepository.save(veiculo);

        log.info(
                "Veículo {} desativado.",
                veiculo.getPlaca());

    }

    // ==========================================================
    // REATIVAR
    // ==========================================================

    @Override
    @Transactional
    public VeiculoResponseDTO reativar(Long id) {

        Veiculo veiculo = buscarEntidade(id);

        if (veiculo.getAtivo()) {
            throw new BusinessException(
                    "O veículo já está ativo.");
        }

        if (!veiculo.getCliente().getAtivo()) {
            throw new BusinessException(
                    "Não é permitido reativar um veículo de cliente inativo.");
        }

        veiculo.setAtivo(true);

        Veiculo atualizado = veiculoRepository.save(veiculo);

        log.info(
                "Veículo {} reativado.",
                atualizado.getPlaca());

        return veiculoMapper.toResponseDTO(atualizado);

    }

    // ==========================================================
    // MÉTODOS AUXILIARES
    // ==========================================================

    /**
     * Busca um veículo pelo ID.
     */
    private Veiculo buscarEntidade(Long id) {

        return veiculoRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Veículo", id));

    }

    /**
     * Busca um cliente pelo ID.
     */
    private Cliente buscarCliente(Long id) {

        return clienteRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cliente", id));

    }

    /**
     * Valida placa duplicada.
     */
    private void validarPlaca(String placa) {

        if (veiculoRepository.existsByPlaca(placa)) {

            throw new BusinessException(
                    "Já existe um veículo cadastrado com esta placa.");

        }

    }

    /**
     * Valida regra de quilometragem.
     */
    private void validarQuilometragem(
            Integer atual,
            Integer nova) {

        if (atual != null &&
                nova < atual) {

            throw new BusinessException(
                    "A quilometragem não pode ser inferior à atual.");

        }

    }

}
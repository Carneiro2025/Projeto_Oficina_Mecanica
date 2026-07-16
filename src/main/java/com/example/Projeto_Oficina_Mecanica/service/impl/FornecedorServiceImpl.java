package com.example.Projeto_Oficina_Mecanica.service.impl;

import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarFornecedorRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarFornecedorRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.FornecedorResponseDTO;
import com.example.Projeto_Oficina_Mecanica.entity.Fornecedor;
import com.example.Projeto_Oficina_Mecanica.exception.BusinessException;
import com.example.Projeto_Oficina_Mecanica.exception.ResourceNotFoundException;
import com.example.Projeto_Oficina_Mecanica.mapper.FornecedorMapper;
import com.example.Projeto_Oficina_Mecanica.repository.FornecedorRepository;
import com.example.Projeto_Oficina_Mecanica.service.FornecedorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ============================================================
 * SERVICE - FORNECEDOR
 * ============================================================
 *
 * Regras de Negócio
 *
 * RN01 - CNPJ obrigatório e único.
 *
 * RN02 - Inscrição Estadual única quando informada.
 *
 * RN03 - E-mail único quando informado.
 *
 * RN04 - Exclusão lógica utilizando o campo ativo.
 *
 * RN05 - Fornecedor inativo não pode emitir Nota Fiscal de Entrada.
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FornecedorServiceImpl implements FornecedorService {

    private final FornecedorRepository fornecedorRepository;

    private final FornecedorMapper fornecedorMapper;

    // ==========================================================
    // CRIAR
    // ==========================================================

    @Override
    @Transactional
    public FornecedorResponseDTO criar(
            CriarFornecedorRequestDTO dto) {

        validarCnpj(dto.getCnpj());

        validarEmail(dto.getEmail());

        validarInscricaoEstadual(dto.getInscricaoEstadual());

        Fornecedor fornecedor =
                fornecedorMapper.toEntity(dto);

        fornecedor.setAtivo(true);

        Fornecedor salvo =
                fornecedorRepository.save(fornecedor);

        log.info(
                "Fornecedor {} cadastrado com sucesso.",
                salvo.getRazaoSocial());

        return fornecedorMapper.toResponseDTO(salvo);

    }

       // ==========================================================
    // LISTAR
    // ==========================================================

    @Override
    public Page<FornecedorResponseDTO> listar(
            String razaoSocial,
            String cnpj,
            String cidade,
            Pageable pageable) {

        return fornecedorRepository
                .buscarComFiltros(
                        razaoSocial,
                        cnpj,
                        cidade,
                        pageable)
                .map(fornecedorMapper::toResponseDTO);

    }

    // ==========================================================
    // BUSCAR POR ID
    // ==========================================================

    @Override
    public FornecedorResponseDTO buscarPorId(Long id) {

        return fornecedorMapper.toResponseDTO(
                buscarEntidade(id));

    }

    // ==========================================================
    // ATUALIZAR
    // ==========================================================

    @Override
    @Transactional
    public FornecedorResponseDTO atualizar(
            Long id,
            AtualizarFornecedorRequestDTO dto) {

        Fornecedor fornecedor = buscarEntidade(id);

        // ===========================================
        // Razão Social
        // ===========================================

        if (dto.getRazaoSocial() != null) {
            fornecedor.setRazaoSocial(dto.getRazaoSocial());
        }

        // ===========================================
        // Nome Fantasia
        // ===========================================

        if (dto.getNomeFantasia() != null) {
            fornecedor.setNomeFantasia(dto.getNomeFantasia());
        }

        // ===========================================
        // CNPJ
        // ===========================================

        if (dto.getCnpj() != null &&
                !dto.getCnpj().equals(fornecedor.getCnpj())) {

            if (fornecedorRepository.existsByCnpj(dto.getCnpj())) {

                throw new BusinessException(
                        "Já existe um fornecedor cadastrado com este CNPJ.");

            }

            fornecedor.setCnpj(dto.getCnpj());

        }

        // ===========================================
        // Inscrição Estadual
        // ===========================================

        if (dto.getInscricaoEstadual() != null &&
                !dto.getInscricaoEstadual().equals(
                        fornecedor.getInscricaoEstadual())) {

            if (fornecedorRepository.existsByInscricaoEstadual(
                    dto.getInscricaoEstadual())) {

                throw new BusinessException(
                        "Já existe um fornecedor com esta Inscrição Estadual.");

            }

            fornecedor.setInscricaoEstadual(
                    dto.getInscricaoEstadual());

        }

        // ===========================================
        // Email
        // ===========================================

        if (dto.getEmail() != null &&
                !dto.getEmail().equalsIgnoreCase(
                        fornecedor.getEmail())) {

            if (fornecedorRepository.existsByEmail(dto.getEmail())) {

                throw new BusinessException(
                        "Já existe um fornecedor com este e-mail.");

            }

            fornecedor.setEmail(dto.getEmail());

        }

        // ===========================================
        // Demais campos
        // ===========================================

        if (dto.getTelefone() != null)
            fornecedor.setTelefone(dto.getTelefone());

        if (dto.getCelular() != null)
            fornecedor.setCelular(dto.getCelular());

        if (dto.getSite() != null)
            fornecedor.setSite(dto.getSite());

        if (dto.getNomeContato() != null)
            fornecedor.setNomeContato(dto.getNomeContato());

        if (dto.getObservacoes() != null)
            fornecedor.setObservacoes(dto.getObservacoes());

        if (dto.getEndereco() != null)
            fornecedor.setEndereco(
                    fornecedorMapper.toEndereco(dto.getEndereco()));

        Fornecedor atualizado =
                fornecedorRepository.save(fornecedor);

        log.info(
                "Fornecedor {} atualizado com sucesso.",
                atualizado.getRazaoSocial());

        return fornecedorMapper.toResponseDTO(atualizado);

    }
    
        // ==========================================================
    // DESATIVAR
    // ==========================================================

    @Override
    @Transactional
    public void desativar(Long id) {

        Fornecedor fornecedor = buscarEntidade(id);

        if (!fornecedor.getAtivo()) {
            throw new BusinessException(
                    "O fornecedor já está inativo.");
        }

        fornecedor.setAtivo(false);

        fornecedorRepository.save(fornecedor);

        log.info(
                "Fornecedor {} desativado.",
                fornecedor.getRazaoSocial());

    }

    // ==========================================================
    // REATIVAR
    // ==========================================================

    @Override
    @Transactional
    public FornecedorResponseDTO reativar(Long id) {

        Fornecedor fornecedor = buscarEntidade(id);

        if (fornecedor.getAtivo()) {
            throw new BusinessException(
                    "O fornecedor já está ativo.");
        }

        fornecedor.setAtivo(true);

        Fornecedor atualizado =
                fornecedorRepository.save(fornecedor);

        log.info(
                "Fornecedor {} reativado.",
                atualizado.getRazaoSocial());

        return fornecedorMapper.toResponseDTO(atualizado);

    }

    // ==========================================================
    // MÉTODOS AUXILIARES
    // ==========================================================

    /**
     * Busca fornecedor por ID.
     */
    private Fornecedor buscarEntidade(Long id) {

        return fornecedorRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Fornecedor", id));

    }

    /**
     * Valida CNPJ.
     */
    private void validarCnpj(String cnpj) {

        if (cnpj != null &&
                fornecedorRepository.existsByCnpj(cnpj)) {

            throw new BusinessException(
                    "Já existe um fornecedor cadastrado com este CNPJ.");

        }

    }

    /**
     * Valida e-mail.
     */
    private void validarEmail(String email) {

        if (email != null &&
                !email.isBlank() &&
                fornecedorRepository.existsByEmail(email)) {

            throw new BusinessException(
                    "Já existe um fornecedor cadastrado com este e-mail.");

        }

    }

    /**
     * Valida inscrição estadual.
     */
    private void validarInscricaoEstadual(String ie) {

        if (ie != null &&
                !ie.isBlank() &&
                fornecedorRepository.existsByInscricaoEstadual(ie)) {

            throw new BusinessException(
                    "Já existe um fornecedor cadastrado com esta Inscrição Estadual.");

        }

    }

}

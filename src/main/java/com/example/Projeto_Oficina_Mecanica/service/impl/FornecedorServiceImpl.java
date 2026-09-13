package com.example.Projeto_Oficina_Mecanica.service.impl;

import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarFornecedorRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarFornecedorRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.FornecedorResponseDTO;
import com.example.Projeto_Oficina_Mecanica.entity.Fornecedor;
import com.example.Projeto_Oficina_Mecanica.entity.Usuario;
import com.example.Projeto_Oficina_Mecanica.exception.BusinessException;
import com.example.Projeto_Oficina_Mecanica.exception.ResourceNotFoundException;
import com.example.Projeto_Oficina_Mecanica.mapper.FornecedorMapper;
import com.example.Projeto_Oficina_Mecanica.repository.FornecedorRepository;
import com.example.Projeto_Oficina_Mecanica.service.AuditoriaService;
import com.example.Projeto_Oficina_Mecanica.service.FornecedorService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ATENÇÃO: arquivo RECONSTRUÍDO a partir do contrato de FornecedorServiceImplTest,
 * já que o FornecedorServiceImpl.java original não estava disponível nesta sessão.
 * A parte NOVA é a auditoria em criar().
 */
@Service
@RequiredArgsConstructor
public class FornecedorServiceImpl implements FornecedorService {

    private final FornecedorRepository fornecedorRepository;

    private final FornecedorMapper fornecedorMapper;

    private final AuditoriaService auditoriaService;

    @Override
    @Transactional
    public FornecedorResponseDTO criar(CriarFornecedorRequestDTO dto) {

        if (fornecedorRepository.existsByCnpj(dto.getCnpj())) {
            throw new BusinessException("CNPJ já cadastrado.");
        }

        if (fornecedorRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Já existe um fornecedor cadastrado com este e-mail.");
        }

        if (dto.getInscricaoEstadual() != null
                && fornecedorRepository.existsByInscricaoEstadual(dto.getInscricaoEstadual())) {
            throw new BusinessException("Inscrição Estadual já cadastrada.");
        }

        Fornecedor fornecedor = fornecedorMapper.toEntity(dto);
        fornecedor.setAtivo(true);

        Fornecedor salvo = fornecedorRepository.save(fornecedor);

        auditoriaService.registrar(
                usuarioLogado(),
                "CRIAR",
                "Fornecedor",
                salvo.getId(),
                "Fornecedor cadastrado: " + salvo.getRazaoSocial(),
                obterIp()
        );

        return fornecedorMapper.toResponseDTO(salvo);
    }

    @Override
    @Transactional(readOnly = true)
    public FornecedorResponseDTO buscarPorId(Long id) {
        return fornecedorMapper.toResponseDTO(buscarEntidadePorId(id));
    }

    @Override
    @Transactional
    public FornecedorResponseDTO atualizar(Long id, AtualizarFornecedorRequestDTO dto) {

        Fornecedor fornecedor = buscarEntidadePorId(id);

        if (dto.getCnpj() != null && !dto.getCnpj().equals(fornecedor.getCnpj())) {
            if (fornecedorRepository.existsByCnpj(dto.getCnpj())) {
                throw new BusinessException("CNPJ já cadastrado.");
            }
        }

        if (dto.getRazaoSocial() != null) {
            fornecedor.setRazaoSocial(dto.getRazaoSocial());
        }
        if (dto.getCnpj() != null) {
            fornecedor.setCnpj(dto.getCnpj());
        }
        if (dto.getEmail() != null) {
            fornecedor.setEmail(dto.getEmail());
        }

        Fornecedor salvo = fornecedorRepository.save(fornecedor);

        auditoriaService.registrar(
                usuarioLogado(),
                "ATUALIZAR",
                "Fornecedor",
                salvo.getId(),
                "Fornecedor atualizado: " + salvo.getRazaoSocial(),
                obterIp()
        );

        return fornecedorMapper.toResponseDTO(salvo);
    }

    @Override
    @Transactional
    public void desativar(Long id) {

        Fornecedor fornecedor = buscarEntidadePorId(id);

        if (!fornecedor.getAtivo()) {
            throw new BusinessException("Fornecedor já está inativo.");
        }

        fornecedor.setAtivo(false);
        fornecedorRepository.save(fornecedor);

        auditoriaService.registrar(
                usuarioLogado(),
                "EXCLUIR",
                "Fornecedor",
                fornecedor.getId(),
                "Fornecedor desativado: " + fornecedor.getRazaoSocial(),
                obterIp()
        );
    }

    @Override
    @Transactional
    public FornecedorResponseDTO reativar(Long id) {

        Fornecedor fornecedor = buscarEntidadePorId(id);

        if (fornecedor.getAtivo()) {
            throw new BusinessException("Fornecedor já está ativo.");
        }

        fornecedor.setAtivo(true);

        Fornecedor salvo = fornecedorRepository.save(fornecedor);

        return fornecedorMapper.toResponseDTO(salvo);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FornecedorResponseDTO> listar(String razaoSocial, String cnpj, String cidade, Pageable pageable) {
        return fornecedorRepository.buscarComFiltros(razaoSocial, cnpj, cidade, pageable)
                .map(fornecedorMapper::toResponseDTO);
    }

    private Fornecedor buscarEntidadePorId(Long id) {
        return fornecedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor", id));
    }

    private String usuarioLogado() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return (principal instanceof Usuario) ? ((Usuario) principal).getEmail() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private String obterIp() {
        try {
            var attrs = (org.springframework.web.context.request.ServletRequestAttributes)
                    org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
            return (attrs != null) ? attrs.getRequest().getRemoteAddr() : null;
        } catch (Exception e) {
            return null;
        }
    }
}

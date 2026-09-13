package com.example.Projeto_Oficina_Mecanica.service.impl;

import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarClienteRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarClienteRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.ClienteResponseDTO;
import com.example.Projeto_Oficina_Mecanica.entity.Cliente;
import com.example.Projeto_Oficina_Mecanica.entity.Usuario;
import com.example.Projeto_Oficina_Mecanica.service.ClienteService;
import com.example.Projeto_Oficina_Mecanica.exception.BusinessException;
import com.example.Projeto_Oficina_Mecanica.exception.ResourceNotFoundException;
import com.example.Projeto_Oficina_Mecanica.mapper.ClienteMapper;
import com.example.Projeto_Oficina_Mecanica.repository.ClienteRepository;
import com.example.Projeto_Oficina_Mecanica.service.AuditoriaService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ATENÇÃO: arquivo RECONSTRUÍDO a partir do contrato de ClienteServiceImplTest,
 * já que o ClienteServiceImpl.java original não estava disponível nesta sessão.
 * A parte NOVA é a auditoria em criar() — confira a assinatura de
 * AuditoriaService.registrar(...) contra a sua versão real.
 */
@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;

    private final ClienteMapper clienteMapper;

    private final AuditoriaService auditoriaService;

    @Override
    @Transactional
    public ClienteResponseDTO criar(CriarClienteRequestDTO dto) {

        if (clienteRepository.existsByCpfCnpj(dto.getCpfCnpj())) {
            throw new BusinessException("CPF/CNPJ já cadastrado.");
        }

        Cliente cliente = clienteMapper.toEntity(dto);
        cliente.setAtivo(true);

        Cliente salvo = clienteRepository.save(cliente);

        auditoriaService.registrar(
                usuarioLogado(),
                "CRIAR",
                "Cliente",
                salvo.getId(),
                "Cliente cadastrado: " + salvo.getNome(),
                obterIp()
        );

        return clienteMapper.toResponseDTO(salvo);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDTO buscarPorId(Long id) {
        Cliente cliente = buscarEntidadePorId(id);
        return clienteMapper.toResponseDTO(cliente);
    }

    @Override
    @Transactional
    public ClienteResponseDTO atualizar(Long id, AtualizarClienteRequestDTO dto) {

        Cliente cliente = buscarEntidadePorId(id);

        if (dto.getCpfCnpj() != null && !dto.getCpfCnpj().equals(cliente.getCpfCnpj())) {

            if (clienteRepository.existsByCpfCnpj(dto.getCpfCnpj())) {
                throw new BusinessException("CPF/CNPJ já cadastrado.");
            }
        }

        clienteMapper.updateEntity(dto, cliente);

        Cliente salvo = clienteRepository.save(cliente);

        auditoriaService.registrar(
                usuarioLogado(),
                "ATUALIZAR",
                "Cliente",
                salvo.getId(),
                "Cliente atualizado: " + salvo.getNome(),
                obterIp()
        );

        return clienteMapper.toResponseDTO(salvo);
    }

    @Override
    @Transactional
    public void desativar(Long id) {

        Cliente cliente = buscarEntidadePorId(id);

        if (!cliente.getAtivo()) {
            throw new BusinessException("Cliente já está inativo.");
        }

        cliente.setAtivo(false);
        clienteRepository.save(cliente);

        auditoriaService.registrar(
                usuarioLogado(),
                "EXCLUIR",
                "Cliente",
                cliente.getId(),
                "Cliente desativado: " + cliente.getNome(),
                obterIp()
        );
    }

    @Override
    @Transactional
    public ClienteResponseDTO reativar(Long id) {

        Cliente cliente = buscarEntidadePorId(id);

        if (cliente.getAtivo()) {
            throw new BusinessException("Cliente já está ativo.");
        }

        cliente.setAtivo(true);

        Cliente salvo = clienteRepository.save(cliente);

        return clienteMapper.toResponseDTO(salvo);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClienteResponseDTO> listar(String nome, String cpfCnpj, Boolean ativo, Pageable pageable) {
        return clienteRepository.buscarComFiltros(nome, cpfCnpj, ativo, pageable)
                .map(clienteMapper::toResponseDTO);
    }

    private Cliente buscarEntidadePorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", id));
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

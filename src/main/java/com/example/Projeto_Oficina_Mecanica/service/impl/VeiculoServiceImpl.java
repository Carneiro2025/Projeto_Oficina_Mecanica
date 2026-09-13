package com.example.Projeto_Oficina_Mecanica.service.impl;

import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarVeiculoRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarVeiculoRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.VeiculoResponseDTO;
import com.example.Projeto_Oficina_Mecanica.entity.Cliente;
import com.example.Projeto_Oficina_Mecanica.entity.Usuario;
import com.example.Projeto_Oficina_Mecanica.entity.Veiculo;
import com.example.Projeto_Oficina_Mecanica.exception.BusinessException;
import com.example.Projeto_Oficina_Mecanica.exception.ResourceNotFoundException;
import com.example.Projeto_Oficina_Mecanica.mapper.VeiculoMapper;
import com.example.Projeto_Oficina_Mecanica.repository.ClienteRepository;
import com.example.Projeto_Oficina_Mecanica.repository.VeiculoRepository;
import com.example.Projeto_Oficina_Mecanica.service.AuditoriaService;
import com.example.Projeto_Oficina_Mecanica.service.VeiculoService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ATENÇÃO: arquivo RECONSTRUÍDO a partir do contrato de VeiculoServiceImplTest,
 * já que o VeiculoServiceImpl.java original não estava disponível nesta sessão.
 * A parte NOVA é a auditoria em criar().
 */
@Service
@RequiredArgsConstructor
public class VeiculoServiceImpl implements VeiculoService {

    private final VeiculoRepository veiculoRepository;

    private final ClienteRepository clienteRepository;

    private final VeiculoMapper veiculoMapper;

    private final AuditoriaService auditoriaService;

    @Override
    @Transactional
    public VeiculoResponseDTO criar(CriarVeiculoRequestDTO dto) {

        if (veiculoRepository.existsByPlaca(dto.getPlaca())) {
            throw new BusinessException("Já existe um veículo cadastrado com esta placa.");
        }

        if (dto.getChassi() != null && veiculoRepository.existsByChassi(dto.getChassi())) {
            throw new BusinessException("Já existe um veículo cadastrado com este chassi.");
        }

        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", dto.getClienteId()));

        if (!cliente.getAtivo()) {
            throw new BusinessException("Não é possível cadastrar veículo para um cliente inativo.");
        }

        Veiculo veiculo = veiculoMapper.toEntity(dto);
        veiculo.setCliente(cliente);
        veiculo.setAtivo(true);

        Veiculo salvo = veiculoRepository.save(veiculo);

        auditoriaService.registrar(
                usuarioLogado(),
                "CRIAR",
                "Veiculo",
                salvo.getId(),
                "Veículo cadastrado: " + salvo.getPlaca(),
                obterIp()
        );

        return veiculoMapper.toResponseDTO(salvo);
    }

    @Override
    @Transactional(readOnly = true)
    public VeiculoResponseDTO buscarPorId(Long id) {
        return veiculoMapper.toResponseDTO(buscarEntidadePorId(id));
    }

    @Override
    @Transactional
    public VeiculoResponseDTO atualizar(Long id, AtualizarVeiculoRequestDTO dto) {

        Veiculo veiculo = buscarEntidadePorId(id);

        if (dto.getQuilometragem() != null) {
            if (dto.getQuilometragem() < veiculo.getQuilometragem()) {
                throw new BusinessException("A nova quilometragem não pode ser menor que a atual.");
            }
            veiculo.setQuilometragem(dto.getQuilometragem());
        }

        if (dto.getClienteId() != null) {
            Cliente novoCliente = clienteRepository.findById(dto.getClienteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente", dto.getClienteId()));

            if (!novoCliente.getAtivo()) {
                throw new BusinessException("Não é possível vincular o veículo a um cliente inativo.");
            }

            veiculo.setCliente(novoCliente);
        }

        Veiculo salvo = veiculoRepository.save(veiculo);

        auditoriaService.registrar(
                usuarioLogado(),
                "ATUALIZAR",
                "Veiculo",
                salvo.getId(),
                "Veículo atualizado: " + salvo.getPlaca(),
                obterIp()
        );

        return veiculoMapper.toResponseDTO(salvo);
    }

    @Override
    @Transactional
    public void desativar(Long id) {

        Veiculo veiculo = buscarEntidadePorId(id);

        if (!veiculo.getAtivo()) {
            throw new BusinessException("Veículo já está inativo.");
        }

        veiculo.setAtivo(false);
        veiculoRepository.save(veiculo);

        auditoriaService.registrar(
                usuarioLogado(),
                "EXCLUIR",
                "Veiculo",
                veiculo.getId(),
                "Veículo desativado: " + veiculo.getPlaca(),
                obterIp()
        );
    }

    @Override
    @Transactional
    public VeiculoResponseDTO reativar(Long id) {

        Veiculo veiculo = buscarEntidadePorId(id);

        if (veiculo.getAtivo()) {
            throw new BusinessException("Veículo já está ativo.");
        }

        if (!veiculo.getCliente().getAtivo()) {
            throw new BusinessException("Não é possível reativar veículo de cliente inativo.");
        }

        veiculo.setAtivo(true);

        Veiculo salvo = veiculoRepository.save(veiculo);

        return veiculoMapper.toResponseDTO(salvo);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VeiculoResponseDTO> listar(String placa, String modelo, Long clienteId, Boolean ativo, Pageable pageable) {
        return veiculoRepository.buscarComFiltros(placa, modelo, clienteId, ativo, pageable)
                .map(veiculoMapper::toResponseDTO);
    }

    private Veiculo buscarEntidadePorId(Long id) {
        return veiculoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Veículo", id));
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

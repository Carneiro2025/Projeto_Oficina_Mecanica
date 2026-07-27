package com.example.Projeto_Oficina_Mecanica.service.impl;

import com.example.Projeto_Oficina_Mecanica.dto.request.AtualizarClienteRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.request.CriarClienteRequestDTO;
import com.example.Projeto_Oficina_Mecanica.dto.response.ClienteResponseDTO;
import com.example.Projeto_Oficina_Mecanica.entity.Cliente;
import com.example.Projeto_Oficina_Mecanica.exception.BusinessException;
import com.example.Projeto_Oficina_Mecanica.exception.ResourceNotFoundException;
import com.example.Projeto_Oficina_Mecanica.mapper.ClienteMapper;
import com.example.Projeto_Oficina_Mecanica.repository.ClienteRepository;
import com.example.Projeto_Oficina_Mecanica.service.ClienteService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    @Override
    @Transactional
    public ClienteResponseDTO criar(CriarClienteRequestDTO dto) {

        if (clienteRepository.existsByCpfCnpj(dto.getCpfCnpj())) {
            throw new BusinessException(
                    "CPF/CNPJ já cadastrado."
            );
        }

        Cliente cliente = clienteMapper.toEntity(dto);

        cliente.setAtivo(true);

        Cliente salvo = clienteRepository.save(cliente);

        log.info("Cliente cadastrado: {}", salvo.getNome());

        return clienteMapper.toResponseDTO(salvo);
    }

    @Override
public Page<ClienteResponseDTO> listar(
        String nome,
        String cpfCnpj,
        Boolean ativo,
        Pageable pageable) {

    return clienteRepository
            .buscarComFiltros(nome, cpfCnpj, ativo, pageable)
            .map(clienteMapper::toResponseDTO);
}

@Override
public ClienteResponseDTO buscarPorId(Long id) {

    return clienteMapper.toResponseDTO(
            buscarEntidade(id)
    );

}

private Cliente buscarEntidade(Long id) {

    return clienteRepository.findById(id)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Cliente",
                            id
                    ));

}

@Override
@Transactional
public ClienteResponseDTO atualizar(
        Long id,
        AtualizarClienteRequestDTO dto) {

    Cliente cliente = buscarEntidade(id);

    // Valida CPF/CNPJ caso tenha sido alterado
    if (dto.getCpfCnpj() != null &&
            !dto.getCpfCnpj().equals(cliente.getCpfCnpj())) {

        if (clienteRepository.existsByCpfCnpj(dto.getCpfCnpj())) {
            throw new BusinessException(
                    "CPF/CNPJ já cadastrado."
            );
        }
    }

    // Atualiza somente os campos informados
    clienteMapper.updateEntity(dto, cliente);

    Cliente atualizado = clienteRepository.save(cliente);

    log.info("Cliente atualizado. ID: {}", atualizado.getId());

    return clienteMapper.toResponseDTO(atualizado);
}

@Override
@Transactional
public void desativar(Long id) {

    Cliente cliente = buscarEntidade(id);

    if (!cliente.getAtivo()) {
        throw new BusinessException(
                "Cliente já está inativo."
        );
    }

    cliente.setAtivo(false);

    clienteRepository.save(cliente);

    log.info("Cliente desativado. ID: {}", id);
}

@Override
@Transactional
public ClienteResponseDTO reativar(Long id) {

    Cliente cliente = buscarEntidade(id);

    if (cliente.getAtivo()) {
        throw new BusinessException(
                "Cliente já está ativo."
        );
    }

    cliente.setAtivo(true);

    Cliente atualizado = clienteRepository.save(cliente);

    log.info("Cliente reativado. ID: {}", id);

    return clienteMapper.toResponseDTO(atualizado);
}

}
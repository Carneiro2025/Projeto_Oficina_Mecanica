package com.example.Projeto_Oficina_Mecanica.service;

import com.example.Projeto_Oficina_Mecanica.entity.ServicoRealizado;
import com.example.Projeto_Oficina_Mecanica.repository.ServicoRealizadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicoRealizadoService {

    private final ServicoRealizadoRepository repository;

    public List<ServicoRealizado> listarTodos() {
        return repository.findAll();
    }

    public ServicoRealizado buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Serviço não encontrado"));
    }

    public ServicoRealizado salvar(ServicoRealizado servico) {
        return repository.save(servico);
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }
}

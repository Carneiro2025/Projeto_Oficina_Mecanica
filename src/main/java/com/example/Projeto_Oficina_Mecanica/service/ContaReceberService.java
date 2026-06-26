package com.example.Projeto_Oficina_Mecanica.service;

import com.example.Projeto_Oficina_Mecanica.entity.ContaReceber;
import com.example.Projeto_Oficina_Mecanica.enums.StatusContaReceber;
import com.example.Projeto_Oficina_Mecanica.repository.ContaReceberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContaReceberService {

    private final ContaReceberRepository repository;

    public ContaReceber salvar(ContaReceber conta) {
        return repository.save(conta);
    }

    public List<ContaReceber> listarTodas() {
        return repository.findAll();
    }

    public List<ContaReceber> listarPendentes() {
        return repository.findByStatus(
                StatusContaReceber.PENDENTE
        );
    }

    public ContaReceber registrarPagamento(
            Long id,
            LocalDate dataPagamento) {

        ContaReceber conta = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Conta não encontrada"));

        conta.setStatus(StatusContaReceber.PAGO);
        conta.setDataPagamento(dataPagamento);

        return repository.save(conta);
    }
}


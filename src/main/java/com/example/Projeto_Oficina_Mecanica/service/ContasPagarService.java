package com.example.Projeto_Oficina_Mecanica.service;

import com.example.Projeto_Oficina_Mecanica.entity.ContasPagar;
import com.example.Projeto_Oficina_Mecanica.enums.StatusContaPagar;
import com.example.Projeto_Oficina_Mecanica.repository.ContasPagarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContasPagarService {

    private final ContasPagarRepository repository;

    public ContasPagar salvar(ContasPagar conta) {
        return repository.save(conta);
    }

    public List<ContasPagar> listarTodas() {
        return repository.findAll();
    }

    public List<ContasPagar> listarPendentes() {
        return repository.findByStatus(
                StatusContaPagar.PENDENTE
        );
    }

    public ContasPagar registrarPagamento(
            Long id,
            LocalDate dataPagamento) {

        ContasPagar conta = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Conta não encontrada"));

        conta.setStatus(StatusContaPagar.PAGO);
        conta.setDataPagamento(dataPagamento);

        return repository.save(conta);
    }
}
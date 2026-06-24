package com.example.Projeto_Oficina_Mecanica.service;

import com.example.Projeto_Oficina_Mecanica.entity.*;
import com.example.Projeto_Oficina_Mecanica.repository.MovimentacaoEstoqueRepository;
import com.example.Projeto_Oficina_Mecanica.repository.NotaFiscalEntradaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotaFiscalEntradaService {

    private final NotaFiscalEntradaRepository repository;
    private final MovimentacaoEstoqueRepository movimentacaoRepository;

    @Transactional
    public NotaFiscalEntrada salvar(NotaFiscalEntrada nota) {

        nota.calcularTotal();

        NotaFiscalEntrada salva = repository.save(nota);

        for (ItemNotaFiscalEntrada item : salva.getItens()) {

            Produto produto = item.getProduto();

            produto.setEstoqueAtual(
                    produto.getEstoqueAtual() + item.getQuantidade()
            );

            MovimentacaoEstoque movimentacao =
                    MovimentacaoEstoque.builder()
                            .produto(produto)
                            .tipo(TipoMovimentacaoEstoque.ENTRADA)
                            .quantidade(item.getQuantidade())
                            .observacao(
                                    "Entrada NF " + salva.getNumero()
                            )
                            .build();

            movimentacaoRepository.save(movimentacao);
        }

        return salva;
    }
}

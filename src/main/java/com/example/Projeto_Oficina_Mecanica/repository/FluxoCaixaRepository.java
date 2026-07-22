package com.example.Projeto_Oficina_Mecanica.repository;

import com.example.Projeto_Oficina_Mecanica.entity.FluxoCaixa;
import com.example.Projeto_Oficina_Mecanica.enums.TipoMovimentacaoCaixa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FluxoCaixaRepository
        extends JpaRepository<FluxoCaixa, Long> {

    Page<FluxoCaixa> findAll(Pageable pageable);

    List<FluxoCaixa> findByTipoMovimentacao(
            TipoMovimentacaoCaixa tipo
    );

    List<FluxoCaixa> findByDataMovimentacaoBetween(
            LocalDate inicio,
            LocalDate fim
    );

    List<FluxoCaixa> findByClienteId(
            Long clienteId
    );

    List<FluxoCaixa> findByFornecedorId(
            Long fornecedorId
    );

    /**
     * Última movimentação do caixa.
     */
    Optional<FluxoCaixa> findTopByOrderByDataMovimentacaoDescIdDesc();

}
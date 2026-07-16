package com.example.Projeto_Oficina_Mecanica.repository;

import com.example.Projeto_Oficina_Mecanica.entity.MovimentacaoEstoque;
import com.example.Projeto_Oficina_Mecanica.enums.TipoMovimentacaoEstoque;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface MovimentacaoEstoqueRepository 
        extends JpaRepository<MovimentacaoEstoque, Long> {


    Page<MovimentacaoEstoque> findByProdutoId(
            Long produtoId,
            Pageable pageable
    );


    List<MovimentacaoEstoque> findByProdutoIdOrderByDataMovimentacaoDesc(
            Long produtoId
    );


    List<MovimentacaoEstoque> findByTipo(
            TipoMovimentacaoEstoque tipo
    );

}

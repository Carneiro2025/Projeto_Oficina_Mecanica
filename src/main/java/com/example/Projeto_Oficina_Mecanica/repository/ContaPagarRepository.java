package com.example.Projeto_Oficina_Mecanica.repository;

import com.example.Projeto_Oficina_Mecanica.entity.ContaPagar;
import com.example.Projeto_Oficina_Mecanica.enums.StatusContaPagar;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContaPagarRepository
        extends JpaRepository<ContaPagar, Long> {

    Page<ContaPagar> findAll(Pageable pageable);

    List<ContaPagar> findByStatus(
            StatusContaPagar status
    );

    List<ContaPagar> findByFornecedorId(
            Long fornecedorId
    );

}
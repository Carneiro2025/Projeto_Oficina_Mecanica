package com.example.Projeto_Oficina_Mecanica.repository;

import com.example.Projeto_Oficina_Mecanica.entity.NotaFiscalEntrada;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotaFiscalEntradaRepository
        extends JpaRepository<NotaFiscalEntrada, Long> {

    boolean existsByNumero(String numero);

    Page<NotaFiscalEntrada> findAll(Pageable pageable);
}

package com.example.Projeto_Oficina_Mecanica.repository;

import com.example.Projeto_Oficina_Mecanica.entity.ContasPagar;
import com.example.Projeto_Oficina_Mecanica.enums.StatusContaPagar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContasPagarRepository
        extends JpaRepository<ContasPagar, Long> {

    List<ContasPagar> findByStatus(StatusContaPagar status);

    List<ContasPagar> findByFornecedorId(Long fornecedorId);
}

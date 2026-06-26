package com.example.Projeto_Oficina_Mecanica.repository;

import com.example.Projeto_Oficina_Mecanica.entity.ContaReceber;
import com.example.Projeto_Oficina_Mecanica.enums.StatusContaReceber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContaReceberRepository
        extends JpaRepository<ContaReceber, Long> {

    List<ContaReceber> findByStatus(StatusContaReceber status);

    List<ContaReceber> findByClienteId(Long clienteId);
}

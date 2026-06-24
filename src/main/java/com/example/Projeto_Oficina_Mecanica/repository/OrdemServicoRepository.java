package com.example.Projeto_Oficina_Mecanica.repository;

import com.example.Projeto_Oficina_Mecanica.entity.OrdemServico;
import com.example.Projeto_Oficina_Mecanica.enums.StatusOrdemServico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdemServicoRepository
        extends JpaRepository<OrdemServico, Long> {

    boolean existsByNumero(Long numero);

    Page<OrdemServico> findByStatus(
            StatusOrdemServico status,
            Pageable pageable
    );
}


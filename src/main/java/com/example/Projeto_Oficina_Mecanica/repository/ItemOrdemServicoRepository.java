package com.example.Projeto_Oficina_Mecanica.repository;

import com.example.Projeto_Oficina_Mecanica.entity.ItemOrdemServico;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemOrdemServicoRepository
        extends JpaRepository<ItemOrdemServico, Long> {
}


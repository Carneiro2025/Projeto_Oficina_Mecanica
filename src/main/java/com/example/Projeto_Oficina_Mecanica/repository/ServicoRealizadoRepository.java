package com.example.Projeto_Oficina_Mecanica.repository;

import com.example.Projeto_Oficina_Mecanica.entity.ServicoRealizado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicoRealizadoRepository
        extends JpaRepository<ServicoRealizado, Long> {
}

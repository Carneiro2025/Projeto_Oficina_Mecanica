package com.example.Projeto_Oficina_Mecanica.repository;

import com.example.Projeto_Oficina_Mecanica.entity.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

    List<Auditoria> findByUsuario(String usuario);

    List<Auditoria> findByAcao(String acao);

    List<Auditoria> findByEntidade(String entidade);

    List<Auditoria> findByRegistroId(Long registroId);

    List<Auditoria> findByDataHoraBetween(
            LocalDateTime inicio,
            LocalDateTime fim
    );

    List<Auditoria> findByUsuarioAndDataHoraBetween(
            String usuario,
            LocalDateTime inicio,
            LocalDateTime fim
    );

}

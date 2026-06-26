package com.example.Projeto_Oficina_Mecanica.repository;

import com.example.Projeto_Oficina_Mecanica.enums.EspecialidadeMecanico;
import com.example.Projeto_Oficina_Mecanica.entity.Mecanico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MecanicoRepository extends JpaRepository<Mecanico, Long> {

    boolean existsByCpf(String cpf);

    Optional<Mecanico> findByCpf(String cpf);

    // Listar mecânicos ativos por especialidade
    @Query("""
        SELECT m FROM Mecanico m
        WHERE m.ativo = true
          AND :especialidade MEMBER OF m.especialidades
        """)
    List<Mecanico> findByEspecialidadeAndAtivoTrue(
            @Param("especialidade") EspecialidadeMecanico especialidade);

    // Busca paginada com filtros
    @Query("""
        SELECT DISTINCT m FROM Mecanico m
        WHERE m.ativo = true
          AND (:nome          IS NULL OR LOWER(m.nome) LIKE LOWER(CONCAT('%', :nome, '%')))
          AND (:especialidade IS NULL OR :especialidade MEMBER OF m.especialidades)
        """)
    Page<Mecanico> buscarComFiltros(
            @Param("nome")          String nome,
            @Param("especialidade") EspecialidadeMecanico especialidade,
            Pageable pageable
    );

    // Listar apenas ativos (para selects de OS)
    List<Mecanico> findByAtivoTrueOrderByNome();
}
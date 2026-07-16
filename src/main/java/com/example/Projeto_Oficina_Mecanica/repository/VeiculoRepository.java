package com.example.Projeto_Oficina_Mecanica.repository;

import com.example.Projeto_Oficina_Mecanica.entity.Veiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository responsável pelo acesso aos dados dos veículos.
 *
 * Funcionalidades:
 *
 * - CRUD
 * - Busca por placa
 * - Busca por chassi
 * - Busca por renavam
 * - Busca por cliente
 * - Paginação
 * - Dashboard
 * - Filtros
 */
@Repository
public interface VeiculoRepository extends JpaRepository<Veiculo, Long>,
        JpaSpecificationExecutor<Veiculo> {

    /**
     * Busca pela placa.
     */
    Optional<Veiculo> findByPlaca(String placa);

    /**
     * Verifica existência da placa.
     */
    boolean existsByPlaca(String placa);

    /**
     * Busca pelo chassi.
     */
    Optional<Veiculo> findByChassi(String chassi);

    /**
     * Verifica existência do chassi.
     */
    boolean existsByChassi(String chassi);

    /**
     * Busca pelo Renavam.
     */
    Optional<Veiculo> findByRenavam(String renavam);

    /**
     * Verifica existência do Renavam.
     */
    boolean existsByRenavam(String renavam);

    /**
     * Lista veículos ativos.
     */
    Page<Veiculo> findByAtivoTrue(Pageable pageable);

    /**
     * Lista veículos inativos.
     */
    Page<Veiculo> findByAtivoFalse(Pageable pageable);

    /**
     * Lista veículos de um cliente.
     */
    List<Veiculo> findByClienteId(Long clienteId);

    /**
     * Conta veículos ativos.
     */
    long countByAtivoTrue();

    /**
     * Conta veículos inativos.
     */
    long countByAtivoFalse();

    /**
     * Pesquisa utilizando filtros.
     */
    @Query("""
        SELECT v
        FROM Veiculo v
        WHERE
            (:placa IS NULL
                OR LOWER(v.placa)
                    LIKE LOWER(CONCAT('%',:placa,'%')))
        AND
            (:modelo IS NULL
                OR LOWER(v.modelo)
                    LIKE LOWER(CONCAT('%',:modelo,'%')))
        AND
            (:clienteId IS NULL
                OR v.cliente.id = :clienteId)
        AND
            (:ativo IS NULL
                OR v.ativo = :ativo)
        ORDER BY
            v.modelo,
            v.marca
        """)
    Page<Veiculo> buscarComFiltros(

            @Param("placa")
            String placa,

            @Param("modelo")
            String modelo,

            @Param("clienteId")
            Long clienteId,

            @Param("ativo")
            Boolean ativo,

            Pageable pageable);

}


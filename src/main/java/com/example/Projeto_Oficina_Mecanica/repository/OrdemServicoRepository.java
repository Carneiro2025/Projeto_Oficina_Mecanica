package com.example.Projeto_Oficina_Mecanica.repository;

import com.example.Projeto_Oficina_Mecanica.entity.OrdemServico;
import com.example.Projeto_Oficina_Mecanica.enums.StatusOrdemServico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {

    boolean existsByNumero(String numero);

    Optional<OrdemServico> findByNumero(String numero);

    Page<OrdemServico> findByStatus(StatusOrdemServico status, Pageable pageable);

    Page<OrdemServico> findByClienteId(Long clienteId, Pageable pageable);

    Page<OrdemServico> findByVeiculoId(Long veiculoId, Pageable pageable);

    @Query("""
            SELECT os
            FROM OrdemServico os
            WHERE os.ativo = true
              AND (:numero IS NULL OR LOWER(os.numero) LIKE LOWER(CONCAT('%', :numero, '%')))
              AND (:status IS NULL OR os.status = :status)
              AND (:clienteId IS NULL OR os.cliente.id = :clienteId)
              AND (:veiculoId IS NULL OR os.veiculo.id = :veiculoId)
              AND (:dataInicial IS NULL OR os.dataAbertura >= :dataInicial)
              AND (:dataFinal IS NULL OR os.dataAbertura <= :dataFinal)
            """)
    Page<OrdemServico> buscarComFiltros(

            @Param("numero") String numero,

            @Param("status") StatusOrdemServico status,

            @Param("clienteId") Long clienteId,

            @Param("veiculoId") Long veiculoId,

            @Param("dataInicial") LocalDate dataInicial,

            @Param("dataFinal") LocalDate dataFinal,

            Pageable pageable

    );

}
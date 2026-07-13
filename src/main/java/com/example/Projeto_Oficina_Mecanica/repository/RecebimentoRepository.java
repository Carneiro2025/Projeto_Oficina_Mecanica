package com.example.Projeto_Oficina_Mecanica.repository;

import com.example.Projeto_Oficina_Mecanica.entity.Recebimento;
import com.example.Projeto_Oficina_Mecanica.enums.StatusRecebimento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;


public interface RecebimentoRepository extends JpaRepository<Recebimento, Long> {

    /**
     * Lista recebimentos por status.
     */
    List<Recebimento> findByStatus(StatusRecebimento status);

    /**
     * Lista recebimentos de um cliente.
     */
    List<Recebimento> findByClienteId(Long clienteId);

    /**
     * Lista recebimentos de uma Ordem de Serviço.
     */

    Optional<Recebimento> findByOrdemServicoId(Long ordemServicoId);

    boolean existsByOrdemServicoId(Long ordemServicoId);


}


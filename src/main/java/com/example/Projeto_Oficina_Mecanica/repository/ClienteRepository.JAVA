package com.example.Projeto_Oficina_Mecanica.repository;

import com.oficina.model.Cliente;
import com.oficina.model.TipoPessoa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByCpfCnpj(String cpfCnpj);

    Optional<Cliente> findByCpfCnpj(String cpfCnpj);

    @Query("""
        SELECT c FROM Cliente c
        WHERE c.ativo = true
          AND (:nome    IS NULL OR LOWER(c.nome)    LIKE LOWER(CONCAT('%', :nome,    '%')))
          AND (:cpfCnpj IS NULL OR c.cpfCnpj        LIKE CONCAT('%', :cpfCnpj, '%'))
          AND (:tipo    IS NULL OR c.tipo            = :tipo)
          AND (:cidade  IS NULL OR LOWER(c.endereco.cidade) LIKE LOWER(CONCAT('%', :cidade, '%')))
        """)
    Page<Cliente> buscarComFiltros(
            @Param("nome")    String nome,
            @Param("cpfCnpj") String cpfCnpj,
            @Param("tipo")    TipoPessoa tipo,
            @Param("cidade")  String cidade,
            Pageable pageable
    );

    @Query("""
        SELECT c FROM Cliente c
        JOIN c.veiculos v
        WHERE c.ativo = true
          AND UPPER(v.placa) LIKE UPPER(CONCAT('%', :placa, '%'))
        """)
    Page<Cliente> buscarPorPlacaVeiculo(@Param("placa") String placa, Pageable pageable);
}
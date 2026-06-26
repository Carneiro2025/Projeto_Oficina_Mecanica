package com.example.Projeto_Oficina_Mecanica.repository;

import com.example.Projeto_Oficina_Mecanica.entity.Fornecedor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {

    boolean existsByCnpj(String cnpj);

    Optional<Fornecedor> findByCnpj(String cnpj);

    @Query("""
        SELECT f FROM Fornecedor f
        WHERE f.ativo = true
          AND (:razaoSocial IS NULL OR LOWER(f.razaoSocial)    LIKE LOWER(CONCAT('%', :razaoSocial, '%')))
          AND (:cnpj        IS NULL OR f.cnpj                  LIKE CONCAT('%', :cnpj, '%'))
          AND (:cidade      IS NULL OR LOWER(f.endereco.cidade) LIKE LOWER(CONCAT('%', :cidade, '%')))
        """)
    Page<Fornecedor> buscarComFiltros(
            @Param("razaoSocial") String razaoSocial,
            @Param("cnpj")        String cnpj,
            @Param("cidade")      String cidade,
            Pageable pageable
    );

    // Fornecedores ativos para selects
    List<Fornecedor> findByAtivoTrueOrderByRazaoSocial();
}
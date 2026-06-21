package com.example.Projeto_Oficina_Mecanica.repository;

import com.example.Projeto_Oficina_Mecanica.model.CategoriaProduto;
import com.example.Projeto_Oficina_Mecanica.model.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    boolean existsByCodigo(String codigo);

    Optional<Produto> findByCodigo(String codigo);

    // Produtos abaixo do estoque mínimo (alerta)
    @Query("SELECT p FROM Produto p WHERE p.ativo = true AND p.estoqueAtual <= p.estoqueMinimo")
    List<Produto> findProdutosAbaixoDoMinimo();

    // Produtos com estoque zerado
    @Query("SELECT p FROM Produto p WHERE p.ativo = true AND p.estoqueAtual = 0")
    List<Produto> findProdutosSemEstoque();

    // Busca paginada com filtros
    @Query("""
        SELECT p FROM Produto p
        LEFT JOIN FETCH p.fornecedor f
        WHERE p.ativo = true
          AND (:descricao   IS NULL OR LOWER(p.descricao) LIKE LOWER(CONCAT('%', :descricao, '%')))
          AND (:codigo      IS NULL OR LOWER(p.codigo)    LIKE LOWER(CONCAT('%', :codigo,    '%')))
          AND (:categoria   IS NULL OR p.categoria        = :categoria)
          AND (:fornecedorId IS NULL OR f.id              = :fornecedorId)
        """)
    Page<Produto> buscarComFiltros(
            @Param("descricao")    String descricao,
            @Param("codigo")       String codigo,
            @Param("categoria")    CategoriaProduto categoria,
            @Param("fornecedorId") Long fornecedorId,
            Pageable pageable
    );

    // Lista por fornecedor
    List<Produto> findByFornecedorIdAndAtivoTrue(Long fornecedorId);

    // Lista por categoria
    List<Produto> findByCategoriaAndAtivoTrue(CategoriaProduto categoria);
}
package com.example.Projeto_Oficina_Mecanica.repository;

import com.example.Projeto_Oficina_Mecanica.entity.Produto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes de integração de {@link ProdutoRepository} contra H2 em memória.
 *
 * ATENÇÃO: escrito sem acesso à entidade/repositório reais nesta sessão —
 * reconstrução a partir do que já foi confirmado em sessões anteriores
 * (existsByCodigo, findProdutosAbaixoDoMinimo()).
 */
@DataJpaTest
@DisplayName("ProdutoRepository")
class ProdutoRepositoryTest {

    @Autowired
    private ProdutoRepository produtoRepository;

    @BeforeEach
    void setUp() {
        produtoRepository.deleteAll();
    }

    @Nested
    @DisplayName("existsByCodigo()")
    class ExistsByCodigo {

        @Test
        @DisplayName("deve retornar true quando o código já está cadastrado")
        void deveRetornarTrue_quandoJaExiste() {
            produtoRepository.save(produtoBase("PRD001", 10, 5));

            assertThat(produtoRepository.existsByCodigo("PRD001")).isTrue();
        }

        @Test
        @DisplayName("deve retornar false quando o código não está cadastrado")
        void deveRetornarFalse_quandoNaoExiste() {
            assertThat(produtoRepository.existsByCodigo("INEXISTENTE")).isFalse();
        }
    }

    @Nested
    @DisplayName("findProdutosAbaixoDoMinimo()")
    class FindProdutosAbaixoDoMinimo {

        @Test
        @DisplayName("deve retornar apenas produtos com estoque atual <= estoque mínimo")
        void deveRetornarApenasAbaixoDoMinimo() {
            Produto abaixoDoMinimo = produtoRepository.save(produtoBase("PRD001", 2, 5));
            produtoRepository.save(produtoBase("PRD002", 20, 5));

            List<Produto> resultado = produtoRepository.findProdutosAbaixoDoMinimo();

            assertThat(resultado).extracting(Produto::getCodigo).containsExactly("PRD001");
            assertThat(resultado).containsExactly(abaixoDoMinimo);
        }

        @Test
        @DisplayName("deve incluir produtos com estoque exatamente igual ao mínimo")
        void deveIncluirEstoqueIgualAoMinimo() {
            produtoRepository.save(produtoBase("PRD003", 5, 5));

            List<Produto> resultado = produtoRepository.findProdutosAbaixoDoMinimo();

            assertThat(resultado).extracting(Produto::getCodigo).containsExactly("PRD003");
        }

        @Test
        @DisplayName("deve retornar vazio quando nenhum produto está abaixo do mínimo")
        void deveRetornarVazio_quandoNenhumAbaixoDoMinimo() {
            produtoRepository.save(produtoBase("PRD004", 50, 5));

            assertThat(produtoRepository.findProdutosAbaixoDoMinimo()).isEmpty();
        }
    }

    private Produto produtoBase(String codigo, int estoqueAtual, int estoqueMinimo) {
        return Produto.builder()
                .codigo(codigo)
                .descricao("Produto de teste " + codigo)
                .categoria(com.example.Projeto_Oficina_Mecanica.enums.CategoriaProduto.OUTROS)
                .precoCusto(new BigDecimal("10.00"))
                .precoVenda(new BigDecimal("20.00"))
                .estoqueAtual(estoqueAtual)
                .estoqueMinimo(estoqueMinimo)
                .ativo(true)
                .build();
    }
}

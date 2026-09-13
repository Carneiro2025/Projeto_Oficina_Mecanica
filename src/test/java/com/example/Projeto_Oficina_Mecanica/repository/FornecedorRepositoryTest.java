package com.example.Projeto_Oficina_Mecanica.repository;

import com.example.Projeto_Oficina_Mecanica.entity.Fornecedor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Pageable;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes de integração de {@link FornecedorRepository} contra H2 em memória.
 *
 * ATENÇÃO: escrito sem acesso à entidade/repositório reais nesta sessão —
 * reconstrução a partir do que já foi confirmado em sessões anteriores
 * (existsByCnpj, existsByEmail, existsByInscricaoEstadual,
 * buscarComFiltros(razaoSocial, cnpj, cidade, Pageable), onde "cidade"
 * é um campo aninhado em endereco.cidade). Se a entidade Fornecedor não
 * tiver um objeto Endereco embutido com esse formato exato, ajuste o
 * builder do @BeforeEach.
 */
@DataJpaTest
@DisplayName("FornecedorRepository")
class FornecedorRepositoryTest {

    @Autowired
    private FornecedorRepository fornecedorRepository;

    private Fornecedor fornecedor;

    @BeforeEach
    void setUp() {
        fornecedorRepository.deleteAll();

        fornecedor = fornecedorRepository.save(
                Fornecedor.builder()
                        .razaoSocial("Auto Peças LTDA")
                        .cnpj("12.345.678/0001-90")
                        .email("contato@autopecas.com")
                        .inscricaoEstadual("123456789")
                        .ativo(true)
                        .build()
        );
    }

    @Nested
    @DisplayName("existsBy...()")
    class Existencia {

        @Test
        @DisplayName("deve retornar true quando o CNPJ já está cadastrado")
        void deveCnpjExistir() {
            assertThat(fornecedorRepository.existsByCnpj("12.345.678/0001-90")).isTrue();
        }

        @Test
        @DisplayName("deve retornar false quando o CNPJ não está cadastrado")
        void deveCnpjNaoExistir() {
            assertThat(fornecedorRepository.existsByCnpj("99.999.999/0001-99")).isFalse();
        }

        @Test
        @DisplayName("deve retornar true quando o e-mail já está cadastrado")
        void deveEmailExistir() {
            assertThat(fornecedorRepository.existsByEmail("contato@autopecas.com")).isTrue();
        }

        @Test
        @DisplayName("deve retornar true quando a Inscrição Estadual já está cadastrada")
        void deveIeExistir() {
            assertThat(fornecedorRepository.existsByInscricaoEstadual("123456789")).isTrue();
        }
    }

    @Nested
    @DisplayName("findByAtivoTrueOrderByRazaoSocial()")
    class FindByAtivoTrue {

        @Test
        @DisplayName("deve retornar apenas fornecedores ativos")
        void deveRetornarApenasAtivos() {
            fornecedorRepository.save(
                    Fornecedor.builder()
                            .razaoSocial("Peças Inativa LTDA")
                            .cnpj("11.111.111/0001-11")
                            .ativo(false)
                            .build()
            );

            assertThat(fornecedorRepository.findByAtivoTrueOrderByRazaoSocial())
                    .containsExactly(fornecedor);
        }
    }

    @Nested
@DisplayName("buscarComFiltros()")
class BuscarComFiltros {

    @Test
    @DisplayName("deve filtrar por razão social")
    void deveFiltrarPorRazaoSocial() {
        Page<Fornecedor> resultado =
                fornecedorRepository.buscarComFiltros("auto", null, null, Pageable.unpaged());

        assertThat(resultado.getContent()).containsExactly(fornecedor);
    }

    @Test
    @DisplayName("deve filtrar por cidade (campo aninhado em endereco.cidade)")
    void deveFiltrarPorCidade() {
        fornecedor.setEndereco(
                com.example.Projeto_Oficina_Mecanica.entity.Endereco.builder()
                        .cidade("Recife")
                        .uf("PE")
                        .build()
        );
        fornecedorRepository.save(fornecedor);

        Page<Fornecedor> resultado =
                fornecedorRepository.buscarComFiltros(null, null, "recife", Pageable.unpaged());

        assertThat(resultado.getContent()).containsExactly(fornecedor);
    }

    @Test
    @DisplayName("deve ignorar fornecedores inativos (o @Query já filtra ativo = true)")
    void deveIgnorarInativos() {
        fornecedor.setAtivo(false);
        fornecedorRepository.save(fornecedor);

        Page<Fornecedor> resultado =
                fornecedorRepository.buscarComFiltros(null, null, null, Pageable.unpaged());

        assertThat(resultado.getContent()).isEmpty();
    }
}


}

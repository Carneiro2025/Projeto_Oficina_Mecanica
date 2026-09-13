package com.example.Projeto_Oficina_Mecanica.repository;

import com.example.Projeto_Oficina_Mecanica.entity.Cliente;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes de integração de {@link ClienteRepository} contra um banco H2 em
 * memória (via @DataJpaTest). Cobre só as queries customizadas — o CRUD
 * puro (save/findById/delete) já é testado pelo próprio Spring Data.
 *
 * ATENÇÃO: escrito sem acesso à entidade/repositório reais nesta sessão
 * (reconstrução a partir do que já foi confirmado em sessões anteriores:
 * ClienteRepository.existsByCpfCnpj(String) e
 * buscarComFiltros(String nome, String cpfCnpj, Boolean ativo, Pageable)).
 * Se os nomes dos campos da entidade Cliente diferirem, ajuste o builder
 * do @BeforeEach.
 */
@DataJpaTest
@DisplayName("ClienteRepository")
class ClienteRepositoryTest {

    @Autowired
    private ClienteRepository clienteRepository;

    private Cliente clienteJoao;
    private Cliente clienteMaria;

    @BeforeEach
    void setUp() {
        clienteRepository.deleteAll();

        clienteJoao = clienteRepository.save(
                Cliente.builder()
                        .nome("João da Silva")
                        .cpfCnpj("111.111.111-11")
                        .email("joao@teste.com")
                        .telefone("11999990000")
                        .ativo(true)
                        .build()
        );

        clienteMaria = clienteRepository.save(
                Cliente.builder()
                        .nome("Maria Souza")
                        .cpfCnpj("222.222.222-22")
                        .email("maria@teste.com")
                        .telefone("11999991111")
                        .ativo(false)
                        .build()
        );
    }

    @Nested
    @DisplayName("existsByCpfCnpj()")
    class ExistsByCpfCnpj {

        @Test
        @DisplayName("deve retornar true quando o CPF/CNPJ já está cadastrado")
        void deveRetornarTrue_quandoJaExiste() {
            assertThat(clienteRepository.existsByCpfCnpj("111.111.111-11")).isTrue();
        }

        @Test
        @DisplayName("deve retornar false quando o CPF/CNPJ não está cadastrado")
        void deveRetornarFalse_quandoNaoExiste() {
            assertThat(clienteRepository.existsByCpfCnpj("999.999.999-99")).isFalse();
        }
    }

    @Nested
    @DisplayName("buscarComFiltros()")
    class BuscarComFiltros {

        @Test
        @DisplayName("deve filtrar por nome (case-insensitive, parcial)")
        void deveFiltrarPorNome() {
            Page<Cliente> resultado = clienteRepository.buscarComFiltros("joão", null, null, Pageable.unpaged());

            assertThat(resultado.getContent()).containsExactly(clienteJoao);
        }

        @Test
        @DisplayName("deve filtrar por status ativo")
        void deveFiltrarPorAtivo() {
            Page<Cliente> resultado = clienteRepository.buscarComFiltros(null, null, true, Pageable.unpaged());

            assertThat(resultado.getContent()).containsExactly(clienteJoao);
        }

        @Test
        @DisplayName("deve retornar todos quando nenhum filtro é informado")
        void deveRetornarTodos_semFiltros() {
            Page<Cliente> resultado = clienteRepository.buscarComFiltros(null, null, null, PageRequest.of(0, 10));

            assertThat(resultado.getTotalElements()).isEqualTo(2);
        }

        @Test
        @DisplayName("deve retornar vazio quando nenhum cliente bate com o filtro")
        void deveRetornarVazio_semCorrespondencia() {
            Page<Cliente> resultado =
                    clienteRepository.buscarComFiltros("Inexistente", null, null, Pageable.unpaged());

            assertThat(resultado.getContent()).isEmpty();
        }
    }
}

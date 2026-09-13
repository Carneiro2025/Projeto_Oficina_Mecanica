package com.example.Projeto_Oficina_Mecanica.repository;

import com.example.Projeto_Oficina_Mecanica.entity.Cliente;
import com.example.Projeto_Oficina_Mecanica.entity.Veiculo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes de integração de {@link VeiculoRepository} contra H2 em memória.
 *
 * ATENÇÃO: escrito sem acesso à entidade/repositório reais nesta sessão —
 * reconstrução a partir do que já foi confirmado em sessões anteriores
 * (existsByPlaca, existsByChassi,
 * buscarComFiltros(placa, modelo, clienteId, ativo, Pageable)).
 */
@DataJpaTest
@DisplayName("VeiculoRepository")
class VeiculoRepositoryTest {

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    private Cliente cliente;
    private Veiculo veiculoOnix;

    @BeforeEach
    void setUp() {
        veiculoRepository.deleteAll();
        clienteRepository.deleteAll();

        cliente = clienteRepository.save(
                Cliente.builder()
                        .nome("João da Silva")
                        .cpfCnpj("111.111.111-11")
                        .ativo(true)
                        .build()
        );

        veiculoOnix = veiculoRepository.save(
                Veiculo.builder()
                        .cliente(cliente)
                        .placa("ABC1D23")
                        .chassi("9BWZZZ377VT004251")
                        .marca("Chevrolet")
                        .modelo("Onix")
                        .quilometragem(10000)
                        .ativo(true)
                        .build()
        );
    }

    @Nested
    @DisplayName("existsByPlaca() / existsByChassi()")
    class Existencia {

        @Test
        @DisplayName("deve retornar true quando a placa já está cadastrada")
        void devePlacaExistir() {
            assertThat(veiculoRepository.existsByPlaca("ABC1D23")).isTrue();
        }

        @Test
        @DisplayName("deve retornar false quando a placa não está cadastrada")
        void devePlacaNaoExistir() {
            assertThat(veiculoRepository.existsByPlaca("ZZZ9Z99")).isFalse();
        }

        @Test
        @DisplayName("deve retornar true quando o chassi já está cadastrado")
        void deveChassiExistir() {
            assertThat(veiculoRepository.existsByChassi("9BWZZZ377VT004251")).isTrue();
        }
    }

    @Nested
    @DisplayName("buscarComFiltros()")
    class BuscarComFiltros {

        @Test
        @DisplayName("deve filtrar por placa")
        void deveFiltrarPorPlaca() {
            Page<Veiculo> resultado =
                    veiculoRepository.buscarComFiltros("ABC1D23", null, null, null, Pageable.unpaged());

            assertThat(resultado.getContent()).containsExactly(veiculoOnix);
        }

        @Test
        @DisplayName("deve filtrar por cliente")
        void deveFiltrarPorCliente() {
            Page<Veiculo> resultado =
                    veiculoRepository.buscarComFiltros(null, null, cliente.getId(), null, Pageable.unpaged());

            assertThat(resultado.getContent()).containsExactly(veiculoOnix);
        }

        @Test
        @DisplayName("deve retornar vazio para um cliente sem veículos")
        void deveRetornarVazio_paraClienteSemVeiculos() {
            Page<Veiculo> resultado =
                    veiculoRepository.buscarComFiltros(null, null, 999L, null, Pageable.unpaged());

            assertThat(resultado.getContent()).isEmpty();
        }
    }
}

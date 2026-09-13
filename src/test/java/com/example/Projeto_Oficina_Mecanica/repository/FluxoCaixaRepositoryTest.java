package com.example.Projeto_Oficina_Mecanica.repository;

import com.example.Projeto_Oficina_Mecanica.entity.FluxoCaixa;
import com.example.Projeto_Oficina_Mecanica.enums.OrigemMovimentacaoCaixa;
import com.example.Projeto_Oficina_Mecanica.enums.TipoMovimentacaoCaixa;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes de integração de {@link FluxoCaixaRepository} contra H2 em memória.
 */
@DataJpaTest
@DisplayName("FluxoCaixaRepository")
class FluxoCaixaRepositoryTest {

    @Autowired
    private FluxoCaixaRepository fluxoCaixaRepository;

    @BeforeEach
    void setUp() {
        fluxoCaixaRepository.deleteAll();
    }

    private FluxoCaixa fluxoBase(TipoMovimentacaoCaixa tipo, BigDecimal valor, LocalDate data) {
        return FluxoCaixa.builder()
                .tipoMovimentacao(tipo)
                .origem(OrigemMovimentacaoCaixa.AJUSTE_MANUAL)
                .descricao("Movimentação de teste")
                .valor(valor)
                .saldoAtual(valor)
                .dataMovimentacao(data)
                .ativo(true)
                .build();
    }

    @Nested
    @DisplayName("findByTipoMovimentacao()")
    class FindByTipoMovimentacao {

        @Test
        @DisplayName("deve retornar apenas as movimentações do tipo informado")
        void deveRetornarApenasDoTipo() {
            fluxoCaixaRepository.save(fluxoBase(TipoMovimentacaoCaixa.ENTRADA, new BigDecimal("100.00"), LocalDate.now()));
            fluxoCaixaRepository.save(fluxoBase(TipoMovimentacaoCaixa.SAIDA, new BigDecimal("-50.00"), LocalDate.now()));

            List<FluxoCaixa> resultado = fluxoCaixaRepository.findByTipoMovimentacao(TipoMovimentacaoCaixa.ENTRADA);

            assertThat(resultado).hasSize(1);
        }
    }

    @Nested
    @DisplayName("findByDataMovimentacaoBetween()")
    class FindByDataMovimentacaoBetween {

        @Test
        @DisplayName("deve retornar apenas movimentações dentro do período")
        void deveRetornarApenasDoPeriodo() {
            LocalDate hoje = LocalDate.now();

            FluxoCaixa dentroDoPeriodo = fluxoCaixaRepository.save(
                    fluxoBase(TipoMovimentacaoCaixa.ENTRADA, new BigDecimal("100.00"), hoje.minusDays(2))
            );
            fluxoCaixaRepository.save(
                    fluxoBase(TipoMovimentacaoCaixa.ENTRADA, new BigDecimal("100.00"), hoje.minusDays(30))
            );

            List<FluxoCaixa> resultado = fluxoCaixaRepository
                    .findByDataMovimentacaoBetween(hoje.minusDays(7), hoje);

            assertThat(resultado).containsExactly(dentroDoPeriodo);
        }
    }

    @Nested
    @DisplayName("findTopByOrderByDataMovimentacaoDescIdDesc()")
    class FindTopByOrderByDataMovimentacaoDescIdDesc {

        @Test
        @DisplayName("deve retornar a movimentação mais recente")
        void deveRetornarAMaisRecente() {
            fluxoCaixaRepository.save(
                    fluxoBase(TipoMovimentacaoCaixa.ENTRADA, new BigDecimal("100.00"), LocalDate.now().minusDays(5))
            );
            FluxoCaixa maisRecente = fluxoCaixaRepository.save(
                    fluxoBase(TipoMovimentacaoCaixa.ENTRADA, new BigDecimal("200.00"), LocalDate.now())
            );

            Optional<FluxoCaixa> resultado = fluxoCaixaRepository.findTopByOrderByDataMovimentacaoDescIdDesc();

            assertThat(resultado).isPresent();
            assertThat(resultado.get().getId()).isEqualTo(maisRecente.getId());
        }

        @Test
        @DisplayName("deve retornar vazio quando não há nenhuma movimentação")
        void deveRetornarVazio_semMovimentacoes() {
            assertThat(fluxoCaixaRepository.findTopByOrderByDataMovimentacaoDescIdDesc()).isEmpty();
        }
    }
}

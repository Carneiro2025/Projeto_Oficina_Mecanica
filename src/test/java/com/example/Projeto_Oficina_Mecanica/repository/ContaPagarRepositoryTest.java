package com.example.Projeto_Oficina_Mecanica.repository;

import com.example.Projeto_Oficina_Mecanica.entity.ContaPagar;
import com.example.Projeto_Oficina_Mecanica.entity.Fornecedor;
import com.example.Projeto_Oficina_Mecanica.enums.StatusContaPagar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes de integração de {@link ContaPagarRepository} contra H2 em memória.
 */
@DataJpaTest
@DisplayName("ContaPagarRepository")
class ContaPagarRepositoryTest {

    @Autowired
    private ContaPagarRepository contaPagarRepository;

    @Autowired
    private FornecedorRepository fornecedorRepository;

    private Fornecedor fornecedor;

    @BeforeEach
    void setUp() {
        contaPagarRepository.deleteAll();
        fornecedorRepository.deleteAll();

        fornecedor = fornecedorRepository.save(
                Fornecedor.builder()
                        .razaoSocial("Auto Peças LTDA")
                        .cnpj("12.345.678/0001-90")
                        .ativo(true)
                        .build()
        );
    }

    private ContaPagar contaBase(StatusContaPagar status, LocalDate vencimento) {
        return ContaPagar.builder()
                .fornecedor(fornecedor)
                .descricao("Compra de peças")
                .valor(new BigDecimal("500.00"))
                .dataVencimento(vencimento)
                .status(status)
                .ativo(true)
                .build();
    }

    @Nested
    @DisplayName("findByStatus()")
    class FindByStatus {

        @Test
        @DisplayName("deve retornar apenas as contas do status informado")
        void deveRetornarApenasDoStatus() {
            contaPagarRepository.save(contaBase(StatusContaPagar.PENDENTE, LocalDate.now().plusDays(10)));
            contaPagarRepository.save(contaBase(StatusContaPagar.PAGO, LocalDate.now().minusDays(5)));

            List<ContaPagar> resultado = contaPagarRepository.findByStatus(StatusContaPagar.PENDENTE);

            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getStatus()).isEqualTo(StatusContaPagar.PENDENTE);
        }
    }

    @Nested
    @DisplayName("findByFornecedorId()")
    class FindByFornecedorId {

        @Test
        @DisplayName("deve retornar apenas as contas do fornecedor informado")
        void deveRetornarApenasDoFornecedor() {
            contaPagarRepository.save(contaBase(StatusContaPagar.PENDENTE, LocalDate.now()));

            Fornecedor outroFornecedor = fornecedorRepository.save(
                    Fornecedor.builder().razaoSocial("Outra LTDA").cnpj("99.999.999/0001-99").ativo(true).build()
            );
            contaPagarRepository.save(
                    ContaPagar.builder()
                            .fornecedor(outroFornecedor)
                            .descricao("Outra compra")
                            .valor(new BigDecimal("100.00"))
                            .dataVencimento(LocalDate.now())
                            .status(StatusContaPagar.PENDENTE)
                            .ativo(true)
                            .build()
            );

            List<ContaPagar> resultado = contaPagarRepository.findByFornecedorId(fornecedor.getId());

            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getDescricao()).isEqualTo("Compra de peças");
        }
    }

    @Nested
    @DisplayName("findByDataVencimentoBeforeAndStatusNot()")
    class FindByDataVencimentoBeforeAndStatusNot {

        @Test
        @DisplayName("deve retornar apenas contas vencidas e não pagas")
        void deveRetornarApenasVencidasNaoPagas() {
            ContaPagar vencidaPendente = contaPagarRepository.save(
                    contaBase(StatusContaPagar.PENDENTE, LocalDate.now().minusDays(5))
            );
            contaPagarRepository.save(contaBase(StatusContaPagar.PAGO, LocalDate.now().minusDays(5)));
            contaPagarRepository.save(contaBase(StatusContaPagar.PENDENTE, LocalDate.now().plusDays(10)));

            List<ContaPagar> resultado = contaPagarRepository
                    .findByDataVencimentoBeforeAndStatusNot(LocalDate.now(), StatusContaPagar.PAGO);

            assertThat(resultado).containsExactly(vencidaPendente);
        }
    }
}

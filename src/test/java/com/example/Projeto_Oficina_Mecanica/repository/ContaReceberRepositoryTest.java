package com.example.Projeto_Oficina_Mecanica.repository;

import com.example.Projeto_Oficina_Mecanica.entity.Cliente;
import com.example.Projeto_Oficina_Mecanica.entity.ContaReceber;
import com.example.Projeto_Oficina_Mecanica.enums.StatusContaReceber;

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
 * Testes de integração de {@link ContaReceberRepository} contra H2 em memória.
 */
@DataJpaTest
@DisplayName("ContaReceberRepository")
class ContaReceberRepositoryTest {

    @Autowired
    private ContaReceberRepository contaReceberRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        contaReceberRepository.deleteAll();
        clienteRepository.deleteAll();

        cliente = clienteRepository.save(
                Cliente.builder().nome("João da Silva").cpfCnpj("111.111.111-11").ativo(true).build()
        );
    }

    private ContaReceber contaBase(StatusContaReceber status, LocalDate vencimento) {
        return ContaReceber.builder()
                .cliente(cliente)
                .valor(new BigDecimal("350.00"))
                .dataVencimento(vencimento)
                .status(status)
                .build();
    }

    @Nested
    @DisplayName("findByStatus()")
    class FindByStatus {

        @Test
        @DisplayName("deve retornar apenas as contas do status informado")
        void deveRetornarApenasDoStatus() {
            contaReceberRepository.save(contaBase(StatusContaReceber.PENDENTE, LocalDate.now().plusDays(10)));
            contaReceberRepository.save(contaBase(StatusContaReceber.PAGO, LocalDate.now().minusDays(5)));

            List<ContaReceber> resultado = contaReceberRepository.findByStatus(StatusContaReceber.PENDENTE);

            assertThat(resultado).hasSize(1);
        }
    }

    @Nested
    @DisplayName("findByClienteId()")
    class FindByClienteId {

        @Test
        @DisplayName("deve retornar apenas as contas do cliente informado")
        void deveRetornarApenasDoCliente() {
            contaReceberRepository.save(contaBase(StatusContaReceber.PENDENTE, LocalDate.now()));

            Cliente outroCliente = clienteRepository.save(
                    Cliente.builder().nome("Maria Souza").cpfCnpj("222.222.222-22").ativo(true).build()
            );
            contaReceberRepository.save(
                    ContaReceber.builder()
                            .cliente(outroCliente)
                            .valor(new BigDecimal("100.00"))
                            .dataVencimento(LocalDate.now())
                            .status(StatusContaReceber.PENDENTE)
                            .build()
            );

            List<ContaReceber> resultado = contaReceberRepository.findByClienteId(cliente.getId());

            assertThat(resultado).hasSize(1);
        }
    }

    @Nested
    @DisplayName("findByDataVencimentoBeforeAndStatusNot()")
    class FindByDataVencimentoBeforeAndStatusNot {

        @Test
        @DisplayName("deve retornar apenas contas vencidas e não pagas")
        void deveRetornarApenasVencidasNaoPagas() {
            ContaReceber vencidaPendente = contaReceberRepository.save(
                    contaBase(StatusContaReceber.PENDENTE, LocalDate.now().minusDays(3))
            );
            contaReceberRepository.save(contaBase(StatusContaReceber.PAGO, LocalDate.now().minusDays(3)));

            List<ContaReceber> resultado = contaReceberRepository
                    .findByDataVencimentoBeforeAndStatusNot(LocalDate.now(), StatusContaReceber.PAGO);

            assertThat(resultado).containsExactly(vencidaPendente);
        }
    }
}

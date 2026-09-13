package com.example.Projeto_Oficina_Mecanica.repository;

import com.example.Projeto_Oficina_Mecanica.entity.Fornecedor;
import com.example.Projeto_Oficina_Mecanica.entity.NotaFiscalEntrada;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes de integração de {@link NotaFiscalEntradaRepository} contra H2 em
 * memória.
 */
@DataJpaTest
@DisplayName("NotaFiscalEntradaRepository")
class NotaFiscalEntradaRepositoryTest {

    @Autowired
    private NotaFiscalEntradaRepository notaFiscalEntradaRepository;

    @Autowired
    private FornecedorRepository fornecedorRepository;

    private Fornecedor fornecedor;

    @BeforeEach
    void setUp() {
        notaFiscalEntradaRepository.deleteAll();
        fornecedorRepository.deleteAll();

        fornecedor = fornecedorRepository.save(
                Fornecedor.builder()
                        .razaoSocial("Auto Peças LTDA")
                        .cnpj("12.345.678/0001-90")
                        .ativo(true)
                        .build()
        );
    }

    @Nested
    @DisplayName("existsByNumero()")
    class ExistsByNumero {

        @Test
        @DisplayName("deve retornar true quando o número já está cadastrado")
        void deveRetornarTrue_quandoJaExiste() {
            notaFiscalEntradaRepository.save(notaBase("NF-001"));

            assertThat(notaFiscalEntradaRepository.existsByNumero("NF-001")).isTrue();
        }

        @Test
        @DisplayName("deve retornar false quando o número não está cadastrado")
        void deveRetornarFalse_quandoNaoExiste() {
            assertThat(notaFiscalEntradaRepository.existsByNumero("NF-999")).isFalse();
        }
    }

    @Nested
    @DisplayName("findByFornecedorId()")
    class FindByFornecedorId {

        @Test
        @DisplayName("deve retornar apenas as notas do fornecedor informado")
        void deveRetornarApenasDoFornecedor() {
            notaFiscalEntradaRepository.save(notaBase("NF-001"));

            Fornecedor outroFornecedor = fornecedorRepository.save(
                    Fornecedor.builder().razaoSocial("Outra LTDA").cnpj("99.999.999/0001-99").ativo(true).build()
            );
            notaFiscalEntradaRepository.save(
                    NotaFiscalEntrada.builder()
                            .numero("NF-002")
                            .fornecedor(outroFornecedor)
                            .dataEmissao(LocalDate.now())
                            .valorTotal(new BigDecimal("80.00"))
                            .build()
            );

            Page<NotaFiscalEntrada> resultado =
                    notaFiscalEntradaRepository.findByFornecedorId(fornecedor.getId(), PageRequest.of(0, 10));

            assertThat(resultado.getContent()).hasSize(1);
            assertThat(resultado.getContent().get(0).getNumero()).isEqualTo("NF-001");
        }
    }

    private NotaFiscalEntrada notaBase(String numero) {
        return NotaFiscalEntrada.builder()
                .numero(numero)
                .fornecedor(fornecedor)
                .dataEmissao(LocalDate.now())
                .valorTotal(new BigDecimal("80.00"))
                .build();
    }
}

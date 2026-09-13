package com.example.Projeto_Oficina_Mecanica.repository;

import com.example.Projeto_Oficina_Mecanica.entity.Cliente;
import com.example.Projeto_Oficina_Mecanica.entity.OrdemServico;
import com.example.Projeto_Oficina_Mecanica.entity.Veiculo;
import com.example.Projeto_Oficina_Mecanica.enums.StatusOrdemServico;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes de integração de {@link OrdemServicoRepository} contra H2 em
 * memória. Cobre só existsByNumero/countByStatus/findByClienteId/
 * findByStatus — não testei buscarComFiltros(7 params) porque não tive
 * como confirmar a ordem/tipo exatos dos 7 parâmetros nesta sessão sem
 * o arquivo real; se quiser esse também, me manda o repositório.
 *
 * ATENÇÃO: escrito sem acesso à entidade/repositório reais nesta sessão.
 */
@DataJpaTest
@DisplayName("OrdemServicoRepository")
class OrdemServicoRepositoryTest {

    @Autowired
    private OrdemServicoRepository ordemServicoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private VeiculoRepository veiculoRepository;

    private Cliente cliente;
    private Veiculo veiculo;

    @BeforeEach
    void setUp() {
        ordemServicoRepository.deleteAll();
        veiculoRepository.deleteAll();
        clienteRepository.deleteAll();

        cliente = clienteRepository.save(
                Cliente.builder().nome("João da Silva").cpfCnpj("111.111.111-11").ativo(true).build()
        );

        veiculo = veiculoRepository.save(
                Veiculo.builder().cliente(cliente).placa("ABC1D23").marca("Chevrolet").modelo("Onix").ativo(true).build()
        );
    }

    private OrdemServico osBase(String numero, StatusOrdemServico status) {
        return OrdemServico.builder()
                .numero(numero)
                .cliente(cliente)
                .veiculo(veiculo)
                .status(status)
                .ativo(true)
                .itens(new ArrayList<>())
                .build();
    }

    @Nested
    @DisplayName("existsByNumero()")
    class ExistsByNumero {

        @Test
        @DisplayName("deve retornar true quando o número já está cadastrado")
        void deveRetornarTrue_quandoJaExiste() {
            ordemServicoRepository.save(osBase("OS-001", StatusOrdemServico.ABERTA));

            assertThat(ordemServicoRepository.existsByNumero("OS-001")).isTrue();
        }

        @Test
        @DisplayName("deve retornar false quando o número não está cadastrado")
        void deveRetornarFalse_quandoNaoExiste() {
            assertThat(ordemServicoRepository.existsByNumero("OS-999")).isFalse();
        }
    }

    @Nested
    @DisplayName("countByStatus()")
    class CountByStatus {

        @Test
        @DisplayName("deve contar apenas as OS do status informado")
        void deveContarApenasDoStatus() {
            ordemServicoRepository.save(osBase("OS-001", StatusOrdemServico.ABERTA));
            ordemServicoRepository.save(osBase("OS-002", StatusOrdemServico.ABERTA));
            ordemServicoRepository.save(osBase("OS-003", StatusOrdemServico.FINALIZADA));

            assertThat(ordemServicoRepository.countByStatus(StatusOrdemServico.ABERTA)).isEqualTo(2L);
            assertThat(ordemServicoRepository.countByStatus(StatusOrdemServico.FINALIZADA)).isEqualTo(1L);
            assertThat(ordemServicoRepository.countByStatus(StatusOrdemServico.CANCELADA)).isZero();
        }
    }

    @Nested
    @DisplayName("findByClienteId()")
    class FindByClienteId {

        @Test
        @DisplayName("deve retornar apenas as OS do cliente informado")
        void deveRetornarApenasDoCliente() {
            ordemServicoRepository.save(osBase("OS-001", StatusOrdemServico.ABERTA));

            Cliente outroCliente = clienteRepository.save(
                    Cliente.builder().nome("Maria Souza").cpfCnpj("222.222.222-22").ativo(true).build()
            );
            Veiculo outroVeiculo = veiculoRepository.save(
                    Veiculo.builder().cliente(outroCliente).placa("XYZ9W88").marca("Fiat").modelo("Uno").ativo(true).build()
            );
            ordemServicoRepository.save(
                    OrdemServico.builder()
                            .numero("OS-002")
                            .cliente(outroCliente)
                            .veiculo(outroVeiculo)
                            .status(StatusOrdemServico.ABERTA)
                            .ativo(true)
                            .itens(new ArrayList<>())
                            .build()
            );

            Page<OrdemServico> resultado =
                    ordemServicoRepository.findByClienteId(cliente.getId(), PageRequest.of(0, 10));

            assertThat(resultado.getContent()).hasSize(1);
            assertThat(resultado.getContent().get(0).getNumero()).isEqualTo("OS-001");
        }
    }

    @Nested
    @DisplayName("findByStatus()")
    class FindByStatus {

        @Test
        @DisplayName("deve retornar apenas as OS do status informado")
        void deveRetornarApenasDoStatus() {
            ordemServicoRepository.save(osBase("OS-001", StatusOrdemServico.FINALIZADA));
            ordemServicoRepository.save(osBase("OS-002", StatusOrdemServico.ABERTA));

            Page<OrdemServico> resultado =
                    ordemServicoRepository.findByStatus(StatusOrdemServico.FINALIZADA, PageRequest.of(0, 10));

            assertThat(resultado.getContent()).hasSize(1);
            assertThat(resultado.getContent().get(0).getNumero()).isEqualTo("OS-001");
        }
    }

    @Nested
@DisplayName("buscarComFiltros()")
class BuscarComFiltros {

    @Test
    @DisplayName("deve filtrar por número")
    void deveFiltrarPorNumero() {
        ordemServicoRepository.save(osBase("OS-001", StatusOrdemServico.ABERTA));

        Page<OrdemServico> resultado = ordemServicoRepository.buscarComFiltros(
                "OS-001", null, null, null, null, null, Pageable.unpaged()
        );

        assertThat(resultado.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("deve filtrar por status")
    void deveFiltrarPorStatus() {
        ordemServicoRepository.save(osBase("OS-001", StatusOrdemServico.ABERTA));
        ordemServicoRepository.save(osBase("OS-002", StatusOrdemServico.FINALIZADA));

        Page<OrdemServico> resultado = ordemServicoRepository.buscarComFiltros(
                null, StatusOrdemServico.FINALIZADA, null, null, null, null, Pageable.unpaged()
        );

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getNumero()).isEqualTo("OS-002");
    }

    @Test
    @DisplayName("deve filtrar por intervalo de data de abertura")
    void deveFiltrarPorIntervaloDeData() {
        OrdemServico os = osBase("OS-001", StatusOrdemServico.ABERTA);
        os.setDataAbertura(java.time.LocalDate.now().minusDays(10));
        ordemServicoRepository.save(os);

        Page<OrdemServico> dentroDoIntervalo = ordemServicoRepository.buscarComFiltros(
                null, null, null, null,
                java.time.LocalDate.now().minusDays(15),
                java.time.LocalDate.now().minusDays(5),
                Pageable.unpaged()
        );
        Page<OrdemServico> foraDoIntervalo = ordemServicoRepository.buscarComFiltros(
                null, null, null, null,
                java.time.LocalDate.now().plusDays(1),
                java.time.LocalDate.now().plusDays(5),
                Pageable.unpaged()
        );

        assertThat(dentroDoIntervalo.getContent()).hasSize(1);
        assertThat(foraDoIntervalo.getContent()).isEmpty();
    }

}
<<<<<<< HEAD

}
=======
>>>>>>> origin/main

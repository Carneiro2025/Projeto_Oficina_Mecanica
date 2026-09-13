package com.example.Projeto_Oficina_Mecanica.service.impl;

import com.example.Projeto_Oficina_Mecanica.dto.RelatorioClienteDTO;
import com.example.Projeto_Oficina_Mecanica.dto.RelatorioFinanceiroDTO;
import com.example.Projeto_Oficina_Mecanica.entity.Cliente;
import com.example.Projeto_Oficina_Mecanica.entity.ContaPagar;
import com.example.Projeto_Oficina_Mecanica.entity.ContaReceber;
import com.example.Projeto_Oficina_Mecanica.entity.FluxoCaixa;
import com.example.Projeto_Oficina_Mecanica.entity.OrdemServico;
import com.example.Projeto_Oficina_Mecanica.entity.Veiculo;
import com.example.Projeto_Oficina_Mecanica.enums.StatusContaPagar;
import com.example.Projeto_Oficina_Mecanica.enums.StatusContaReceber;
import com.example.Projeto_Oficina_Mecanica.enums.TipoMovimentacaoCaixa;
import com.example.Projeto_Oficina_Mecanica.repository.ClienteRepository;
import com.example.Projeto_Oficina_Mecanica.repository.ContaPagarRepository;
import com.example.Projeto_Oficina_Mecanica.repository.ContaReceberRepository;
import com.example.Projeto_Oficina_Mecanica.repository.FluxoCaixaRepository;
import com.example.Projeto_Oficina_Mecanica.repository.OrdemServicoRepository;
import com.example.Projeto_Oficina_Mecanica.repository.ProdutoRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Testes unitários de {@link RelatorioServiceImpl}, focados nos dois pontos
 * corrigidos na Fase 4 da sprint: relatorioFinanceiro() e relatorioClientes().
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RelatorioServiceImpl")
class RelatorioServiceImplTest {

    @Mock
    private OrdemServicoRepository ordemServicoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private ContaPagarRepository contaPagarRepository;

    @Mock
    private ContaReceberRepository contaReceberRepository;

    @Mock
    private FluxoCaixaRepository fluxoCaixaRepository;

    @InjectMocks
    private RelatorioServiceImpl relatorioService;

    @Nested
    @DisplayName("relatorioFinanceiro()")
    class Financeiro {

        @Test
        @DisplayName("deve calcular receitas e despesas a partir do fluxo de caixa, não do valorTotal das OS")
        void deveCalcularComBaseNoFluxoDeCaixa() {
            FluxoCaixa entrada = FluxoCaixa.builder()
                    .tipoMovimentacao(TipoMovimentacaoCaixa.ENTRADA)
                    .valor(new BigDecimal("1000.00"))
                    .build();

            FluxoCaixa saida = FluxoCaixa.builder()
                    .tipoMovimentacao(TipoMovimentacaoCaixa.SAIDA)
                    .valor(new BigDecimal("-400.00"))
                    .build();

            when(fluxoCaixaRepository.findAll()).thenReturn(List.of(entrada, saida));
            when(contaReceberRepository.findByStatus(StatusContaReceber.PAGO))
                    .thenReturn(List.of(new ContaReceber(), new ContaReceber()));
            when(contaPagarRepository.findByStatus(StatusContaPagar.PAGO))
                    .thenReturn(List.of(new ContaPagar()));

            RelatorioFinanceiroDTO resultado = relatorioService.relatorioFinanceiro();

            assertThat(resultado.getTotalReceitas()).isEqualByComparingTo("1000.00");
            assertThat(resultado.getTotalDespesas()).isEqualByComparingTo("400.00");
            assertThat(resultado.getLucro()).isEqualByComparingTo("600.00");
            assertThat(resultado.getQuantidadeRecebimentos()).isEqualTo(2);
            assertThat(resultado.getQuantidadePagamentos()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("relatorioClientes()")
    class RelatorioClientes {

        @Test
        @DisplayName("deve contar corretamente as ordens de serviço de cada cliente")
        void deveContarOrdensDeServicoPorCliente() {
            Cliente cliente1 = Cliente.builder().id(1L).nome("João").veiculos(List.of(new Veiculo())).build();
            Cliente cliente2 = Cliente.builder().id(2L).nome("Maria").veiculos(List.of()).build();

            OrdemServico os1 = OrdemServico.builder().cliente(cliente1).build();
            OrdemServico os2 = OrdemServico.builder().cliente(cliente1).build();
            OrdemServico os3 = OrdemServico.builder().cliente(cliente2).build();

            when(clienteRepository.findAll()).thenReturn(List.of(cliente1, cliente2));
            when(ordemServicoRepository.findAll()).thenReturn(List.of(os1, os2, os3));

            List<RelatorioClienteDTO> resultado = relatorioService.relatorioClientes();

            RelatorioClienteDTO dtoCliente1 = resultado.stream()
                    .filter(d -> d.getClienteId().equals(1L)).findFirst().orElseThrow();
            RelatorioClienteDTO dtoCliente2 = resultado.stream()
                    .filter(d -> d.getClienteId().equals(2L)).findFirst().orElseThrow();

            assertThat(dtoCliente1.getQuantidadeOrdensServico()).isEqualTo(2);
            assertThat(dtoCliente2.getQuantidadeOrdensServico()).isEqualTo(1);
        }

        @Test
        @DisplayName("deve retornar zero ordens para um cliente sem nenhuma OS")
        void deveRetornarZero_quandoClienteSemOS() {
            Cliente cliente = Cliente.builder().id(1L).nome("João").veiculos(List.of()).build();

            when(clienteRepository.findAll()).thenReturn(List.of(cliente));
            when(ordemServicoRepository.findAll()).thenReturn(List.of());

            List<RelatorioClienteDTO> resultado = relatorioService.relatorioClientes();

            assertThat(resultado.get(0).getQuantidadeOrdensServico()).isEqualTo(0);
        }
    }
}

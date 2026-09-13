package com.example.Projeto_Oficina_Mecanica.service.impl;

import com.example.Projeto_Oficina_Mecanica.dto.RelatorioClienteDTO;
import com.example.Projeto_Oficina_Mecanica.dto.RelatorioEstoqueDTO;
import com.example.Projeto_Oficina_Mecanica.dto.RelatorioFinanceiroDTO;
import com.example.Projeto_Oficina_Mecanica.dto.RelatorioOSDTO;
import com.example.Projeto_Oficina_Mecanica.entity.Cliente;
import com.example.Projeto_Oficina_Mecanica.entity.FluxoCaixa;
import com.example.Projeto_Oficina_Mecanica.entity.OrdemServico;
import com.example.Projeto_Oficina_Mecanica.entity.Produto;
import com.example.Projeto_Oficina_Mecanica.enums.StatusContaPagar;
import com.example.Projeto_Oficina_Mecanica.enums.StatusContaReceber;
import com.example.Projeto_Oficina_Mecanica.enums.TipoMovimentacaoCaixa;
import com.example.Projeto_Oficina_Mecanica.repository.ClienteRepository;
import com.example.Projeto_Oficina_Mecanica.repository.ContaPagarRepository;
import com.example.Projeto_Oficina_Mecanica.repository.ContaReceberRepository;
import com.example.Projeto_Oficina_Mecanica.repository.FluxoCaixaRepository;
import com.example.Projeto_Oficina_Mecanica.repository.OrdemServicoRepository;
import com.example.Projeto_Oficina_Mecanica.repository.ProdutoRepository;
import com.example.Projeto_Oficina_Mecanica.service.RelatorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ATENÇÃO: relatorioFinanceiro() e relatorioClientes() foram corrigidos
 * nesta sessão (Fase 4 da sprint) — o resto do arquivo é o
 * RelatorioServiceImpl real do projeto, que já estava correto.
 *
 * Problemas encontrados:
 * - relatorioFinanceiro() somava o valorTotal de TODAS as OS como "receita",
 *   mesmo as que nunca foram pagas, e totalDespesas/quantidadePagamentos
 *   estavam sempre zerados (hardcoded). Agora usa o FluxoCaixa (dinheiro que
 *   de fato entrou/saiu) e conta os pagamentos/recebimentos reais.
 * - relatorioClientes() tinha quantidadeOrdensServico sempre zerado
 *   (hardcoded). Agora conta de verdade, sem N+1 (uma única leitura de
 *   todas as OS, agrupadas por cliente).
 */
@Service
@RequiredArgsConstructor
public class RelatorioServiceImpl implements RelatorioService {

    private final OrdemServicoRepository ordemServicoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final ContaPagarRepository contaPagarRepository;
    private final ContaReceberRepository contaReceberRepository;
    private final FluxoCaixaRepository fluxoCaixaRepository;

    @Override
    public List<RelatorioOSDTO> relatorioOrdensServico() {

        return ordemServicoRepository.findAll()
                .stream()
                .map(this::converterOS)
                .toList();
    }

    @Override
    public RelatorioFinanceiroDTO relatorioFinanceiro() {

        List<FluxoCaixa> movimentacoes = fluxoCaixaRepository.findAll();

        BigDecimal totalReceitas = movimentacoes.stream()
                .filter(f -> f.getTipoMovimentacao() == TipoMovimentacaoCaixa.ENTRADA)
                .map(FluxoCaixa::getValor)
                .map(BigDecimal::abs)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDespesas = movimentacoes.stream()
                .filter(f -> f.getTipoMovimentacao() == TipoMovimentacaoCaixa.SAIDA)
                .map(FluxoCaixa::getValor)
                .map(BigDecimal::abs)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int quantidadeRecebimentos = (int) contaReceberRepository
                .findByStatus(StatusContaReceber.PAGO)
                .size();

        int quantidadePagamentos = (int) contaPagarRepository
                .findByStatus(StatusContaPagar.PAGO)
                .size();

        return RelatorioFinanceiroDTO.builder()
                .totalReceitas(totalReceitas)
                .totalDespesas(totalDespesas)
                .lucro(totalReceitas.subtract(totalDespesas))
                .quantidadeRecebimentos(quantidadeRecebimentos)
                .quantidadePagamentos(quantidadePagamentos)
                .build();
    }

    @Override
    public List<RelatorioEstoqueDTO> relatorioEstoque() {

        return produtoRepository.findAll()
                .stream()
                .map(this::converterProduto)
                .toList();
    }

    @Override
    public List<RelatorioClienteDTO> relatorioClientes() {

        Map<Long, Long> ordensPorCliente = ordemServicoRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        os -> os.getCliente().getId(),
                        Collectors.counting()
                ));

        return clienteRepository.findAll()
                .stream()
                .map(cliente -> converterCliente(cliente, ordensPorCliente))
                .toList();
    }

    // ==========================
    // MÉTODOS AUXILIARES
    // ==========================

    private RelatorioOSDTO converterOS(OrdemServico os) {

        return RelatorioOSDTO.builder()
                .numero(os.getNumero())
                .cliente(os.getCliente().getNome())
                .veiculo(os.getVeiculo().getMarca() + " " + os.getVeiculo().getModelo())
                .mecanico(os.getMecanicoResponsavel() != null ? os.getMecanicoResponsavel() : "")
                .status(os.getStatus().name())
                .dataAbertura(os.getDataAbertura())
                .dataConclusao(os.getDataConclusao())
                .valorTotal(os.getValorTotal())
                .build();
    }

    private RelatorioEstoqueDTO converterProduto(Produto produto) {

        return RelatorioEstoqueDTO.builder()
                .codigoProduto(produto.getCodigo())
                .descricao(produto.getDescricao())
                .estoqueAtual(produto.getEstoqueAtual())
                .estoqueMinimo(produto.getEstoqueMinimo())
                .abaixoMinimo(produto.isEstoqueAbaixoMinimo())
                .build();
    }

    private RelatorioClienteDTO converterCliente(Cliente cliente, Map<Long, Long> ordensPorCliente) {

        return RelatorioClienteDTO.builder()
                .clienteId(cliente.getId())
                .nome(cliente.getNome())
                .telefone(cliente.getTelefone())
                .email(cliente.getEmail())
                .quantidadeVeiculos(cliente.getVeiculos().size())
                .quantidadeOrdensServico(
                        ordensPorCliente.getOrDefault(cliente.getId(), 0L).intValue()
                )
                .build();
    }
}
